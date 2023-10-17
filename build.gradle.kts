plugins {
    kotlin("jvm") version "1.9.0"
    application
}

group = "com.github.koswag"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

object Versions {
    const val KOTEST = "5.7.2"
}

dependencies {
    testImplementation("io.kotest:kotest-runner-junit5:${Versions.KOTEST}")
    testImplementation(kotlin("test"))
    implementation(kotlin("reflect"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}

application {
    mainClass.set("MainKt")
}