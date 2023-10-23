package com.github.koswag.morph.mapper.builder

import com.github.koswag.morph.exception.IncompleteMappingException
import com.github.koswag.morph.mapper.Mapper
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

/**
 * Represents a builder for mapper from type [Source] to type [Target].
 */
interface MapperBuilder<Source : Any, Target : Any> {

    /**
     * Decides whether missing mappings should be inferred.
     */
    var inferMissingMappings: Boolean

    /**
     * Registers a direct mapping from [sourceProp] to [targetProp].
     */
    fun <Prop> mapping(
        sourceProp: KProperty1<Source, Prop>,
        targetProp: KProperty1<Target, Prop>,
    )

    /**
     * Registers a mapping transforming [sourceProp] to [targetProp] using [transform] function.
     */
    fun <SourceProp, TargetProp> mapping(
        sourceProp: KProperty1<Source, SourceProp>,
        targetProp: KProperty1<Target, TargetProp>,
        transform: (SourceProp) -> TargetProp,
    )

    /**
     * Builds a mapper, inferring missing mappings when [inferMissingMappings] is set to `true`.
     * @throws [IncompleteMappingException] if mapping is incomplete.
     */
    fun build(): Mapper<Source, Target>


    /**
     * Registers a direct property mapping.
     * @receiver source property of type [Prop]
     * @param [targetProp] target property of type [Prop]
     */
    infix fun <Prop> KProperty1<Source, Prop>.mappedTo(targetProp: KProperty1<Target, Prop>) {
        mapping(this, targetProp)
    }

    /**
     * Begins a transformation mapping declaration.
     *
     * Note: Does not register any mapping when not followed by [using] method call.
     * @receiver Source property of type [SourceProp]
     * @param [targetProp] target property of type [TargetProp]
     * @return Transformation builder context for further transformation definition
     */
    infix fun <SourceProp, TargetProp> KProperty1<Source, SourceProp>.transformedTo(
        targetProp: KProperty1<Target, TargetProp>,
    ): TransformationBuilder<Source, SourceProp, Target, TargetProp> =
        TransformationBuilder(this, targetProp)

    /**
     * Registers a transform mapping from type [SourceProp] to type [TargetProp].
     * @receiver transformation builder context
     * @param [transform] property mapping function from type [SourceProp] to type [TargetProp]
     */
    infix fun <SourceProp, TargetProp> TransformationBuilder<Source, SourceProp, Target, TargetProp>.using(
        transform: (SourceProp) -> TargetProp,
    ) {
        mapping(sourceProp, targetProp, transform)
    }


    companion object {
        /**
         * Represents a transformation mapping builder context from property [sourceProp] to property [targetProp].
         */
        data class TransformationBuilder<Source : Any, SourceProp, Target : Any, TargetProp>(
            val sourceProp: KProperty1<Source, SourceProp>,
            val targetProp: KProperty1<Target, TargetProp>,
        )

        /**
         * Creates a mapping builder from [sourceClass] to [targetClass],
         * with mappings registered inside [mappingBlock].
         */
        operator fun <Source : Any, Target : Any> invoke(
            sourceClass: KClass<Source>,
            targetClass: KClass<Target>,
            mappingBlock: MapperBuilder<Source, Target>.() -> Unit
        ): MapperBuilder<Source, Target> =
            MapperBuilderImpl(sourceClass, targetClass)
                .apply(mappingBlock)
    }
}
