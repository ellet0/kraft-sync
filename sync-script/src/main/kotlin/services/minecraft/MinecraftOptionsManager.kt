package services.minecraft

import constants.SyncScriptInstanceFiles
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.annotations.VisibleForTesting
import java.io.File

/**
 * Helper class for reading [SyncScriptInstanceFiles.Options] file which contains Minecraft settings
 * for reading and getting the properties or set them if it doesn't exist
 * */
object MinecraftOptionsManager {
    private var optionsFile = SyncScriptInstanceFiles.Options.file
    private val properties: MutableMap<String, String> = mutableMapOf()
    // TODO: Might add something like isLoaded, check if false in readProperty and load them
    // TODO: Currently it's possible to read and write without using loadPropertiesFromFile which might cause unexpected behavior

    @VisibleForTesting
    fun setOptionsFileForTests(file: File) {
        optionsFile = file
    }

    @VisibleForTesting
    fun getPropertiesForTests(): Map<String, String> = properties

    /**
     * Load the [properties] from [optionsFile]
     *
     * @throws IllegalArgumentException If [optionsFile] doesn't exist
     * @throws IndexOutOfBoundsException If the text of [optionsFile] is invalid
     * */
    fun loadPropertiesFromFile(): Result<Unit> {
        return try {
            require(optionsFile.exists()) { "The file ${optionsFile.name} doesn't exist in ${optionsFile.path}" }
            if (properties.isNotEmpty()) {
                properties.clear()
            }
            optionsFile.forEachLine { line ->
                val (key, value) = line.split(":", limit = 2)
                val trimmedKey = key.trim()
                val trimmedValue = value.trim()
                properties[trimmedKey] = trimmedValue
            }
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    enum class Property(val key: String) {
        ResourcePacks("resourcePacks"),
        IncompatibleResourcePacks("incompatibleResourcePacks"),
        Lang("lang"),
    }

    /**
     * Should call [loadPropertiesFromFile] before calling this
     * @IllegalArgumentException If the property doesn't exist in [optionsFile]
     * */
    fun readProperty(property: Property): Result<String> {
        return try {
            val propertyKey = property.key
            val propertyValue = properties[propertyKey]
            requireNotNull(
                propertyValue,
            ) { "The key property $propertyKey doesn't exist in the ${optionsFile.name} in ${optionsFile.path}" }
            println(properties)
            Result.success(propertyValue)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun readAsList(property: Property): List<String>? {
        val resourcePacksAsString = readProperty(property).getOrNull() ?: return null
        return Json.decodeFromString<List<String>>(resourcePacksAsString)
    }

    fun clear() {
        properties.clear()
        optionsFile.writeText(text = "")
    }

    fun setProperty(
        property: Property,
        propertyValue: String,
    ): Result<Unit> {
        return try {
            properties[property.key] = propertyValue

            optionsFile.bufferedWriter().use { bufferedWriter ->
                properties.forEach { (key, value) ->
                    bufferedWriter.appendLine("$key:$value")
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun setPropertyAsList(
        property: Property,
        propertyValue: List<String>,
    ): Result<Unit> {
        return setProperty(property, Json.encodeToString<List<String>>(propertyValue))
    }

    sealed class ResourcePack(private val value: String) {
        data class File(val resourcePackZipFileName: String) : ResourcePack(value = resourcePackZipFileName)

        data class BuiltIn(val builtInResourcePackName: String) : ResourcePack(value = builtInResourcePackName)

        /**
         * Return Minecraft specific value of key [Property.ResourcePacks]
         * */
        fun toValue(): String {
            return when (this) {
                is BuiltIn -> value
                is File -> "file/$value"
            }
        }

        override fun toString(): String = toValue()

        companion object {
            /**
             * Get instance of [ResourcePack] by the Minecraft specific value of [Property.ResourcePacks]
             * */
            fun getByValue(value: String): ResourcePack {
                if (value.startsWith("file/", ignoreCase = true)) {
                    return File(resourcePackZipFileName = value.replaceFirst("file/", ""))
                }
                return BuiltIn(builtInResourcePackName = value)
            }
        }
    }

    fun readResourcePacks(): List<ResourcePack>? {
        return readAsList(Property.ResourcePacks)?.map { ResourcePack.getByValue(it) }
    }

    fun setResourcePacks(resourcePacks: List<ResourcePack>): Result<Unit> {
        return setPropertyAsList(Property.ResourcePacks, resourcePacks.map { it.toValue() })
    }

    fun readIncompatibleResourcePacks(): List<ResourcePack>? {
        return readAsList(Property.IncompatibleResourcePacks)?.map { ResourcePack.getByValue(it) }
    }

    fun setIncompatibleResourcePacks(resourcePacks: List<ResourcePack>): Result<Unit> {
        return setPropertyAsList(Property.IncompatibleResourcePacks, resourcePacks.map { it.toValue() })
    }
}