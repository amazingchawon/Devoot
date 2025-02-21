plugins {
    java
    id("org.springframework.boot") version "3.4.1"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.ec4j.editorconfig") version "0.1.0"
    id("checkstyle")
}

group = "com.gamee"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks.withType<Checkstyle>().configureEach {
    // src/test 디렉토리 검사 제외 (추가적인 안전 조치)
    exclude("**/src/test/**")
}

// 선택 사항: checkstyleTest 비활성화
tasks.named<Checkstyle>("checkstyleTest") {
    isEnabled = false
}

editorconfig {
    excludes.add("build")
}

tasks.named("build") {
    doFirst {
        // Set the system property for the active Spring profile
        tasks.withType<JavaExec>().configureEach {
            systemProperty("spring.profiles.active", "test")
        }
    }
}

checkstyle {
    maxWarnings = 0
    configFile = file("${rootDir}/config/naver-checkstyle-rules.xml")
    configProperties["suppressionFile"] = "${rootDir}/config/naver-checkstyle-suppressions.xml"
    toolVersion = "8.42"
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}
tasks.withType<Checkstyle>().configureEach {
    // src/test 디렉토리 검사 제외 (추가적인 안전 조치)
    exclude("**/src/test/**")
}

// 선택 사항: checkstyleTest 비활성화
tasks.named<Checkstyle>("checkstyleTest") {
    isEnabled = false
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.mariadb.jdbc:mariadb-java-client")
    implementation("org.springframework.boot:spring-boot-starter-data-elasticsearch")
    implementation("com.mysql:mysql-connector-j")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("jakarta.validation:jakarta.validation-api:3.0.0")
    implementation("org.hibernate.validator:hibernate-validator:9.0.0.CR1")
    implementation("com.google.firebase:firebase-admin:9.4.2")
    implementation("io.awspring.cloud:spring-cloud-aws-starter-s3:3.0.0")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("com.h2database:h2")
    testImplementation("org.springframework.security:spring-security-test:6.4.2")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Test> {
    systemProperty("file.encoding", "UTF-8")
}

tasks.named("check") {
    dependsOn("editorconfigCheck") // If checkstyle is configured, run editorconfigCheck before checkstyle
}

tasks.withType<Test> {
    val envFile = file(".env")
    if (envFile.exists()) {
        envFile.forEachLine { line ->
            if (line.isNotBlank() && !line.startsWith("#")) {
                val (key, value) = line.split("=", limit = 2)
                environment(key.trim(), value.trim())
            }
        }
    }
}
