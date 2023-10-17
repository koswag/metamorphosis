package com.github.koswag.morph

import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.primaryConstructor

internal class MapperImpl<Source : Any, Target : Any>(
    targetClass: KClass<Target>,
    private val mappings: Collection<PropertyMapping<Source, *, *>>,
) : Mapper<Source, Target> {

    private val targetConstructor = targetClass.primaryConstructor
        ?: error("Type $targetClass has no primary constructor")

    override fun map(source: Source): Target {
        val callParameters = getTargetMappingsFor(source)
            .mapKeys { (targetProp, _) ->
                targetConstructor.parameters
                    .first { it.name == targetProp.name }
            }

        return targetConstructor.callBy(callParameters)
    }

    private fun getTargetMappingsFor(source: Source): Map<KProperty<*>, *> =
        mappings.associate { mapping ->
            mapping.targetProp to mapping(source)
        }

}