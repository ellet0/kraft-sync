package services.hashGenerator

import okio.HashingSink
import okio.HashingSource
import okio.blackholeSink
import okio.buffer
import okio.source
import java.nio.file.Path

class OkioHashGenerator : HashGenerator {
    override suspend fun generateMD5(text: String): Result<String> =
        try {
            HashingSink.md5(blackholeSink()).use { hashingSink ->
                hashingSink.buffer().use { bufferedSink ->
                    bufferedSink.writeUtf8(text)
                }

                Result.success(hashingSink.hash.hex())
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }

    override suspend fun generateMD5(filePath: Path): Result<String> =
        try {
            filePath.source().use { fileSource ->
                HashingSource.md5(fileSource).use { hashingSource ->
                    hashingSource.buffer().use { source ->
                        source.readAll(blackholeSink())
                        Result.success(hashingSource.hash.hex())
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }

    override suspend fun generateSHA1(filePath: Path): Result<String> =
        try {
            filePath.source().use { fileSource ->
                HashingSource.sha1(fileSource).use { hashingSource ->
                    hashingSource.buffer().use { source ->
                        source.readAll(blackholeSink())
                        Result.success(hashingSource.hash.hex())
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }

    override suspend fun generateSHA256(filePath: Path): Result<String> =
        try {
            filePath.source().use { fileSource ->
                HashingSource.sha256(fileSource).use { hashingSource ->
                    hashingSource.buffer().use { source ->
                        source.readAll(blackholeSink())
                        Result.success(hashingSource.hash.hex())
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }

    override suspend fun generateSHA512(filePath: Path): Result<String> =
        try {
            filePath.source().use { fileSource ->
                HashingSource.sha512(fileSource).use { hashingSource ->
                    hashingSource.buffer().use { source ->
                        source.readAll(blackholeSink())
                        Result.success(hashingSource.hash.hex())
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
}
