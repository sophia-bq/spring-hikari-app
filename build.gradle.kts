plugins {
    id("org.springframework.boot") version "3.3.13"
    id("io.spring.dependency-management") version "1.1.4"
    java
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("software.amazon.jdbc:aws-advanced-jdbc-wrapper:2.6.0")
    implementation("org.mariadb.jdbc:mariadb-java-client:3.5.3")
}
