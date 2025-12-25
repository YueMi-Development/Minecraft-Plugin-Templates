plugins {
    `java-library`
    `maven-publish`
}

val pluginName: String by project
val yuemiMaven: String by project

require(pluginName.isNotBlank()) { "pluginName must be set in gradle.properties" }
require(yuemiMaven.isNotBlank()) { "yuemiMaven must be set in gradle.properties" }

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.6-R0.1-SNAPSHOT")
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

tasks.jar {
    archiveBaseName.set(pluginName)
    archiveVersion.set(project.version.toString())
    archiveClassifier.set("")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            groupId = project.group.toString()
            artifactId = pluginName
            version = project.version.toString()
        }
    }

    repositories {
        maven {
            name = "YuemiMaven"
            url = uri(yuemiMaven)

            credentials {
                username = System.getenv("MAVEN_USERNAME")
                password = System.getenv("MAVEN_PASSWORD")
            }
        }
    }
}
