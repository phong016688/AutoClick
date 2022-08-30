plugins {
    id("com.android.application") version "7.2.2" apply false
    id("com.android.library") version "7.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.7.10" apply false
    id("androidx.navigation.safeargs.kotlin") version "2.4.1" apply false
    kotlin("kapt") version "1.7.0" apply false
}

tasks.register("clean", Delete::class) { delete(rootProject.buildDir) }

tasks.register("Test") {
    doLast { println("Test completed") }
}

allprojects {
    configurations.all {
        resolutionStrategy.eachDependency {
            if (requested.group == "org.jetbrains.kotlin" && requested.name == "kotlin-reflect") {
                useVersion("1.7.10")
            }
        }
    }
}