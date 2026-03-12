plugins {
    kotlin("jvm") version "2.0.21"
    application
    id("com.gradleup.shadow") version "8.3.6"
}

repositories {
    mavenCentral()
}

val cucumberVersion = "7.18.1"

sourceSets {
    create("acceptance") {
        kotlin.srcDir("src/acceptance/kotlin")
        resources.srcDir("src/acceptance/resources")
        compileClasspath += sourceSets["main"].output
        runtimeClasspath += output + compileClasspath
    }
}

val acceptanceImplementation by configurations.getting

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.3")
    testImplementation("com.google.truth:truth:1.4.4")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    acceptanceImplementation(sourceSets["main"].output)
    acceptanceImplementation("io.cucumber:cucumber-java8:$cucumberVersion")
    acceptanceImplementation("io.cucumber:cucumber-junit-platform-engine:$cucumberVersion")
    acceptanceImplementation("org.junit.platform:junit-platform-suite:1.11.3")
    acceptanceImplementation("org.junit.platform:junit-platform-launcher:1.11.3")
    acceptanceImplementation("io.cucumber:cucumber-picocontainer:$cucumberVersion")
    acceptanceImplementation("com.google.truth:truth:1.4.4")
}

application {
    mainClass.set("merkle.MainKt")
}

tasks.test {
    useJUnitPlatform()
}

tasks.named<Copy>("processAcceptanceResources") {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.register<Test>("acceptanceCuke") {
    description = "Runs Cucumber acceptance tests"
    group = "verification"
    testClassesDirs = sourceSets["acceptance"].output.classesDirs
    classpath = sourceSets["acceptance"].runtimeClasspath
    useJUnitPlatform()
    systemProperty(
        "cucumber.plugin",
        "json:build/reports/tests/acceptance/cucumber/cucumber.json"
    )
    reports {
        html.required.set(false)
        junitXml.required.set(false)
    }
}

tasks.shadowJar {
    archiveBaseName.set("merkle")
    archiveClassifier.set("")
    archiveVersion.set("")
    destinationDirectory.set(layout.projectDirectory.dir("dist"))
}
