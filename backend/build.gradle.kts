plugins {
    java
    id("org.springframework.boot") version "3.3.4"
    id("io.spring.dependency-management") version "1.1.6"
}

group = "com.linkedin"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(22)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    // Support pour les fichiers .env
    implementation("me.paulschwarz:spring-dotenv:2.5.4")


    // Pour l'API OpenAI
    implementation("com.theokanning.openai-gpt3-java:service:0.18.2")
    implementation("com.theokanning.openai-gpt3-java:client:0.18.2")

    // Pour la communication HTTP asynchrone
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-jackson:2.9.0")
    // Database
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("mysql:mysql-connector-java:8.0.33")

    // Search
    implementation("org.hibernate.search:hibernate-search-mapper-orm:7.2.2.Final")
    implementation("org.hibernate.search:hibernate-search-backend-lucene:7.2.2.Final")
    implementation("org.jboss.logging:jboss-logging:3.6.1.Final")

    // Security
    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    implementation("io.jsonwebtoken:jjwt-impl:0.12.6")
    implementation(("io.jsonwebtoken:jjwt-jackson:0.12.6"))

    // DevTools
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
