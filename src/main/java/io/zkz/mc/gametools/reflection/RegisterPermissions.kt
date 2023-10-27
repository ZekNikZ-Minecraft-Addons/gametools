package io.zkz.mc.gametools.reflection

/**
 * Finds all static fields of the annotated class of type Permissions and registers them.
 */
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
annotation class RegisterPermissions 