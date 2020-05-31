import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.extra

const val kotlinVersion = "1.3.72"

const val gradleToolsVersion = "3.6.3"

val javaVersion = JavaVersion.VERSION_1_8

object Android {
    const val minSdk = 14
    const val compileSdk = 29
}

object Publishing {
    const val versionName = "2.0.0-beta5"
    const val versionCode = 34

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

    fun getBintrayUser(project: Project): String = project.extra["private_bintray_user"].toString()
    fun getBintrayApiKey(project: Project): String = project.extra["private_bintray_apikey"].toString()
    fun getBintrayGpgPassphrase(project: Project): String = project.extra["private_bintray_gpg_passphrase"].toString()
}

