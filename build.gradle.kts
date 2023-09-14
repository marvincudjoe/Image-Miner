import org.gradle.api.JavaVersion.VERSION_17

plugins {
    java
    idea
}

val javaVersion = VERSION_17.majorVersion.toInt()
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(javaVersion)
    }
}

dependencies {
    implementation("org.apache.pdfbox:pdfbox:3.0.0")
    implementation("org.apache.poi:poi:5.2.3")
    implementation("org.apache.poi:poi-ooxml:5.2.3")

    implementation("ch.qos.logback:logback-classic:1.4.11")
    implementation("ch.qos.logback:logback-core:1.4.11")
    implementation("org.apache.logging.log4j:log4j-core:3.0.0-alpha1")
    implementation("org.slf4j:slf4j-api:2.0.9")

    testImplementation("org.mockito:mockito-core:5.5.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.0")
}

repositories {
    mavenCentral()
}

tasks.test {
    // Use the built-in JUnit support of Gradle.
    useJUnitPlatform()
}