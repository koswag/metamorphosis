package com.github.koswag.morph.builder

import com.github.koswag.morph.PropertyMapping
import com.github.koswag.morph.PropertyMapping.DirectMapping
import com.github.koswag.morph.PropertyMapping.TransformMapping
import kotlin.reflect.KProperty1

class MapperBuilderImpl<Source : Any, Target : Any> : MapperBuilder<Source, Target> {
    override var inferMissingMappings = DEFAULT_MAPPING_INFERENCE_FLAG
    override var allowIncompleteMapping = DEFAULT_INCOMPLETE_MAPPING_FLAG

    private val mappings = mutableListOf<PropertyMapping<Source, *, *>>()

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

    companion object {
        private const val DEFAULT_MAPPING_INFERENCE_FLAG = true
        private const val DEFAULT_INCOMPLETE_MAPPING_FLAG = false
    }
}