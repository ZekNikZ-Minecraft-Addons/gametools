package io.zkz.mc.gametools.data

interface IDataManager {
    fun load()
    fun save()

    val autoSave: Boolean
}
