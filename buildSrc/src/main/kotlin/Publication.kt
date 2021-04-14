enum class Publication(val artifactId: String, val description: String) {
    CORE(Publishing.mainArtifactId, Publishing.mainDescription),
    CONDUCTOR(Publishing.conductorArtifactId, Publishing.conductorDescription),
    RX_JAVA_2(Publishing.rxJava2ArtifactId, Publishing.rxJava2Description),
    RX_JAVA_3(Publishing.rxJava3ArtifactId, Publishing.rxJava3Description),
    COROUTINES(Publishing.coroutinesArtifactId, Publishing.coroutinesDescription)
}