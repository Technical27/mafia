plugins {
    // Apply the Kotlin JVM plugin to add support for Kotlin.
    kotlin("jvm") version "1.4.0"

    // Apply the application plugin to add support for building a CLI application.
    application
}

repositories {
    // Use jcenter for resolving dependencies.
    // You can declare any Maven/Ivy/file repository here.
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.discord4j:discord4j-core:3.1.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactive:1.3.9")
    implementation("io.github.cdimascio:java-dotenv:5.2.1")
}

application {
    // Define the main class for the application.
    mainClass.set("io.github.technical27.mafia.AppKt")
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "io.github.technical27.mafia.AppKt"
    }

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
}
