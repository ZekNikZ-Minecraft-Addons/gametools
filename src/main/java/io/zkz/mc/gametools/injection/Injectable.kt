package io.zkz.mc.gametools.injection

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class Injectable(val key: String = "")