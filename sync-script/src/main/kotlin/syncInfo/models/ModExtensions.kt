package syncInfo.models

import config.models.ScriptConfig
import syncInfo.models.mod.Mod
import utils.getFileNameFromUrlOrError
import utils.showErrorMessageAndTerminate
import java.nio.file.Path
import kotlin.io.path.name
import kotlin.system.exitProcess

/**
 * The name that will be used to display in the console or GUI
 * will use the [Mod.name] which is the mod display name / title
 * will use [getFileNameFromUrlOrError] as an alternative if it's not available
 * */
fun Mod.getDisplayName(): String = name ?: getFileNameFromUrlOrError(downloadUrl)

private fun Mod.shouldSyncOptionalModForCurrentEnvironment() =
    syncOptionalForCurrentEnv
        ?: SyncInfo.instance.modSyncInfo.syncOptionalModsForCurrentEnv

/**
 * If this mod should be downloaded on the current [Environment].
 *
 * For example, if this is a client side-only mod and the environment is [Environment.Server],
 * it will return false
 * */
fun Mod.shouldSyncOnCurrentEnvironment(): Boolean =
    when (ScriptConfig.getInstanceOrThrow().environment) {
        Environment.Client ->
            when (clientSupport) {
                Mod.ModSupport.Required -> true
                Mod.ModSupport.Optional -> shouldSyncOptionalModForCurrentEnvironment()
                Mod.ModSupport.Unsupported -> false
            }

        Environment.Server ->
            when (serverSupport) {
                Mod.ModSupport.Required -> true
                Mod.ModSupport.Optional -> shouldSyncOptionalModForCurrentEnvironment()
                Mod.ModSupport.Unsupported -> false
            }
    }

/**
 * Allow overriding the value for a specific mod, or all the mods, or use a global value for all the assets.
 * */
fun Mod.shouldVerifyFileIntegrity(): Boolean =
    verifyFileIntegrity ?: SyncInfo.instance.modSyncInfo.verifyFilesIntegrity
        ?: SyncInfo.instance.verifyAssetFilesIntegrity

suspend fun Mod.hasValidFileIntegrityOrError(modFilePath: Path): Boolean? =
    this.fileIntegrityInfo.hasValidIntegrity(filePath = modFilePath).getOrElse {
        showErrorMessageAndTerminate(
            title = "File Integrity Validation Error ⚠️",
            message = "An error occurred while validating the integrity of the mod file (${modFilePath.name}) \uD83D\uDCC1.",
        )
        // This will never reach due to the previous statement stopping the application
        exitProcess(1)
    }
