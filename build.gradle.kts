import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import kotlinx.kover.api.CounterType.BRANCH
import kotlinx.kover.api.CounterType.LINE
import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
import org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED
import org.gradle.api.tasks.testing.logging.TestLogEvent.STANDARD_ERROR
import java.time.Instant

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.8.10"
    id("org.jetbrains.kotlin.kapt") version "1.8.0"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.8.10"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("io.micronaut.application") version "3.7.0"

    id("info.solidsoft.pitest") version "1.9.11"
    id("org.jlleitschuh.gradle.ktlint") version "11.1.0"
    id("io.gitlab.arturbosch.detekt") version "1.22.0"
    id("org.jetbrains.kotlinx.kover") version "0.6.1"
}

version = "0.1"
group = "dev.kastro"

val kotlinVersion = project.properties.get("kotlinVersion")
repositories {
    mavenCentral()
}

dependencies {
    // MICRONAUT
    kapt("io.micronaut:micronaut-http-validation")
    implementation("io.micronaut:micronaut-http-client")
    implementation("io.micronaut:micronaut-jackson-databind")
    implementation("io.micronaut.kotlin:micronaut-kotlin-runtime")
    implementation("io.micronaut:micronaut-validation")

    // KOTLIN
    implementation("jakarta.annotation:jakarta.annotation-api")
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    runtimeOnly("com.fasterxml.jackson.module:jackson-module-kotlin")

    // LOG
    runtimeOnly("ch.qos.logback:logback-classic")

    // AWS
    implementation("software.amazon.awssdk:dynamodb-enhanced:2.20.1")
    implementation("software.amazon.awssdk:secretsmanager:2.20.0")

    // TEST
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.2")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.25")
    testImplementation("io.mockk:mockk:1.13.4")
}

application {
    mainClass.set("dev.kastro.ApplicationKt")
}

java {
    sourceCompatibility = JavaVersion.toVersion("11")
}

graalvmNative.toolchainDetection.set(false)

var excludeFiles = listOf(
    "mocks.*",
    "*.application.Application*",
    "*.application.config.*",
    "*.application.controllers.requests.*",
    "*.application.controllers.responses.*",
    "*.application.exceptions.*",
    "*.application.handlers.*",
    "*.domain.enums.*",
    "*.domain.models.*",
    "*.domain.repositories.*",
    "*.resources.repositories.*"
)

micronaut {
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("dev.kastro.*")
    }
}

tasks {
    compileKotlin {
        kotlinOptions {
            jvmTarget = "11"
        }
    }

    compileTestKotlin {
        kotlinOptions {
            jvmTarget = "11"
        }
    }

    withType<Test> {
        jvmArgs("--add-opens", "java.base/java.time=ALL-UNNAMED")

        useJUnitPlatform()

        testLogging {
            events(FAILED, STANDARD_ERROR, SKIPPED)
            exceptionFormat = FULL
            showExceptions = true
            showCauses = true
            showStackTraces = true
        }
    }

    withType<ShadowJar> {
        archiveBaseName.set("wallet-service")
        mergeServiceFiles()
        manifest {
            attributes(
                mapOf(
                    "Main-Class" to "dev.kastro.application.ApplicationKt",
                    "Built-Date" to Instant.now().toString()
                )
            )
        }
    }
}

detekt {
    config = files("detekt-config.yml")
}

ktlint {
    verbose.set(true)
}

pitest {
    junit5PluginVersion.set("0.15")
    timestampedReports.set(false)
    excludedClasses.set(excludeFiles)
    avoidCallsTo.set(listOf("kotlin.ResultKt", "kotlin.jvm.internal"))
}

kover {
    filters {
        classes {
            excludes += excludeFiles
        }
    }

    instrumentation {
        excludeTasks += setOf("testNativeImage")
    }

    xmlReport {
        onCheck.set(true)
    }

    htmlReport {
        onCheck.set(true)
    }

    verify {
        rule {
            bound {
                minValue = 98
                counter = LINE
            }
        }
        rule {
            bound {
                minValue = 90
                counter = BRANCH
            }
        }
    }
}
