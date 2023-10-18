package com.github.koswag.morph.mapper.builder

import com.github.koswag.morph.mapper.Mapper
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

interface MapperBuilder<Source : Any, Target : Any> {
    var inferMissingMappings: Boolean

    fun <Prop> mapping(
        sourceProp: KProperty1<Source, Prop>,
        targetProp: KProperty1<Target, Prop>,
    )

    fun <SourceProp, TargetProp> transformation(
        sourceProp: KProperty1<Source, SourceProp>,
        targetProp: KProperty1<Target, TargetProp>,
        transform: (SourceProp) -> TargetProp,
    )

    fun build(): Mapper<Source, Target>


    infix fun <Prop> KProperty1<Source, Prop>.mappedTo(targetProp: KProperty1<Target, Prop>) {
        mapping(this, targetProp)
    }

    infix fun <SourceProp, TargetProp> KProperty1<Source, SourceProp>.transformedTo(
        targetProp: KProperty1<Target, TargetProp>,
    ): TransformationBuilder<Source, SourceProp, Target, TargetProp> =
        TransformationBuilder(this, targetProp)

    infix fun <SourceProp, TargetProp> TransformationBuilder<Source, SourceProp, Target, TargetProp>.using(
        transform: (SourceProp) -> TargetProp,
    ) {
        transformation(sourceProp, targetProp, transform)
    }


    companion object {
        data class TransformationBuilder<Source : Any, SourceProp, Target : Any, TargetProp>(
            val sourceProp: KProperty1<Source, SourceProp>,
            val targetProp: KProperty1<Target, TargetProp>,
        )

        operator fun <Source : Any, Target : Any> invoke(
            sourceClass: KClass<Source>,
            targetClass: KClass<Target>,
            block: MapperBuilder<Source, Target>.() -> Unit
        ): MapperBuilder<Source, Target> =
            MapperBuilderImpl(sourceClass, targetClass)
                .apply(block)
    }
}
