package io.zkz.mc.gametools.data

import com.google.gson.GsonBuilder
import java.nio.file.Files
import java.nio.file.Path
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

open class JSONDataManager<T : Any>(
    private val filePath: Path,
    private val clazz: KClass<T>,
    val onLoad: (T) -> Unit,
    val onSave: () -> T,
    override val autoSave: Boolean = true,
) : IDataManager {
    private val gson = GsonBuilder().serializeNulls().create()

    override fun load() {
        if (!Files.exists(filePath)) {
            save()
        }

        val json = Files.readString(filePath)
        val res = gson.fromJson(json, clazz.java)
        onLoad(res)
    }

    override fun save() {
        val data = onSave()
        val json = gson.toJson(data)
        Files.writeString(filePath, json)
    }
}

class JSONDelegate<T : Any>(
    filePath: Path,
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

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return data
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        data = value
    }
}

inline fun <reified T : Any> jsonSynced(
    filePath: Path,
    autoSave: Boolean = true,
    noinline defaultValue: () -> T,
): JSONDelegate<T> {
    return JSONDelegate(filePath, T::class, defaultValue, autoSave)
}
