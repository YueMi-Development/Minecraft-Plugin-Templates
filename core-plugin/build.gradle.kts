plugins {
    java
}

val pluginName: String by project

require(pluginName.isNotBlank())

dependencies {
    implementation(project(":core-api"))
    compileOnly("io.papermc.paper:paper-api:1.21.6-R0.1-SNAPSHOT")
}

tasks.jar {
    archiveBaseName.set(pluginName)
    archiveVersion.set(project.version.toString())
    archiveClassifier.set("")

    manifest {
        attributes(
            "Implementation-Title" to pluginName,
            "Implementation-Version" to project.version.toString()
        )
    }
}
