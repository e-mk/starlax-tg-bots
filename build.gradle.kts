plugins {
    id("java")
}

group = "com.mandos"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.7.22")
    implementation("org.telegram:telegrambots:6.0.1")
    implementation("org.slf4j:slf4j-api:2.0.6")
    implementation("org.slf4j:slf4j-simple:2.0.6")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.ton:ton-kotlin:0.2.4")
}
