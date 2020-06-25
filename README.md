# What is MvvmBase?
MvvmBase is a library to help work with data binding and the MVVM pattern on Android. It is build to extend the official [Android Data Binding tools](https://developer.android.com/topic/libraries/data-binding/). It is written in Kotlin and best used with it.

# Setup

MvvmBase is available via JCenter. To use it put this in your `build.gradle`:

```gradle
dependencies {
    // Always needed, library depends on ViewModelLazy
    implementation "androidx.lifecycle:lifecycle-viewmodel-ktx:2.2.0"
    // Necessary for SavedStateHandle
    implementation "androidx.lifecycle:lifecycle-viewmodel-savedstate:2.2.0"

    def mvvmbaseVersion = "2.0.0-beta6"

    [...]
    implementation de.trbnb.mvvmbase:mvvmbase:$mvvmbaseVersion"

    // RxJava 2 extensions
    implementation de.trbnb.mvvmbase:mvvmbaseRxJava2:$mvvmbaseVersion"
    
    // RxJava 3 extensions
    implementation "de.trbnb.mvvmbase:mvvmbaseRxJava3:$mvvmbaseVersion"

    // Coroutines extensions
    implementation "de.trbnb.mvvmbase:mvvmbaseCoroutines:$mvvmbaseVersion"

    // Conductor support
    implementation "de.trbnb.mvvmbase:mvvmbaseConductor:$mvvmbaseVersion"
}
```

After that the library should be initialized as it needs to know the `BR` class from which to get the property field IDs. The library can use its own generated class or another class can be specified. The best place for this is in your custom `Application` files `onCreate()`:

```kotlin
// initialization with de.trbnb.mvvmbase.BR
MvvmBase.autoInit()

// initialization with manual BR class specification
MvvmBase.init<BR>()
```

A futher explanation on why this is necessary is given in section `BindableProperty`.

## Proguard

The library also uses some Kotlin reflection features. The AAR is exported with a proguard config. Be aware that if you choose to call `MvvmBase.init<BR>()` the manually specified BR class will need its own rule.

# Features
This library comes with two major features:
* `MvvmActivity<VM>` / `MvvmFragment<VM>`
  View components that have a `ViewModel` instance associated with them and keep it alive during the entire lifecycle.

* `BindableProperty<T>`  
  A delegate property that calls `notfiyPropertyChanged(Int)` and finds the field ID in `BR.java` automatically. 

# How to use?

## `MvvmActivity<VM>` / `MvvmFragment<VM>`

The idea of these components is to simplify the setup of Activities and Fragments for use with Android Data Binding and MVVM architecture. So as an example let's take a look at how to use them with an example:

First we need a `BaseViewModel` implementation:

```kotlin
class MainViewModel() : BaseViewModel()
```

However this is not enough if we want to support saving state, so instead we use a `BaseStateSavingViewModel`:

```kotlin
class MainViewModel(savedStateHandle: SavedStateHandle) : BaseStateSavingViewModel(savedStateHandle)
```

Now this view model can be used by an `MvvmActivity`. This activity has to specify its layout ID. This can be done by override the `layoutId` property or by passing it as constructor paramter:

```kotlin
class MainActivity : MvvmActivity<MainViewModel>() {
    override val layoutId: Int = R.layout.activity_main
}

class MainActivity : MvvmActivity<MainViewModel>(R.layout.activity_main)
```

So what happens here?

The layout resource ID tells the data binding tools which layout should be inflated and prepared for data binding. It will also need a variable with the name `vm` and the type of the view model. This variable will be used to associate the view model with the layout. It can then be used to bind properties to views. It should look like this:

```xml
<layout xmlns:android="http://schemas.android.com/apk/res/android">

    <data>
        <variable
            name="vm"
            type="com.example.MainViewModel"/>
    </data>
    
    <TextView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:gravity="center"/>

</layout>
``` 

If you want to use Fragments instead, the procedure is exactly the same. Just use MvvmFragment instead of MvvmActivity.

Access to the created binding (to access specific views, etc.) can be gained by using `MvvmBindingActivity<VM, B>` / `MvvmBindingFragment<VM, B>`. Just specify the binding class as second type parameter and then you will be able to access it via the `binding` property.

The ViewModel will be initialized via AndroidX ViewModel API. Custom initialization techniques can be specified by overriding `getDefaultViewModelProviderFactory()`. See `ViewModel.viewModelDelegate` for more information.

***Note**: In contrast to previous versions of this library a `Provider<VM>` is not sufficient for this purpose anymore as it is now possible to pass a `SavedStateHandle` to the ViewModel. If Dagger was used before to inject the ViewModel changing to  `LegacyMvvmBindingActivity`/`LegacyMvvmBindingFragment` can be a quick fix as it provides the old API. However those classes are only for migration purposes and will be removed in the future.
It is also recommended to migrate from a `BaseViewModel` to a `BaseStateSavingViewModel` to support saving state. Injecting the `SavedStateHandle` via constructor will then be possible via `Subcomponent`s, [AssistedInject](https://github.com/square/AssistedInject) or Dagger Hilt with `ViewModelInject` as shown in the sample app module.*


### MvvmActivity/MvvmFragment

* `onViewModelLoaded(viewModel: VM)`  
This will be called when the view model is "loaded". This is called after the view model has been created or retained in `onCreate`. The loading of the view model may not be synchronous, so don't try to access it in `onCreate`.

* `onViewModelPropertyChanged(viewModel: VM, fieldId: Int)`  
This is called when the view model calls `notifyPropertyChanged`. The associated view model and the field ID that was used to call `notifyPropertyChanged` will be passed.

### ViewModel

* `onBind()`  
Will be called when the view model is associated with an activity and layout.

* `onUnbind()`  
Will be called when the view model is no longer associated with any activity/layout.

* `onDestroy()`  
Will be called when the instance is about to be destroyed. This should be used to clear references to avoid memory leaks.

#### Lifecycle

The ViewModel also implements LifecycleOwner which allows LiveData, rx.Observable, etc. to cancel listeners, subscriptions and others automatically.

Its state is:
- After initialization & being unbound: `Lifecycle.State.STARTED`.
- After being bound: `Lifecycle.State.RESUMED`.
- After being unbound: `Lifecycle.State.STARTED`.
- After being destroyed: `Lifecycle.State.DESTROYED`.

## BindableProperty<T>

As said before this is a delegate property which calls `notifyPropertyChanged` itself if its value has changed. It can also detect the field ID from the BR.java file automatically (specifying the field ID manually is also possible). So to use this feature first the property needs to know what `BR` class should be used. This should be done as early as possible, e.g. in the Applications `onCreate()`:

```kotlin
MvvmBase.init<com.example.BR>()
``` 

It can then be used like this:

```kotlin
class MainViewModel : BaseViewModel() {

    // Using bindable(defaultValue: T) to create a simple BindableProperty.
    @get:Bindable
    var isLoading1: Boolean by bindable(false)

    // Property type can be inferred.
    @get:Bindable
    var isLoading2 by bindable(false)

    // Specifying field ID manually is possible.
    @get:Bindable
    var isLoading3 by bindable(false, BR.loading)

    // Nullable property doesn't need a default value. Will default to null.
    @get:Bindable
    var foo: String? by bindable()

}
```

The BindableProperty also can be customized to react to value changes, etc. These extensions can be used in a builder pattern way like this:

```kotlin
@get:Bindable
var foo by bindable("")
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

### Primitives

There are also BindableProperties for primitive JVM types:

| Type      | BindableProperty type         | Convenience function  | default Value
|-----------|-------------------------------|-----------------------|:------------:
| `Boolean` | `BindableBooleanProperty`     | `bindableBoolean()`   | `false`
| `Byte`    | `BindableByteProperty`        | `bindableByte()`      | `0`
| `Char`    | `BindableCharProperty`        | `bindableChar()`      | none
| `Double`  | `BindableDoubleProperty`      | `bindableDouble()`    | `0.0`
| `Float`   | `BindableFloatProperty`       | `bindableFloat()`     | `0.0`
| `Int`     | `BindableIntProperty`         | `bindableInt()`       | `0`
| `Long`    | `BindableLongProperty`        | `bindableLong()`      | `0`
| `Short`   | `BindableShortProperty`       | `bindableShort()`     | `0`
| `UByte`*  | `BindableUByteProperty`       | `bindableUByte()`     | `0`
| `UInt`*   | `BindableUIntProperty`        | `bindableUInt()`      | `0`
| `ULong`*  | `BindableULongProperty`       | `bindableULong()`     | `0`
| `UShort`* | `BindableUShortProperty`      | `bindableUShort()`    | `0`

* Bindable Properties for unsigned types are experimental as unsigned types themselves are experimental.

## Saving state

Android X provides a way to save state of ViewModels via a `SavedStateHandle`. An implementation for that is provided as previously mentioned with `BaseStateSavingViewModel`. It contains a getter property for that handle that will have to be passed as constructor parameter.

BindableProperties also support the handle. By default the library tries to automatically derive a key from the property name for supported types. A key can also be defined manually or saving state can be disabled.

Example:

```kotlin
// defaults to StateSaveOption.Automatic for supported types (see below), otherwise StateSaveOption.None
@get:Bindable
var text1 by bindable("")

// key is derived automatically
@get:Bindable
var text2 by bindable("", stateSaveOption = StateSaveOption.Automatic)

// key is specified manually
@get:Bindable
var text3 by bindable("", stateSaveOption = StateSaveOption.Manual("progress"))

// no state saving for this property
@get:Bindable
var text4 by bindable("", stateSaveOption = StateSaveOption.None)
```

### Supported types

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

## EventChannel

Every `ViewModel` has an `EventChannel`. This can be used to transfer information to the view that are not state (e.g. showing a temporary error as toast).

```kotlin
sealed class MainEvent : Event {
    class ShowToast(val text: String) : MainEvent()
    class ShowSnackbar(val text: String) : MainEvent()
}

class MainViewModel : BaseViewModel() {
    init {
        eventChannel(MainEvent.ShowToast("Test"))
    }
}

class MainActivity : MvvmBindingActivity<MainViewModel, ActivityMainBinding>() {
    [...]
    override fun onEvent(event: Event) {
        super.onEvent(event)

        when (event as? MainEvent ?: return) {
            is MainEvent.ShowToast -> Toast.makeText(this, event.text, Toast.LENGTH_LONG).show()
            is MainEvent.ShowSnackbar -> Snackbar.make(binding.root, event.text, Snackbar.LENGTH_SHORT).show()
        }
    }
}
```

Because of the Activity/Fragment lifecycle it can happen that events are called when no listener is registered. By default these events are kept in memory until a listener is registered. That listener will then be called with all those events in the same order in which they were raised.  
This behavior can be turned off in the `BaseViewModel` by overriding `memorizeNotReceivedEvents` to let it return `false`.

## Nested ViewModels

Sometimes it can be useful to have multiple ViewModels inside another ViewModel (ViewModels for list items e.g.). In this case the "child" ViewModels must be destroyed when the "parent" is destroyed. For this purpose the function `autoDestroy()` can be used.

It can also be quite handy to send events from a "child" ViewModel and to have the "parent" emit them as well. This can be achieved via `bindEvents()`.

Example:

```kotlin
class MainViewModel : BaseViewModel() {
    @get:Bindable
    var items by bindable<List<ItemViewModel>>()
        .beforeSet { old, _ -> old.forEach { it.onDestroy() } } // old list items have to be destroyed manually
        
    fun fetch() {
        items = loadData()
            .map { /* map data to ItemViewModel */ }
            .bindEvents()
            .autoDestroy()
    }
}
```

## Commands

With Databinding it is possible to build Android apps with MVVM in a way that is similar to WPF. So it naturally makes sense to look at practices and patterns from that platform and see if they work in Android development.

One of the basic features of WPF are [Commands](https://msdn.microsoft.com/en-us/library/ms752308(v=vs.110).aspx).
This library includes an implementation of that pattern for Android.

### Specificaiton

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
    The RuleCommands enabled-state is defined by a function returning a Boolean. That function is invoked during construction and whenever `onEnabledChanged` is called.

Both commands let you pass a function in the constructor and come with a helper function to create a parameter-less instance without specifying `Unit` as parameter type.
This function will then be invoked when the Command is invoked.

#### Listener

View components can register listeners on commands that are triggered when the enabled-state changes. These have to be cleared. This is best done in the `onUnbind` method of the ViewModel by calling `clearEnabledListeners` on the commands.  
That's why both Command implementations come with ViewModel extension functions to create lifecycle-aware instances that call that function automatically.

A sample use could look like this:

```kotlin
val buttonCommand = simpleCommand {
    eventChannel(ShowToast("It just works!"))
}
```

After the Command is present in your ViewModel you can now bind it to any View with the `android:clickCommand` attribute. Your code could then look like this:

```xml
<Button
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="Sample Button"
    android:clickCommand="@{vm.buttonCommand}"/>
```

A Command that is bound via this attribute will be executed on click events. The enabled-state of the Command is also bound to enabled-state of the View. At the moment there is only support for "onClick"-event.

### Command bindings

The library includes some BindingAdapters for DataBinding XML that are used regularly.

- `android:clickCommand`  
  Binds a Command that will be invoked when the View is clicked. The enabled-property of the view will also be bound to the enabled-property of the command. Needs a parameter-less command.

- `android:longClickCommand`  
  Binds a Command that will be invoked when the View triggers the OnLongClickListener. As the OnLongClickListener returns a Boolean the command can do the same, otherwise `true` will be returned from the listener. Needs a parameter-less command.

## RxJava extensions

The optional `mvvmbaseRxJava2` & `mvvmbaseRxJava3` dependencies contain useful RxJava extensions for ViewModels. The ViewModel has to implement the `RxViewModel` interface to gain access to those extension functions (no property or function do actually have to manually be implemented).

### Disposable.autoDispose()

Calling this function will lead to the `Disposable` to be disposed when the ViewModels `onDestroy` is called.

### ViewModel.compositeDisposable

The library also provides extension property for a `CompositeDisposable` that is disposed in the ViewModels `onDestroy`, similar to the `viewModelScope` extension property for coroutines.

### toBindable()

The `RxViewModel` interface mostly consists of converter functions that will turn an RxJava object into a read-only delegate property that will call `notifyPropertyChanged` on value changes.

For the types `Observable<T>`, `Flowable<T>`, `Single<T>`, `Maybe<T>` the delegated property will then of type `T?`. It is nullable because `null` will be the value until something is emitted. The functions for those type also allow to set a `onComplete` and `onError` callback.  
It is also possible to set a non-nullable default value for these delegates that will be used in case the Rx object hasn't emitted anything by the time the getter is called and the value has to be non-nullable.

For `Completable` the delegated property will be of type `Boolean`. Its value will be `false` by default and then `true` when it is completed. Passing an `onError` callback is also allowed here.

All `toBindable` functions will dispose automatically after `onDestroy` is called.

## Coroutines

The coroutines dependency contains a `Flow<T>.toBindable()` function in the `CoroutinesViewModel` interface to create a Bindable Property that collects values emitted from that `Flow`. It also allows specifying a `CoroutineScope` (`ViewModel.viewModelScope` by default), error- and completion-handler function.

## Conductor support

The optional `mvvmbaseConductor` dependency contains a `MvvmController<VM>` and `MvvmBindingController<VM, B>` to use like `MvvmFragment<VM>` or `MvvmBindingFragment<VM, B>` in case [Conductor](https://github.com/bluelinelabs/Conductor) is used instead of the Fragment API.
