plugins {
    kotlin("jvm") version "1.4.31"
    kotlin("plugin.serialization") version "1.4.31"
}

group = "dev.dreamhopping.coordinate"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.1.0")
    implementation("de.brudaswen.kotlinx.serialization:kotlinx-serialization-csv:1.1.0")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.3")
}
