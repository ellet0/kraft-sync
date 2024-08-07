package utils

import utils.os.OperatingSystem
import java.nio.file.Path
import java.util.concurrent.TimeUnit
import kotlin.io.path.absolutePathString

/**
 * Run a command in the command line of the system
 * @param args The arguments that is used to run the command, the reason this is not a String to
 * prevent some issues and bugs with the paths or the arguments that have spacing
 * @param reasonOfRunningTheCommand This is used to tell the user why we're running this command on the system.
 * Should be something like `to check if the system in dark mode`
 * @param isLoggingEnabled Should we print the result, output and errors to the log?
 * */
fun commandLine(
    vararg args: String,
    reasonOfRunningTheCommand: String?,
    isLoggingEnabled: Boolean = true,
): Result<String> {
    if (isLoggingEnabled) {
        val message =
            buildString {
                append("\uD83D\uDD27 Running the command")
                reasonOfRunningTheCommand?.let {
                    append(" $it")
                }
                append(": ${args.joinToString(" ")}")
            }
        Logger.info { message }
    }
    return try {
        val process =
            ProcessBuilder(listOf(*args))
                .redirectErrorStream(true)
                .start()
        val isCompleted = process.waitFor(1, TimeUnit.MINUTES)
        if (!isCompleted) {
            process.destroy()
            val errorMessage = "⏰ Process timed out for the command: ${args.joinToString(" ")}"
            if (isLoggingEnabled) {
                Logger.error { "❌ $errorMessage" }
            }
            return Result.failure(RuntimeException(errorMessage))
        }
        val result = process.inputStream.bufferedReader().use { it.readText() }
        if (isLoggingEnabled) {
            Logger.info {
                "✅ Command executed successfully: '${args.joinToString(" ")}'. " + "📜 Output: ${result.trim()}"
            }
        }
        Result.success(result)
    } catch (e: Exception) {
        if (isLoggingEnabled) {
            Logger.error {
                "❌ Error executing command `${args.joinToString(" ")}`: ${e.message}"
            }
        }
        e.printStackTrace()
        Result.failure(e)
    }
}

/**
 * Similar to [commandLine] without logging and waiting for the result.
 * It doesn't handle the output or specific error to the output of the command.
 * */
fun commandLineNonBlocking(vararg args: String): Result<Unit> =
    runCatching {
        ProcessBuilder(listOf(*args))
            .start()
    }

/**
 * Has the similar functionality as [commandLine] specifically for using powershell commands in Windows
 * */
fun powerShellCommandLine(
    vararg args: String,
    reasonOfRunningTheCommand: String?,
    isLoggingEnabled: Boolean = true,
): Result<String> {
    if (!OperatingSystem.current.isWindows()) {
        return Result.failure(UnsupportedOperationException("Powershell commands can be only executed in Windows \uD83E\uDE9F"))
    }
    return commandLine(
        "powershell.exe",
        "-Command",
        *args,
        reasonOfRunningTheCommand = reasonOfRunningTheCommand,
        isLoggingEnabled = isLoggingEnabled,
    )
}

/**
 * Launch a bat script file on **Microsoft Windows** in a new window which will **prevent blocking the code execution**
 * the code execution of the bat script will continue to work even if the application has been closed
 * @throws IllegalStateException If the current operating system is not [OperatingSystem.Windows]
 * */
fun executeBatchScriptInSeparateWindow(batScriptFilePath: Path) {
    if (!OperatingSystem.current.isWindows()) {
        throw IllegalStateException("Bat script can be only executed on Windows.")
    }
    ProcessBuilder("cmd", "/c", "start", batScriptFilePath.absolutePathString()).start()
}
