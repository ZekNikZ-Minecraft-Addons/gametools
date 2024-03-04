package io.zkz.mc.gametools.injection

import java.util.logging.Logger
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.primaryConstructor

class InjectionContainer {
    companion object {
        @JvmField
        val globalContainer = InjectionContainer()

        private val logger = Logger.getLogger("injection")
    }

    val nodes: MutableMap<InjectionKey, InjectionNode<*>> = mutableMapOf()

    fun <T : Any> get(type: KClass<T>, key: String = ""): T {
        val component = nodes[InjectionKey(type, key)]
        if (component != null && component.key.type == type) {
            @Suppress("UNCHECKED_CAST")
            return component.value as T
        }
        throw IllegalStateException("Component of type ${type.qualifiedName} is not injectable")
    }

    inline fun <reified T : Any> get(key: String = ""): T {
        val component = nodes[InjectionKey(T::class, key)]
        if (component != null && component.value is T) {
            return component.value as T
        }
        throw IllegalStateException("Component of type ${T::class.qualifiedName} is not injectable")
    }

    inline fun <reified T : Any> get(key: InjectionKey): T {
        val component = nodes[key]
        if (component != null && component.value is T) {
            return component.value as T
        }
        throw IllegalStateException("Component of type ${T::class.qualifiedName} is not injectable")
    }

    inline fun <reified T : Any> getAllOfType(): List<T> {
        @Suppress("UNCHECKED_CAST")
        return nodes.filter {
            it.key.type.isSubclassOf(T::class)
        }.values.map {
            it.value
        } as List<T>
    }

    fun getAllWhere(predicate: (InjectionKey) -> Boolean): List<Any> {
        return nodes.filter { predicate(it.key) }.values.map { it.value }
    }

    fun <T : Any> register(key: InjectionKey, builder: (InjectionContainer) -> T) {
        if (nodes.containsKey(key)) {
            throw IllegalStateException("Injection key already exists in container")
        }

        nodes[key] = InjectionNode(
            key,
            this,
            builder,
        )

        println("Registered injectable with key $key")
    }

    fun registerConstructorBuilder(key: InjectionKey) {
        if (nodes.containsKey(key)) {
            throw IllegalStateException("Injection key already exists in container")
        }

        if (key.type.primaryConstructor == null) {
            throw IllegalArgumentException("KClass does not have a primary constructor")
        }

        nodes[key] = InjectionNode(
            key,
            this,
        ) { container ->
            val constructor = key.type.primaryConstructor!!
            val params = constructor.parameters.map {
                val type = it.type.classifier
                container.get(type as KClass<*>)
            }
            try {
                constructor.call(*params.toTypedArray())
            } catch (e: IllegalArgumentException) {
                println("Error: $e")
                println("       invalid constructor")
                println("       constructor was ${constructor.returnType.classifier}::${constructor.name}")
                println("       constructor expected ${constructor.parameters.size} args of types: ${constructor.parameters.joinToString(", ") { it.type.classifier.toString() }}")
                println("       actual arguments were ${params.size} args of types: ${params.joinToString(", ") { it.javaClass.canonicalName }}")
            }
        }

        println("Registered injectable constructor with key $key")
    }

    fun query(builder: QueryBuilder.() -> Unit): List<Any> {
        val queryBuilder = QueryBuilder()
        queryBuilder.builder()
        return queryBuilder.performQuery(nodes)
    }

    @DslMarker
    annotation class QueryBuilderDSL

    @QueryBuilderDSL
    class QueryBuilder {
        private val queries = mutableListOf<InjectionQuery>()

        fun oneOf(vararg builders: QueryBuilder.() -> Unit) {
            val subqueries = builders.map {
                QueryBuilder().apply(it)
            }.map {
                it.queries
            }

            queries.add { entry ->
                subqueries.any {
                    it.all { query ->
                        query(entry)
                    }
                }
            }
        }

        fun subclassOf(clazz: KClass<*>) {
            queries.add {
                val res = it.key.type.isSubclassOf(clazz)
                if (!res) {
                    logger.fine("${it.key.type.simpleName} is NOT a subclass of ${clazz.simpleName}")
                }
                res
            }
        }

        fun annotatedWith(clazz: KClass<out Annotation>) {
            queries.add {
                it.key.type.annotations.any { annotation ->
                    annotation.annotationClass == clazz
                }
            }
        }

        fun primaryConstructorDependsOn(clazz: KClass<*>) {
            queries.add {
                val primaryConstructor = it.key.type.primaryConstructor
                primaryConstructor != null && primaryConstructor.parameters.any { parameter ->
                    parameter.type.classifier == clazz
                }
            }
        }

        fun declaredInPackage(packageName: String) {
            queries.add {
                val res = it.key.type.java.packageName.startsWith(packageName)
                if (!res) {
                    logger.fine("${it.key.type.qualifiedName} is NOT in package $packageName")
                }
                res
            }
        }

        fun filterByKey(predicate: (InjectionKey) -> Boolean) {
            queries.add {
                predicate(it.key)
            }
        }

        fun filterByValue(predicate: (Any) -> Boolean) {
            queries.add {
                predicate(it.value.value)
            }
        }

        internal fun performQuery(nodes: Map<InjectionKey, InjectionNode<*>>): List<Any> {
            nodes.forEach {
                logger.fine("Found node ${it.key}")
            }
            return nodes.filter { entry ->
                queries.all { query ->
                    query(entry)
                }
            }.values.map { it.value }
        }
    }
}

private typealias InjectionQuery = (Map.Entry<InjectionKey, InjectionNode<*>>) -> Boolean
