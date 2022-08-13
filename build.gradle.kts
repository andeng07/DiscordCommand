plugins {
    kotlin("jvm") version "1.7.10"
    java
}

group = "me.centauri07.dc"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))

    implementation("net.dv8tion:JDA:5.0.0-alpha.17")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}