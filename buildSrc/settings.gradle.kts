// Explicitly setting for caching with Gradle project accessors to fix:
// Project accessors enabled, but root project name not explicitly set for 'buildSrc'. Checking out the project
// in different folders will impact the generated code and implicitly the buildscript classpath, breaking caching.
rootProject.name = "buildSrc"

// To use Gradle Version catalogs
dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}
