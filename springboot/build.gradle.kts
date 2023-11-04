import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.1.4"
	id("io.spring.dependency-management") version "1.1.3"
	kotlin("jvm") version "1.8.22"
	kotlin("plugin.spring") version "1.8.22"
}

group = "com.goliath.emojihub"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

//java {
//	sourceCompatibility = JavaVersion.VERSION_17
//}

repositories {
	mavenCentral()
}

dependencies {
	// Web &DB
	implementation("org.springframework.boot:spring-boot-starter-web")

	// Kotlin Features
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")

	// Test
	testImplementation("org.springframework.boot:spring-boot-starter-test")

	// Firebase
	implementation("com.google.firebase:firebase-admin:9.2.0")

	// Auth
	implementation("io.jsonwebtoken:jjwt-api:0.11.5")
	implementation("io.jsonwebtoken:jjwt-impl:0.11.5")
	implementation("io.jsonwebtoken:jjwt-jackson:0.11.5")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("com.google.code.gson:gson:2.10.1")

    implementation("org.projectlombok:lombok:1.18.30")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
