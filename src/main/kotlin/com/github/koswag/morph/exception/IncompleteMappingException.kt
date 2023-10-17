package com.github.koswag.morph.exception

import java.lang.Exception
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

class IncompleteMappingException(
    targetClass: KClass<*>,
    val missingMappings: Collection<KProperty<*>>,
) : Exception() {
    override val message =
        "There are missing mappings for properties - ${
            missingMappings.joinToString { 
                "${targetClass.qualifiedName}::${it.name}: ${it.returnType}"
            }
        }"
}
