import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.extra

const val kotlinVersion = "1.4.31"

const val gradleToolsVersion = "4.1.1"

val javaVersion = JavaVersion.VERSION_1_8

object Versions {
    const val compose = "1.0.0-beta03"
    const val daggerHilt = "2.33-beta"
}

object Android {
    const val minSdk = 21
    const val compileSdk = 29
}

object Publishing {
    const val versionName = "2.1.0"
    const val versionCode = 42

    const val url = "https://github.com/trbnb/MvvmBase"
    const val gitUrl = "https://github.com/trbnb/MvvmBase.git"
    const val licenseUrl = "https://github.com/trbnb/MvvmBase/blob/master/LICENSE"
    const val groupId = "de.trbnb.mvvmbase"

    const val mainDescription = "MVVM Framework for Android."
    const val mainArtifactId = "mvvmbase"

    const val rxJava2Description = "RxJava2 extensions for MvvmBase."
    const val rxJava2ArtifactId = "mvvmbaseRxJava2"

    const val rxJava3Description = "RxJava3 extensions for MvvmBase."
    const val rxJava3ArtifactId = "mvvmbaseRxJava3"

    const val conductorDescription = "Conductor extensions for MvvmBase."
    const val conductorArtifactId = "mvvmbaseConductor"

    const val coroutinesDescription = "Coroutines extensions for MvvmBase."
    const val coroutinesArtifactId = "mvvmbaseCoroutines"

    fun getBintrayUser(project: Project): String = project.extra["private_bintray_user"].toString()
    fun getBintrayApiKey(project: Project): String = project.extra["private_bintray_apikey"].toString()
    fun getBintrayGpgPassphrase(project: Project): String = project.extra["private_bintray_gpg_passphrase"].toString()
}

