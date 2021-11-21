import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.tasks.testing.logging.TestLogEvent.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version "1.5.10"
  application
  id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "com.ak"
version = "1.0.0-SNAPSHOT"

repositories {
  mavenCentral()
}

val vertxVersion = "4.2.1"
val junitJupiterVersion = "5.7.0"
val logbackVersion = "0.1.5"

val libraries = mapOf(
  "platform" to "io.vertx:vertx-stack-depchain:$vertxVersion",
  "vertx-web" to "io.vertx:vertx-web",
  "vertx-web-openapi" to "io.vertx:vertx-web-openapi",
  "vertx-lang-kotlin" to "io.vertx:vertx-lang-kotlin",
  "logback-json-classic" to "ch.qos.logback.contrib:logback-json-classic:$logbackVersion",
  "logback-jackson" to "ch.qos.logback.contrib:logback-jackson:$logbackVersion",
  "stdlib" to "stdlib-jdk8",
  "junit5-vertx" to "io.vertx:vertx-junit5",
  "junit5" to "org.junit.jupiter:junit-jupiter-api:$junitJupiterVersion",
)

val mainVerticleName = "com.ak.async.MainVerticle"
val launcherClassName = "io.vertx.core.Launcher"

val watchForChange = "src/**/*"
val doOnChange = "${projectDir}/gradlew classes"

application {
  mainClass.set(launcherClassName)
}

dependencies {
  implementation(platform(libraries["platform"]!!))
  implementation(libraries["vertx-web"]!!)
  implementation(libraries["vertx-web-openapi"]!!)
  implementation(libraries["vertx-lang-kotlin"]!!)
  implementation(libraries["logback-json-classic"]!!)
  implementation(libraries["logback-jackson"]!!)
  implementation(kotlin(libraries["stdlib"]!!))
  testImplementation(libraries["junit5-vertx"]!!)
  testImplementation(libraries["junit5"]!!)
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions.jvmTarget = "11"

tasks.withType<ShadowJar> {
  archiveClassifier.set("fat")
  manifest {
    attributes(mapOf("Main-Verticle" to mainVerticleName))
  }
  mergeServiceFiles()
}

tasks.withType<Test> {
  useJUnitPlatform()
  testLogging {
    events = setOf(PASSED, SKIPPED, FAILED)
  }
}

tasks.withType<JavaExec> {
  args = listOf(
    "run",
    mainVerticleName,
    "--redeploy=$watchForChange",
    "--launcher-class=$launcherClassName",
    "--on-redeploy=$doOnChange"
  )
}
