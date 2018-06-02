# What is MvvmBase?
MvvmBase is a small library to help work with data binding and the MVVM pattern on Android. It is build to extend the official [Android Data Binding tools](https://developer.android.com/topic/libraries/data-binding/). It is built with Kotlin and is best used with it.

# Installation

MvvmBase is available via JCenter. To use it put this in your `build.gradle`:

```gradle
dependencies {
    [...]
    implementation 'de.trbnb.mvvmbase:mvvmbase:1.0.0'
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
