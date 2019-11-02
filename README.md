# What is MvvmBase?
MvvmBase is a small library to help work with data binding and the MVVM pattern on Android. It is build to extend the official [Android Data Binding tools](https://developer.android.com/topic/libraries/data-binding/). It is built with Kotlin and is best used with it.

# Installation

MvvmBase is available via JCenter. To use it put this in your `build.gradle`:

```gradle
dependencies {
    [...]
    // With old support libraries:
    implementation 'de.trbnb.mvvmbase:mvvmbase:1.2.0'

    // With androidx:
    implementation 'de.trbnb.mvvmbase:mvvmbase:1.2.0-androidx'
}
```

# Features
This library comes with two major features:
* `MvvmActivity<VM>` / `MvvmFragment<VM>`  
  View components that have a `BaseViewModel` instance associated with them and keep it alive during the entire lifecycle.

* `BindableProperty<T>`  
  A delegate property that calls `notfiyPropertyChanged(Int)` and finds the field ID in `BR.java` automatically. 

# How to use?

## `MvvmActivity<VM>` / `MvvmFragment<VM>`

The idea of these components is to simplify the setup of Activities and Fragments for use with Android Data Binding and MVVM architecture. So as an example let's take a look at how to use them with an example:

First we need a `BaseViewModel` implementation:

```kotlin
class MainViewModel : BaseViewModel()
```

Now this view model can be used by an `MvvmActivity`. For this a layout resource ID and a [`Provider<MainViewModel>`](https://docs.oracle.com/javaee/6/api/javax/inject/Provider.html) is needed.

```kotlin
class MainActivity : MvvmActivity<MainViewModel>() {

    override val layoutId: Int
        get() = R.layout.activity_main

    override val viewModelProvider: Provider<MainViewModel>
        get() = Provider { MainViewModel() }
}
```

So what do those two properties do?

* `viewModelProvider`  
The provider will be responsible for providing a view model instance when the Activity is created for the first time. Using a provider gives you two choices on how to create these instances:  
    * Create an instance yourself (like in the example).
    * Use a dependency injection framework (e.g. Dagger) to inject an instance.

    The Activity will then retain that view model for its lifetime, nothing else needs to be done in that regard.

* `layoutId`  
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

Access to the created binding (to access specific views, etc.) can be gained by using `MvvmBindingActivity<VM, B>` / `MvvmBindingFragment<VM, B>`. Just specify the binding class as second type parameter and then you will be able to access it via the `binding` property

And that's it! You're ready to go.  
But wait! There's more! There are methods that can be overridden if reaction to an event is wanted:

### MvvmActivity/MvvmFragment

* `onViewModelLoaded(viewModel: VM)`  
This will be called when the view model is "loaded". This is called after the view model has been created or retained in `onCreate`. The loading of the view model may not be synchronous, so don't try to access it in `onCreate`.

* `onViewModelPropertyChanged(viewModel: VM, fieldId: Int)`  
This is called when the view model calls `notifyPropertyChanged`. The associated view model and the field ID that was used to call `notifyPropertyChanged` will be passed.

### BaseViewModel

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
BindableProperty.init<com.example.BR>()
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
        return@validate if (new.length > old.length) new else old
    }
    .afterSet {
        Log.d(TAG, "Value changed: $it")
    }
```

This is what those extensions do:

* `distinct`  
If a new value is about to be set it will check if the new value is equal to the old one. If this is the case nothing further happens, `notifyPropertyChanged` will not be called and none of the extensions be invoked.

* `beforeSet`  
Will be called if a new value is about to be set. The old and the new value will be passed as parameters. Will not be invoked if `distinct` has an effect.

* `validate`  
Will be called if a new value is about to be set. The old and the new value will be passed as parameters and a return value that will then be set as actual value is expected. Will not be invoked if `distinct` has an effect.

* `afterSet`  
Will be called after a new value is set. The new value will be passed as parameter.

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

## Saving state

Android X provides a way to save state of ViewModels via a SavedStateHandle. While offical examples show this being done with the handle being given to the ViewModel as a constructor parameter this library will not support this implementation to allow for constructor dependency injection. Instead the handle is being given right after initialization. It can then be accessed with `ViewModel.savedStateHandle` and `ViewModel.withSavedStateHandle { }`.

BindableProperties also support this mechanism. By default the library tries to automatically derive a key from the property name. A key can also be defined manually or saving state can be disabled.
Example:

```kotlin
// defaults to StateSaveOption.Automatic
@get:Bindable
var progress1 by bindableInt()

// derives the key automatically
@get:Bindable
var progress2 by bindableInt(stateSaveOption = StateSaveOption.Automatic)

// key is specified manually
@get:Bindable
var progress3 by bindableInt(stateSaveOption = StateSaveOption.Manual("progress"))

// no state saving for this property
@get:Bindable
var progress4 by bindableInt(stateSaveOption = StateSaveOption.None)
```

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
