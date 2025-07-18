plugins {
	java
	id("org.springframework.boot") version "3.4.5"
	id("io.spring.dependency-management") version "1.1.7"
	id("com.diffplug.spotless") version "7.0.4"
    id("org.springdoc.openapi-gradle-plugin") version "1.9.0"
}

group = "de.tum"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

spotless {
	java {
		palantirJavaFormat()
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	implementation("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")

	// Optional für Tests
	testImplementation("org.projectlombok:lombok")
	testAnnotationProcessor("org.projectlombok:lombok")

	implementation("org.springframework.boot:spring-boot-starter-web")
	testImplementation("org.springframework.boot:spring-boot-starter-test")

	implementation ("org.springframework.boot:spring-boot-starter-webflux")

	implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
	// Springdoc OpenAPI dependency
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0")

	//Prometheus dependencies
    implementation("io.micrometer:micrometer-registry-prometheus")
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:2024.0.1")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

openApi {
	apiDocsUrl.set("http://localhost:3000/v3/api-docs")
}