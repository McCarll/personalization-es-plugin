rootProject.name = "personalization-plugin"

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven {
            url = uri("https://artifacts.elastic.co/maven")
        }
    }
}