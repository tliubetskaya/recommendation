import org.openapitools.generator.gradle.plugin.tasks.GenerateTask

plugins {
    java
    id("org.springframework.boot") version "3.3.2"
    id("io.spring.dependency-management") version "1.1.6"
    id("org.openapi.generator") version "7.7.0"
}

group = "com.investment"
version = "0.0.1"

java {
    toolchain{
        languageVersion = JavaLanguageVersion.of(21)
    }
}

java.sourceSets["main"].java {
    srcDir("$rootDir/generated/src/main/java")
}

openApiGenerate {
    generatorName.set("spring")
    library.set("spring-boot")
    inputSpec.set("$rootDir/recommendation-spec.yaml")
    outputDir.set("$rootDir/generated")
    apiPackage.set("com.investment.controller")
    modelPackage.set("com.investment.service.model")

    configOptions.put("generatedConstructorWithRequiredArgs", "true")
    configOptions.put("interfaceOnly", "true")
    configOptions.put("useSpringController", "true")
    configOptions.put("useTags", "true")
    configOptions.put("dateLibrary", "java8")
    configOptions.put("skipDefaultInterface", "true")
    configOptions.put("useSpringBoot3", "true")
    configOptions.put("useJakartaEe", "true")
    configOptions.put("useResponseEntity", "false")
    configOptions.put("additionalModelTypeAnnotations", """
        |@lombok.Builder
        |@lombok.extern.jackson.Jacksonized
        |@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
    """.trimMargin())

    typeMappings.put("string:dateTime", "java.time.LocalDateTime")
    typeMappings.put("string:date", "java.time.LocalDateTime")
    modelNameMappings.put("SymbolEnum", "CryptoSymbol")
}

repositories {
    mavenCentral()
}
object Versions {
    const val LOMBOK = "1.18.30"
    const val APACHE_COMMONS_LANG = "3.14.0"
    const val APACHE_COMMONS_COLLECTIONS = "4.4"
    const val LOGGING = "2.0"
}

dependencies {
    modules {
        module("org.springframework.boot:spring-boot-starter-logging") {
            replacedBy("org.springframework.boot:spring-boot-starter-log4j2", "Use Log4j2 instead of Logback")
        }
    }
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-web")
   // implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-log4j2")
    implementation("org.apache.commons:commons-csv:1.11.0")
    implementation("org.apache.commons:commons-lang3:${Versions.APACHE_COMMONS_LANG}")
    implementation("org.apache.commons:commons-collections4:${Versions.APACHE_COMMONS_COLLECTIONS}")
    implementation("jakarta.validation:jakarta.validation-api:3.0.2")
    implementation("org.springdoc:springdoc-openapi-ui:1.8.0")
    implementation("org.openapitools:jackson-databind-nullable:0.2.6")
    implementation("com.newrelic.logging:log4j2:${Versions.LOGGING}")
    implementation("com.bucket4j:bucket4j-core:8.7.0")

    annotationProcessor("org.projectlombok:lombok:${Versions.LOMBOK}")
    compileOnly("org.projectlombok:lombok:${Versions.LOMBOK}")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.named("openApiGenerate") {
    outputs.upToDateWhen { false }
    outputs.cacheIf { false }
}

tasks.register<Delete>("cleanGenerated") {
    delete("$rootDir/generated")
}

tasks{
    clean{
        dependsOn("cleanGenerated")
    }
}
tasks {
    compileJava {
        dependsOn("openApiGenerate")
    }
}

tasks.test {
    useJUnitPlatform()
}