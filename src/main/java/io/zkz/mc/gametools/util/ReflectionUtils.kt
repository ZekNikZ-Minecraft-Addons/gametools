package io.zkz.mc.gametools.util

import org.reflections.Reflections
import org.reflections.util.ConfigurationBuilder
import java.io.IOException
import java.util.logging.Logger
import kotlin.reflect.KClass

private val logger = Logger.getLogger("injection")

/**
 * Scans all classes accessible from given class loader which belong
 * to the given package (or subpackages) which are annotated with the
 * given annotations.
 *
 * @param classLoader The class loader
 * @param packageName The base package
 * @param annotationClass The annotations to search for
 * @return The classes
 *
 * @throws ClassNotFoundException
 * @throws IOException
 */
@Throws(ClassNotFoundException::class, IOException::class)
fun findClassesAnnotatedWith(
    classLoader: ClassLoader,
    packageName: String,
    annotationClass: KClass<out Annotation>,
): List<KClass<*>> {
    logger.info("Looking for classes in package $packageName annotated with ${annotationClass.qualifiedName} from loader ${classLoader.name}")
    return Reflections(ConfigurationBuilder().forPackage(packageName, classLoader)).getTypesAnnotatedWith(annotationClass.java)
        .map { logger.fine("Found ${it.canonicalName}"); it.kotlin }
}

// @Throws(ClassNotFoundException::class, IOException::class)
// fun findClassesAnnotatedWith(
//    classLoader: ClassLoader,
//    packageName: String,
//    vararg annotationClass: KClass<out Annotation>,
// ): List<KClass<*>> {
//    val path = packageName.replace('.', '/')
//    val annotations = listOf(*annotationClass)
//    val resources: Enumeration<URL> = classLoader.getResources(path)
//
//    val dirs: MutableList<File> = ArrayList<File>()
//    while (resources.hasMoreElements()) {
//        val resource: URL = resources.nextElement()
//        val uri = URI(resource.toString())
//        dirs.add(File(uri.getPath()))
//    }
//
//    val classes: MutableList<KClass<*>> = ArrayList()
//    for (directory in dirs) {
//        classes.addAll(findClasses(directory, classLoader, packageName, annotations))
//    }
//
//    return classes
// }
//
// /**
// * Recursive method used to find all classes in a given directory and
// * subdirs.
// *
// * @param directory
// * The base directory
// * @param packageName
// * The package name for classes found inside the base directory
// * @return The classes
// * @throws ClassNotFoundException
// */
// @Throws(ClassNotFoundException::class)
// private fun findClasses(directory: File, classLoader: ClassLoader, packageName: String, annotationClasses: List<KClass<out Annotation>>): List<KClass<*>> {
//    val classes: MutableList<KClass<*>> = ArrayList()
//    if (!directory.exists()) {
//        return classes
//    }
//
//    val files = directory.listFiles() ?: arrayOf()
//    for (file in files) {
//        if (file.isDirectory()) {
//            classes.addAll(findClasses(file, classLoader, packageName + "." + file.getName(), annotationClasses))
//        } else if (file.getName().endsWith(".class")) {
//            val clazz = Class.forName(
//                packageName + '.' + file.getName().substring(0, file.getName().length - 6),
//                true,
//                classLoader,
//            ).kotlin
//
//            if (annotationClasses.all { clazz.annotations.any { annotation -> annotation.annotationClass == it } }) {
//                classes.add(clazz)
//            }
//        }
//    }
//
//    return classes
// }
