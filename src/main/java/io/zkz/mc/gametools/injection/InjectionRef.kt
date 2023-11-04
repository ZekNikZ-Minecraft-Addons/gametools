package io.zkz.mc.gametools.injection

import kotlin.reflect.KClass
import kotlin.reflect.KProperty

class InjectionRef<T : Any>(private val type: KClass<out T>, private val container: InjectionContainer) {
    companion object {
        inline operator fun <reified T : Any> invoke(container: InjectionContainer) = InjectionRef(T::class, container)
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return container.get(type)
    }
}
