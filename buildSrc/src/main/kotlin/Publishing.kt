import org.gradle.api.Project
import org.gradle.api.publish.PublicationContainer
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.get
import java.nio.file.Paths

object Publishing {
    const val versionName = "2.1.1"
    const val versionCode = 43

    const val url = "https://github.com/trbnb/MvvmBase"
    const val gitUrl = "https://github.com/trbnb/MvvmBase.git"
    const val licenseUrl = "https://github.com/trbnb/MvvmBase/blob/master/LICENSE"
    const val groupId = "de.trbnb"

    const val mainDescription = "MVVM Framework for Android."
    const val mainArtifactId = "mvvmbase-core"

    const val rxJava2Description = "RxJava2 extensions for MvvmBase."
    const val rxJava2ArtifactId = "mvvmbase-rxjava2"

    const val rxJava3Description = "RxJava3 extensions for MvvmBase."
    const val rxJava3ArtifactId = "mvvmbase-rxjava3"

    const val conductorDescription = "Conductor extensions for MvvmBase."
    const val conductorArtifactId = "mvvmbase-conductor"

    const val coroutinesDescription = "Coroutines extensions for MvvmBase."
    const val coroutinesArtifactId = "mvvmbase-coroutines"

    fun getOssrhUsername(project: Project) = project.rootProject.extra["private_ossrh_user"].toString()
    fun getOssrhPassword(project: Project) = project.rootProject.extra["private_ossrh_password"].toString()

    fun setupSigning(project: Project) {
        project.rootProject.extra["signing.keyId"] = project.rootProject.extra["private_ossrh_signing_keyid"].toString()
        project.rootProject.extra["signing.password"] = project.rootProject.extra["private_ossrh_signing_passphrase"].toString()
        project.rootProject.extra["signing.secretKeyRingFile"] = Paths.get(project.rootDir.canonicalPath, "signing.gpg")
    }
}

fun PublicationContainer.create(publication: Publication, project: Project) = create<MavenPublication>("release") {
    from(project.components["release"])

    project.getTasksByName("sourcesJar", false).forEach { artifact(it) }

    groupId = Publishing.groupId
    artifactId = publication.artifactId
    version = Publishing.versionName

    pom {
        packaging = "aar"

        name.set(publication.artifactId)
        description.set(publication.description)
        url.set(Publishing.url)

        developers {
            developer {
                id.set("trbnb")
                name.set("Thorben Buchta")
                email.set("thorbenbuchta@gmail.com")
                url.set("https://www.trbnb.de")
            }
        }

        licenses {
            license {
                name.set("The Apache Software License, Version 2.0")
                url.set(Publishing.licenseUrl)
            }
        }

        scm {
            connection.set(Publishing.gitUrl)
            developerConnection.set(Publishing.gitUrl)
            url.set(Publishing.url)
        }
    }
}