# What is MvvmBase?
MvvmBase is an MVVM framework to use with Jetpack Compose (and Data Binding for compatibility reasons). It is written in Kotlin and best used with it.

# Setup

MvvmBase is available via Maven Central. To use it put this in your `build.gradle`:

```gradle
dependencies {
    def mvvmbaseVersion = "3.0.1"

    [...]
    implementation de.trbnb:mvvmbase-core:$mvvmbaseVersion"

    // Data Binding compatibility extensions
    implementation de.trbnb:mvvmbase-databinding:$mvvmbaseVersion"

    // RxJava 2 extensions for Data Binding
    implementation de.trbnb:mvvmbase-rxjava2:$mvvmbaseVersion"
    
    // RxJava 3 extensions for Data Binding
    implementation "de.trbnb:mvvmbase-rxjava3:$mvvmbaseVersion"

    // Coroutines extensions for Data Binding
    implementation "de.trbnb:mvvmbase-coroutines:$mvvmbaseVersion"

    // Conductor support for Data Binding
    implementation "de.trbnb:mvvmbase-conductor:$mvvmbaseVersion"
}
```

# Usage

ViewModels can simply be declared like this:

```kotlin
class MyViewModel : BaseViewModel()
```

The `BaseViewModel` implements the `ViewModel` interface which has these major features:

- `notifyPropertyChanged(String)`/`observeAsState()`
- observable properties
- an event channel
- commands

## `notifyPropertyChanged(String)`/`observeAsState()`

To notify observers that the values of properties have changed they can be notified by calling `notifyPropertyChanged` with the propertys name or alternatively a property reference.

To observe a specific property as a Compose state `observeAsState` can be called:

```kotlin
viewModel::isLoading.observeAsState()
viewModel::textInput.observeAsMutableState()
```

## Observable properties

To write observable properties without having to call `notifyPropertyChanged` all the time one can use `observable()` as delegate property. Alternatively getter-only properties can be annotated with `DependsOn` to specify when it may have changed depending on other properties.

```kotlin
class MyViewModel : BaseViewModel() {
    var isLoading by observable(false)
  
    @DependsOn("isLoading")
    val showItems: Boolean
        get() = !isLoading
}
```

### `ObservableProperty` extensions

The ObservableProperty also can be customized to react to value changes, etc. These extensions can be used in a builder pattern way like this:

```kotlin
var foo by observable("")
    .distinct()
    .beforeSet { old, new ->
        Log.d(TAG, "Value is about to be changed from $old to $new")
    }
    .validate { old, new ->
        if (new.length > old.length) new else old
    }
    .afterSet { old, new ->
        Log.d(TAG, "Value changed: $new")
    }
```

This is what those extensions do:

* `distinct`  
  If a new value is about to be set it will check if the new value is referentially equal to the old one. If this is the case nothing further happens, `notifyPropertyChanged` will not be called and none of the extensions be invoked.

* `beforeSet`  
  Will be called if a new value is about to be set. The old and the new value will be passed as parameters. Will not be invoked if `distinct` has an effect.

* `validate`  
  Will be called if a new value is about to be set. The old and the new value will be passed as parameters and a return value that will then be set as actual value is expected. Will not be invoked if `distinct` has an effect.

* `afterSet`  
  Will be called after a new value is set. The previous and new values will be passed as parameter.


### Saving state

Jetpack libraries provide a way to save state of ViewModels via a `SavedStateHandle`. An implementation for that is provided as previously mentioned with `BaseStateSavingViewModel`. It contains a getter property for that handle that will have to be passed as constructor parameter.

`ObservableProperty` also support the handle. By default the library tries to automatically derive a key from the property name for supported types. A key can also be defined manually or saving state can be disabled.

Example:

```kotlin
// defaults to StateSaveOption.Automatic for supported types (see below), otherwise StateSaveOption.None
var text1 by observable("")

// key is derived automatically
var text2 by observable("", stateSaveOption = StateSaveOption.Automatic)

// key is specified manually
var text3 by observable("", stateSaveOption = StateSaveOption.Manual("progress"))

// no state saving for this property
var text4 by observable("", stateSaveOption = StateSaveOption.None)
```

#### Supported types

As mentioned above StateSaveOption.Automatic is applied implicitly to most types. This includes all primitive types (unsigned ones too) and the following:
- `BooleanArray`
- `ByteArray`
- `CharArray`
- `CharSequence`
- `Array<CharSequence>`
- `DoubleArray`
- `FloatArray`
- `IntArray`
- `LongArray`
- `ShortArray`
- `String`
- `Array<String>`
- `Binder`
- `Bundle`
- `Parcelable`
- `Array<Parcelable>`
- `Serializable` (includes wrapper types for primitives)
- `Size` (only API 21+)
- `SizeF` (only API 21+)

Note that this excludes `ArrayList` and `SparseArray` which _are_ supported by `SavedStateHandle` but that is dependent on the type they are containing. Type erasure makes it dificult to check for this so the default for those types is `StateSaveOption.None` (`StateSaveOption.Automatic` can still be applied manually for the right types).


## Event channel

Every `ViewModel` has an `EventChannel`. This can be used to transfer information to the view that are not state (e.g. showing a temporary error as toast).

```kotlin
class MyViewModel : BaseViewModel() {
    fun somethingHappened() {
        eventChannel(ToastEvent("Error message!"))
    }
}

class ToastEvent(val message: String) : Event
```

Because of lifecycles it can happen that events are called when no listener is registered. By default these events are kept in memory until a listener is registered. That listener will then be called with all those events in the same order in which they were raised.  
This behavior can be turned off in the `BaseViewModel` by overriding `memorizeNotReceivedEvents` to let it return `false`.

## Commands

Commands have two major functions.

1. They can be invoked.
2. They can be enabled. If it is not enabled but still invoked it will throw an Exception.

Commands can also take a parameter and return a value. A parameter-less command should use `Unit` as parameter type.

Commands are mainly used when a user has to interact with the UI and the ViewModel has to react to that. For that a Command is bound to a certain type of event of a UI element.

### Examples

The first step is to create a Command in your ViewModel. Since `Command` is only an interface you have to choose which implementation you want to use. This library includes two from the start (you can of course also write your own implementation):
- `SimpleCommand`  
  The SimpleCommand lets you modify the enabled-state simply setting a boolean value. It also has an additional constructor that lets you define that initial enabled-state.
- `RuleCommand`  
  The RuleCommands enabled-state is defined by a function returning a Boolean. That function is invoked during construction and whenever `onEnabledChanged` is called or if dependency properties are specified those have changed.

Both commands let you pass a function in the constructor and come with a helper function to create a parameter-less instance without specifying `Unit` as parameter type.
This function will then be invoked when the Command is invoked.

```kotlin
class MyViewModel : BaseViewModel() {
    val isLoading by observable(false)
  
    val fetchCommand = ruleCommand(
      enabledRule = { !isLoading },
      action = {/*do some fetching */},
      dependencyProperties = listOf(::isLoading)
    )
  
    val someSimpleCommand = simpleCommand(isEnabled = false) {
      // do some stuff
    }
  
    fun function() {
      someSimpleCommand.isEnabled = true
    }
}
```

The enabled state of a Command can be observed as a Compose state like this:

```kotlin
viewModel.command::isEnabled.observeAsState()
```

## Other `ViewModel` features

### `onDestroy`

Will be called when the instance is about to be destroyed. This should be used to clear references to avoid memory leaks.

### Lifecycle

The ViewModel also implements LifecycleOwner which allows LiveData, rx.Observable, etc. to cancel listeners, subscriptions and others automatically.

Its state is:
- After initialization & being unbound: `Lifecycle.State.RESUMED`.
- After being destroyed: `Lifecycle.State.DESTROYED`.

### Nested ViewModels

Sometimes it can be useful to have multiple ViewModels inside another ViewModel (ViewModels for list items e.g.). In this case the "child" ViewModels must be destroyed when the "parent" is destroyed. For this purpose the function `autoDestroy()` can be used.

It can also be quite handy to send events from a "child" ViewModel and to have the "parent" emit them as well. This can be achieved via `bindEvents()`.

Example:

```kotlin
class MainViewModel : BaseViewModel() {
    var items by bindable<List<ViewModel>>(emptyList())
        .beforeSet { old, new ->
            old.destroyAll()
            new.autoDestroy().bindEvents()
        }

    fun fetch() {
        items = loadData()
            .map { /* map data to ItemViewModel */ }
    }
}
```

Because this can be common and tedious `.asChildren()` can be used instead:

```kotlin
class MainViewModel : BaseViewModel() {
    var items by observable<List<ViewModel>>(emptyList())
        .asChildren()

    fun fetch() {
        items = loadData()
            .map { /* map data to ItemViewModel */ }
    }
}
```

Note however that this does override `beforeSet` so `asChildren` takes an action for `beforeSet`.

# DataBinding compatibility

Previous versions of the library were meant to be used with DataBinding. The documentation for the compatibility modules is [here](./README-DataBinding.md).