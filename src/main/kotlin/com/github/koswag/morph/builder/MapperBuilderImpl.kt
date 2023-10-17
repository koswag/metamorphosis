package com.github.koswag.morph.builder

import com.github.koswag.morph.Mapper
import com.github.koswag.morph.MapperImpl
import com.github.koswag.morph.PropertyMapping
import com.github.koswag.morph.PropertyMapping.DirectMapping
import com.github.koswag.morph.PropertyMapping.TransformMapping
import com.github.koswag.morph.util.getFields
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.full.primaryConstructor

internal class MapperBuilderImpl<Source : Any, Target : Any>(
    private val sourceClass: KClass<Source>,
    private val targetClass: KClass<Target>,
) : MapperBuilder<Source, Target> {

    override var inferMissingMappings = DEFAULT_MAPPING_INFERENCE_FLAG
    override var allowIncompleteMapping = DEFAULT_INCOMPLETE_MAPPING_FLAG

    private val mappings = mutableListOf<PropertyMapping<Source, *, *>>()

    private val targetConstructor = targetClass.primaryConstructor
        ?: error("Type $targetClass has no primary constructor")

    override fun <Prop> mapping(sourceProp: KProperty1<Source, Prop>, targetProp: KProperty1<Target, Prop>) {
        mappings.add(
            DirectMapping(sourceProp, targetProp)
        )
    }

    override fun <SourceProp, TargetProp> transformation(
        sourceProp: KProperty1<Source, SourceProp>,
        targetProp: KProperty1<Target, TargetProp>,
        transform: (SourceProp) -> TargetProp
    ) {
        mappings.add(
            TransformMapping(sourceProp, targetProp, transform)
        )
    }


    override fun build(): Mapper<Source, Target> {
        if (inferMissingMappings) {
            inferMissingMappings()
        }

        if (!allowIncompleteMapping) {
            validateMappings()
        }

        return MapperImpl(targetClass, mappings)
    }

    private fun inferMissingMappings() {
        val missingMappings = getMissingMappings()
        mappings.addAll(missingMappings)
    }

    private fun getMissingMappings(): Collection<DirectMapping<Source, *>> =
        getUnmappedTargets()
            .mapNotNull { unmappedTarget ->
                sourceClass.getFields()
                    .firstOrNull {
                        it.name == unmappedTarget.name
                            && it.returnType == unmappedTarget.returnType
                    }
                    ?.let { unmappedSource -> DirectMapping(unmappedSource, unmappedTarget) }
            }

    private fun validateMappings() {
        val unmappedTargets = getUnmappedTargets()

        if (unmappedTargets.isNotEmpty()) {
            val unmappedTargetsFmt = unmappedTargets.joinToString {
                "${targetClass.simpleName}::${it.name}: ${it.returnType}"
            }

            error("There are missing mappings for properties - $unmappedTargetsFmt")
        }
    }

    private fun getUnmappedTargets(): Collection<KProperty<*>> {
        val mappedTargets = mappings.map { it.targetProp }.toSet()
        val targetConstructorParameterNames = targetConstructor.parameters.map { it.name }.toSet()

        return targetClass.getFields()
            .filter { it !in mappedTargets && it.name in targetConstructorParameterNames }
    }


    companion object {
        private const val DEFAULT_MAPPING_INFERENCE_FLAG = true
        private const val DEFAULT_INCOMPLETE_MAPPING_FLAG = false
    }

}
