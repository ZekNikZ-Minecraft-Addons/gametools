package io.zkz.mc.gametools.injection

import kotlin.reflect.KClass

data class InjectionKey(
    val type: KClass<*>,
    val key: String
)