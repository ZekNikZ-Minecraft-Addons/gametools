package io.zkz.mc.gametools.injection

import kotlin.reflect.KClass

interface InjectionComponent {
    val injectionContainer: InjectionContainer get() = InjectionContainer.globalContainer
}

fun <T : Any> InjectionComponent.get(type: KClass<T>): T {
    return injectionContainer.get(type)
}

inline fun <reified T> InjectionComponent.get(): T {
    return injectionContainer.get()
}

inline fun <reified T : Any> InjectionComponent.inject(): InjectionRef<T> {
    return InjectionRef(injectionContainer)
}
