package io.zkz.mc.gametools.util

import java.io.File
import java.io.OutputStream
import java.security.DigestInputStream
import java.security.MessageDigest

object HashUtils {
    @OptIn(ExperimentalStdlibApi::class)
    fun sha1Hash(file: File): String {
        val digest = MessageDigest.getInstance("SHA-1")
        DigestInputStream(file.inputStream(), digest).use {
            it.transferTo(OutputStream.nullOutputStream())
            return it.messageDigest.digest().toHexString()
        }
    }
}
