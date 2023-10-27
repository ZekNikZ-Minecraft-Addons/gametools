package io.zkz.mc.gametools.injection

import kotlin.reflect.KClass

data class InjectionKey(
    val type: KClass<*>,
    val key: String
)

data class InjectionNode<T : Any>(
    val key: InjectionKey,
    val container: InjectionContainer,
    val builder: (InjectionContainer) -> T,
) {
    val value: T by lazy {
        if (inConstruction) {
            throw IllegalStateException("Circular dependencies detected")
        }

        inConstruction = true
        val result = builder(container)
        inConstruction = false

        return@lazy result
    }

    var inConstruction: Boolean = false
}