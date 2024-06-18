package syncInfo.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// When adding a new property, consider converting/import the new
// data from other launchers data format if possible

/**
 * The mod data, we initially wanted to use **sealed class** to use different providers:
 *
 * ```kotlin
 * sealed class Mod {
 *     data class Custom(val downloadUrl: String): ModProvider()
 *     data class CurseForge(val modId :String, val fileId: String): ModProvider()
 *     data class Modrinth(val modId: String, val fileId: String): ModProvider()
 * }
 * ```
 *
 * except then we have to send http requests for each mod based on the provider api,
 * and there might not be a support for a route for getting
 * all the mod download urls by mod ids.
 * It's requiring more time to run the script before launching the game
 *
 * And the api for some asset providers can be strict and require an API key
 *
 * */
@Serializable
data class Mod(
    /**
     * The public download url of the mod file
     * */
    val downloadUrl: String,
    /**
     * The [fileIntegrityInfo] contains different ways to validate a mod file integrity
     * */
    val fileIntegrityInfo: FileIntegrityInfo = FileIntegrityInfo(),
    /**
     * The mod name (optional) for now will be only used in the gui, if you don't specify it, then will get
     * the file name from [mod]
     * // TODO: Might rename this to title
     * */
    val name: String? = null,
    /**
     * Used to check if the mod required or not to allow the user to ignore some mods
     * // TODO: Currently not implemented or might removed
     * */
    val isRequired: Boolean = false,
    /**
     * The script needs this info to know if it should download the mod based on the [Environment]
     * if you're using a client side only and not needed in server,
     * pass [ModSupport.Required] to [clientSupport] and [ModSupport.Unsupported] to [serverSupport]
     * or if it's optional on the server, pass [ModSupport.Optional] to [serverSupport].
     *
     * If it's required on both sides, pass [ModSupport.Required] to both [clientSupport] and [serverSupport]
     * */
    val clientSupport: ModSupport = ModSupport.Required,
    /**
     * The script needs this info to know if it should download the mod based on the [Environment]
     * if you're using a server side only and not needed in a client,
     * pass [ModSupport.Required] to [serverSupport] and [ModSupport.Unsupported] to [clientSupport]
     * or if it's optional on the client, pass [ModSupport.Optional] to [clientSupport].
     *
     * If it's required on both sides, pass [ModSupport.Required] to both [clientSupport] and [serverSupport]
     * */
    val serverSupport: ModSupport = ModSupport.Required,
    /**
     * The description of the mod might be used in GUI or console
     * while downloading the mods, for example
     * */
    val description: String? = null,
    /**
     * Will override [SyncInfo.shouldVerifyModFilesIntegrity] and [SyncInfo.shouldVerifyAssetFilesIntegrity]
     * for a specific mod
     *
     * @see SyncInfo.shouldVerifyModFilesIntegrity for more details.
     * */
    val overrideShouldVerifyFileIntegrity: Boolean? = null,
) {
    @Serializable
    enum class ModSupport {
        @SerialName("required")
        Required,

        @SerialName("optional")
        Optional,

        @SerialName("unsupported")
        Unsupported,
    }
}