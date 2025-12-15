plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.kotlinSpring)
    alias(libs.plugins.springBoot)
    alias(libs.plugins.springDependencyManagement)
    alias(libs.plugins.kotlinx.serialization)
}

group = "good.space.runnershi"
version = "1.0.0"

dependencies {
    implementation(libs.mysql.connector)
    implementation(projects.shared)
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.json)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.spring.boot.starter.data.jpa)
    testImplementation(libs.spring.boot.starter.test)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation(libs.kotlin.reflect)
}


kotlin {
    jvmToolchain(17)
}

tasks.withType<Test> {
    useJUnitPlatform()
}