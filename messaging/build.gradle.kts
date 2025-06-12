plugins {
    kotlin("jvm") version "2.1.20"
}

group = "com.mendoza"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("io.github.viartemev:rabbitmq-kotlin:0.7.0-SNAPSHOT")
    implementation("com.rabbitmq:amqp-client:5.18.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}
