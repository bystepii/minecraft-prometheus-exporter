plugins {
    `java-library`
    `maven-publish`
    kotlin("jvm") version "2.0.21"
    //id("io.papermc.paperweight.userdev") version "1.7.5"
}

repositories {
    mavenLocal()
    maven {
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        url = uri("https://repo.clojars.org/")
    }
    mavenCentral()
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
    testImplementation("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
    //paperweight.devBundle("puregero.multipaper", "1.20.1-R0.1-SNAPSHOT")
    //compileOnly("com.github.puregero:multipaper-api:1.20.1-R0.1-SNAPSHOT")
    //testImplementation("com.github.puregero:multipaper-api:1.20.1-R0.1-SNAPSHOT")

    implementation("com.github.puregero:multilib:1.2.4")

    api("org.eclipse.jetty:jetty-server:12.0.14")
    api("io.prometheus:simpleclient_common:0.16.0")
    api("io.prometheus:simpleclient_hotspot:0.16.0")
    api("com.jayway.jsonpath:json-path:2.9.0")
    api("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.0.21")

    testImplementation("org.junit.jupiter:junit-jupiter:5.11.2")
    testImplementation("org.mockito:mockito-core:5.14.1")
    testImplementation("org.mockito:mockito-junit-jupiter:5.14.1")
    testImplementation("org.assertj:assertj-core:3.26.3")
    testImplementation("io.rest-assured:rest-assured:5.5.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test:2.0.21")
}

group = "de.sldk.mc"
version = "3.1.6-SNAPSHOT"
description = "minecraft-prometheus-exporter"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

kotlin {
    jvmToolchain(17)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "de.sldk.mc.PrometheusExporter"
    }
}

tasks.register<Jar>("jarWithDependencies") {
    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) }
    })

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    archiveClassifier.set("with-dependencies")
    manifest {
        attributes["Main-Class"] = "de.sldk.mc.PrometheusExporter"
    }
}

