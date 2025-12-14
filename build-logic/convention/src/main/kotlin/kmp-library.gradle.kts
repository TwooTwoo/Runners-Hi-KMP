import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

class KmpLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jetbrains.kotlin.multiplatform")
            }

            extensions.configure<org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension> {
                applyDefaultHierarchyTemplate()

                androidTarget {
                    compilerOptions {
                        jvmTarget.set(JvmTarget.JVM_11)
                    }
                }

                listOf(
                    iosArm64(),
                    iosSimulatorArm64()
                ).forEach { iosTarget ->
                    iosTarget.binaries.framework {
                        baseName = project.name
                        isStatic = true
                    }
                }

                jvm()

                sourceSets {
                    val commonMain by getting
                    val commonTest by getting

                    val jvmMain = create("jvmMain") {
                        dependsOn(commonMain)
                    }
                    val androidMain by getting {
                        dependsOn(jvmMain)
                    }
                    val jvmTest = create("jvmTest") {
                        dependsOn(commonTest)
                    }

                    val iosMain = create("iosMain") {
                        dependsOn(commonMain)
                    }
                    val iosArm64Main by getting {
                        dependsOn(iosMain)
                    }
                    val iosSimulatorArm64Main by getting {
                        dependsOn(iosMain)
                    }
                }
            }
        }
    }
}
