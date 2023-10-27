package io.zkz.mc.gametools.reflection

/**
 * Marks this method as a command registry. The method may or may not be public, but must have the following signature: <pre>static void methodName(CommandRegistry registry)</pre>
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
annotation class RegisterCommands 