package com.github.koswag.morph.builder

import kotlin.reflect.KProperty1

interface MapperBuilder<Source : Any, Target : Any> {
    var inferMissingMappings: Boolean
    var allowIncompleteMapping: Boolean

    fun <Prop> mapping(
        sourceProp: KProperty1<Source, Prop>,
        targetProp: KProperty1<Target, Prop>,
    )

    fun <SourceProp, TargetProp> transformation(
        sourceProp: KProperty1<Source, SourceProp>,
        targetProp: KProperty1<Target, TargetProp>,
        transform: (SourceProp) -> TargetProp,
    )


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
    }
}
