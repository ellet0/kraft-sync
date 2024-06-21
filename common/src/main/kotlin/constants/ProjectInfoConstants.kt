package constants

object ProjectInfoConstants {
    const val REPOSITORY_LINK = "https://github.com/ellet0/kraft-sync"
    const val DISPLAY_NAME = "Kraft Sync"
    const val NORMALIZED_NAME = "kraft-sync"

    // At the moment, we don't have a website; this will make it easier to provide a link to it later
    const val WEBSITE = REPOSITORY_LINK

    const val LIBS_VERSIONS_TOML_FILE_URL =
        "https://raw.githubusercontent.com/ellet0/kraft-sync/main/gradle/libs.versions.toml"

    const val LATEST_SYNC_SCRIPT_JAR_FILE_URL = "https://github.com/ellet0/kraft-sync/releases/download/latest/kraft-sync.min.jar"
}
