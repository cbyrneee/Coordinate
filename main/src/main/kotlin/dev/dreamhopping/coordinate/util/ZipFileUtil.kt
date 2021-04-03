package dev.dreamhopping.coordinate.util

import java.io.Closeable
import java.io.File
import java.net.URL
import java.nio.charset.Charset
import java.nio.file.Files
import java.util.zip.ZipFile

class ZipFileUtil(zipURL: URL) : Closeable {
    private val temporaryDirectory = Files.createTempDirectory("coordinate-temp").toFile()
    private val zip = File(temporaryDirectory, "temporaryZip.zip")
    private val zipFile: ZipFile

    init {
        zip.writeBytes(zipURL.readBytes())
        zipFile = ZipFile(zip)
    }

    fun getEntry(name: String) = zipFile.getEntry(name)

    fun readEntryText(entryName: String) =
        zipFile.getInputStream(getEntry(entryName)).readBytes().toString(Charset.defaultCharset())

    override fun close() {
        temporaryDirectory.deleteRecursively()
    }
}
