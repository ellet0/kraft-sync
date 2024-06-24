package services.syncScriptInstaller

sealed class SyncScriptInstallationConfig {
    /**
     * Install the sync script
     * */
    data class Install(
        val syncScriptJarFilePath: String,
    ) : SyncScriptInstallationConfig()

    /**
     * Uninstall the sync script and delete all it's specific data and changes.
     * */
    data object UnInstall : SyncScriptInstallationConfig()
}
