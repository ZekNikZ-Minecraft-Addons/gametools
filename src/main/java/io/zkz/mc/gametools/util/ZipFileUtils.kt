package io.zkz.mc.gametools.util

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object ZipFileUtils {
    @Throws(IOException::class)
    fun zipDirectory(dir: File, zipFile: File) {
        val fout = FileOutputStream(zipFile)
        val zout = ZipOutputStream(fout)
        zipSubDirectory("", dir, zout)
        zout.close()
    }

    @Throws(IOException::class)
    private fun zipSubDirectory(basePath: String, dir: File, zout: ZipOutputStream) {
        val buffer = ByteArray(4096)
        val files: Array<File> = dir.listFiles() ?: return

        for (file in files) {
            if (file.isDirectory()) {
                val path = basePath + file.getName() + "/"
                zout.putNextEntry(ZipEntry(path))
                zipSubDirectory(path, file, zout)
                zout.closeEntry()
            } else {
                FileInputStream(file).use { fin ->
                    zout.putNextEntry(ZipEntry(basePath + file.getName()))
                    var length: Int
                    while ((fin.read(buffer).also { length = it }) > 0) {
                        zout.write(buffer, 0, length)
                    }
                    zout.closeEntry()
                }
            }
        }
    }
}
