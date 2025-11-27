import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.androidLint)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    `maven-publish`
}

//https://docs.github.com/zh/packages/working-with-a-github-packages-registry/working-with-the-gradle-registry
// build.gradle.kts (在你的 Compose Multiplatform 模块中)

val GITHUB_USERNAME = "simplepeng" // 例如：octocat
val REPO_NAME = "cmp-x" // 例如：compose-library

// 在 publishing 块中配置仓库
publishing {
    repositories {
        maven {
            // 这是 GitHub Packages 的 URL 格式
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/$GITHUB_USERNAME/$REPO_NAME")

            // 配置认证信息
            credentials {
                // 在 CI 环境中，使用 GH_TOKEN 或我们定义的 GPR_TOKEN
                username = System.getenv("GITHUB_ACTOR") ?: GITHUB_USERNAME // CI 环境中会使用 GITHUB_ACTOR

                // 从环境变量中获取 Token
                password = System.getenv("GPR_TOKEN") ?: project.findProperty("gprToken") as String? ?: ""
            }
        }
    }
    // 确保为所有组件创建发布
    publications {
        // Compose Multiplatform 插件通常会为你生成这些 publication
        withType<MavenPublication> {
            groupId = "com.github.simplepeng"
            artifactId = "cmp-x"
            version = "0.0.6"
        }
    }
}

kotlin {

    // Target declarations - add or remove as needed below. These define
    // which platforms this KMP module supports.
    // See: https://kotlinlang.org/docs/multiplatform-discover-project.html#targets
    androidLibrary {
        namespace = "simple.cmp.x"
        compileSdk = 36
        minSdk = 24

        withHostTestBuilder {
        }

        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }

    // For iOS targets, this is also where you should
    // configure native binary output. For more information, see:
    // https://kotlinlang.org/docs/multiplatform-build-native-binaries.html#build-xcframeworks

    // A step-by-step guide on how to include this library in an XCode
    // project can be found here:
    // https://developer.android.com/kotlin/multiplatform/migrate
    val xcfName = "sharedKit"

    iosX64 {
        binaries.framework {
            baseName = xcfName
        }
    }

    iosArm64 {
        binaries.framework {
            baseName = xcfName
        }
    }

    iosSimulatorArm64 {
        binaries.framework {
            baseName = xcfName
        }
    }

    jvm()

    js {
        browser()
        binaries.executable()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }

    // Source set declarations.
    // Declaring a target automatically creates a source set with the same name. By default, the
    // Kotlin Gradle Plugin creates additional source sets that depend on each other, since it is
    // common to share sources between related targets.
    // See: https://kotlinlang.org/docs/multiplatform-hierarchy.html
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlin.stdlib)
                // Add KMP dependencies here
                implementation(compose.runtime)
                implementation(compose.foundation)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        androidMain {
            dependencies {
                // Add Android-specific dependencies here. Note that this source set depends on
                // commonMain by default and will correctly pull the Android artifacts of any KMP
                // dependencies declared in commonMain.
            }
        }

        getByName("androidDeviceTest") {
            dependencies {
                implementation(libs.androidx.runner)
                implementation(libs.androidx.core)
                implementation(libs.androidx.testExt.junit)
            }
        }

        iosMain {
            dependencies {
                // Add iOS-specific dependencies here. This a source set created by Kotlin Gradle
                // Plugin (KGP) that each specific iOS target (e.g., iosX64) depends on as
                // part of KMP’s default source set hierarchy. Note that this source set depends
                // on common by default and will correctly pull the iOS artifacts of any
                // KMP dependencies declared in commonMain.
            }
        }
        jsMain {
            dependencies {
            }
        }
        jvmMain {
            dependencies {
            }
        }
        wasmJsMain {
            dependencies {
            }
        }
        webMain {
            dependencies {
            }
        }
    }

}