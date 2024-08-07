package syncInfo.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Needed by the script to know if we are syncing on the server or a client
 * This is required, so we know if we should install the mod or not based on [Mod.clientSupport] and [Mod.serverSupport]
 *
 * one other example why this is needed, the shaders and resource-packs should not be synced on the server and will be
 * ignored because those are client side resources
 * */
@Serializable
enum class Environment {
    @SerialName("client")
    Client,

    @SerialName("server")
    Server,

    ;

    fun isServer() = this == Server

    fun isClient() = this == Client

    override fun toString(): String =
        when (this) {
            Client -> "Client"
            Server -> "Server"
        }
}
