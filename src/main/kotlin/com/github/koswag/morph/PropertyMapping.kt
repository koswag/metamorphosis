package com.github.koswag.morph

import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1

internal sealed interface PropertyMapping<Source : Any, SourceProp, TargetProp> : (Source) -> TargetProp {
    val sourceProp: KProperty1<Source, SourceProp>
    val targetProp: KProperty<TargetProp>

    data class DirectMapping<Source : Any, Prop>(
        override val sourceProp: KProperty1<Source, Prop>,
        override val targetProp: KProperty<Prop>,
    ) : PropertyMapping<Source, Prop, Prop> {
        override fun invoke(source: Source): Prop =
            sourceProp.invoke(source)
    }

    data class TransformMapping<Source : Any, SourceProp, TargetProp>(
        override val sourceProp: KProperty1<Source, SourceProp>,
        override val targetProp: KProperty<TargetProp>,
        val transform: (SourceProp) -> TargetProp,
    ) : PropertyMapping<Source, SourceProp, TargetProp> {
        override fun invoke(source: Source): TargetProp =
            transform(sourceProp.invoke(source))
    }
}
