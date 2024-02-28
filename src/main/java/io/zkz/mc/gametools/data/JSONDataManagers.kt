package io.zkz.mc.gametools.data

import com.google.gson.GsonBuilder
import java.io.File
import kotlin.reflect.KClass

open class JSONDataManager<T : Any>(
    private val filePath: String,
    private val clazz: KClass<T>,
    val onLoad: (T) -> Unit,
    val onSave: () -> T,
    override val autoSave: Boolean = true,
) : IDataManager {
    private val gson = GsonBuilder().create()

    override fun load() {
        val json = File(filePath).readText()
        val res = gson.fromJson(json, clazz.java)
        onLoad(res)
    }

    override fun save() {
        val data = onSave()
        val json = gson.toJson(data)
        File(filePath).writeText(json)
    }
}

class JSONDelegate<T : Any>(
    filePath: String,
    clazz: KClass<T>,
    defaultValue: () -> T,
    override val autoSave: Boolean = true,
) : IDataManager {
    private var data: T = defaultValue()
        set(value) {
            field = value
            if (autoSave) {
                dataManager.save()
            }
        }

    private val dataManager: JSONDataManager<T> = JSONDataManager(
        filePath,
        clazz,
        { data = it },
        { data },
        autoSave,
    )

    override fun load() {
        dataManager.load()
    }

    override fun save() {
        dataManager.save()
    }
}

inline fun <reified T : Any> jsonSynced(
    filePath: String,
    autoSave: Boolean = true,
    noinline defaultValue: () -> T,
): JSONDelegate<T> {
    return JSONDelegate(filePath, T::class, defaultValue, autoSave)
}
