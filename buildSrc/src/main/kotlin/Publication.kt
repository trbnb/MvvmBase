enum class Publication(val artifactId: String, val description: String) {
    CORE("mvvmbase-core", "MVVM Framework for Jetpack Compose on Android."),
    RX_JAVA_2("mvvmbase-rxjava2", "RxJava2 extensions for MvvmBase + DataBinding."),
    RX_JAVA_3("mvvmbase-rxjava3", "RxJava3 extensions for MvvmBase + DataBinding."),
    COROUTINES("mvvmbase-coroutines", "Coroutines extensions for MvvmBase."),
    DATABINDING("mvvmbase-databinding", "Compatibility module for MvvmBase + DataBinding."),
    CONDUCTOR("mvvmbase-conductor", "Conductor extensions for MvvmBase + DataBinding.")
}