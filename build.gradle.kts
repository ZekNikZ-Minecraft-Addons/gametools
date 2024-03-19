plugins {
    java
    id("org.jetbrains.kotlin.jvm") version "1.9.20"
    id("io.papermc.paperweight.userdev") version "1.5.10"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("org.jlleitschuh.gradle.ktlint") version "11.6.1"
    `maven-publish`
}

group = "io.zkz.mc"
version = "6.0.0-SNAPSHOT"

object Constants {
    // SDK
    const val kotlinVersion = "1.9.20"
    const val targetJavaVersion = 17

    // Libraries
    const val paperVersion = "1.20.4-R0.1-SNAPSHOT"
    const val reflectionsVersion = "0.10.2"
    const val cloudCommandFrameworkVersion = "1.8.4"

    // Plugins
    const val protocolLibVersion = "5.2.0-SNAPSHOT"

    // MC
    const val apiVersion = "1.20"
}

repositories {
    mavenCentral()
    maven(url = "https://repo.papermc.io/repository/maven-public/")
    maven(url = "https://oss.sonatype.org/content/groups/public/")
    maven(url = "https://repo.papermc.io/repository/maven-public/")
    maven(url = "https://repo.dmulloy2.net/repository/public/")
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Constants.kotlinVersion}")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${Constants.kotlinVersion}")

    paperweightDevelopmentBundle("io.papermc.paper:dev-bundle:${Constants.paperVersion}")

    implementation("org.reflections:reflections:${Constants.reflectionsVersion}")

    api("cloud.commandframework:cloud-paper:${Constants.cloudCommandFrameworkVersion}")
    api("cloud.commandframework:cloud-minecraft-extras:${Constants.cloudCommandFrameworkVersion}")

    compileOnly("com.comphenix.protocol:ProtocolLib:${Constants.protocolLibVersion}")
}

java {
    val javaVersion = JavaVersion.toVersion(Constants.targetJavaVersion)
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
    toolchain {
        languageVersion = JavaLanguageVersion.of(Constants.targetJavaVersion.toString())
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.release = Constants.targetJavaVersion
}

tasks.processResources {
    val props = mapOf(
        "version" to version,
        "name" to project.name,
        "apiVersion" to Constants.apiVersion,
    )
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}

kotlin {
    jvmToolchain(Constants.targetJavaVersion)
}

publishing {
    publications {
        create<MavenPublication>("libraryJar") {
            from(components["java"])
        }
    }

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/ZekNikZ-Minecraft-Addons/gametools")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

tasks.assemble {
    dependsOn(tasks.reobfJar)
}
