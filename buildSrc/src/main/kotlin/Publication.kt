enum class Publication(val artifactId: String, val description: String) {
    CORE("mvvmbase-core", "MVVM Framework for Android."),
    RX_JAVA_2("mvvmbase-rxjava2", "RxJava2 extensions for MvvmBase."),
    RX_JAVA_3("mvvmbase-rxjava3", "RxJava3 extensions for MvvmBase."),
    COROUTINES("mvvmbase-coroutines", "Coroutines extensions for MvvmBase."),
    DATABINDING("mvvmbase-databinding", "DataBinding extensions for MvvmBase.")
}