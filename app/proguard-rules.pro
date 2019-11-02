# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Users\Thorben\Documents\android-sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
#-dontobfuscate
#-keepattributes Signature, InnerClasses, EnclosingMethod, RuntimeVisibleAnnotations
-keep class de.trbnb.mvvmbase.** { *; }
# Prevent a BuildLoader error. Not needed when running with R8.
#-keep interface kotlin.reflect.jvm.internal.impl.builtins.BuiltInsLoader
#-keep class kotlin.reflect.jvm.internal.impl.serialization.deserialization.builtins.BuiltInsLoaderImpl
-keep @interface kotlin.Metadata { *; }
-keep class kotlin.reflect.jvm.internal.** { *; }

-keep class androidx.lifecycle.ViewModel
-keep interface androidx.databinding.Observable
-keep interface androidx.lifecycle.LifecycleOwner
