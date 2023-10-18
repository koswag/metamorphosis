package com.github.koswag.morph

import com.github.koswag.morph.mapper.Mapper
import com.github.koswag.morph.mapper.builder.MapperBuilder
import java.util.NoSuchElementException
import kotlin.reflect.KClass

class DataMorph {
    private val mappers = mutableMapOf<Pair<KClass<*>, KClass<*>>, Mapper<*, *>>()

    fun canMap(mapping: Pair<KClass<*>, KClass<*>>): Boolean =
        mapping in mappers

    operator fun contains(mapping: Pair<KClass<*>, KClass<*>>): Boolean =
        canMap(mapping)

    operator fun get(mapping: Pair<KClass<*>, KClass<*>>): Mapper<*, *>? =
        mappers[mapping]


    inline fun <reified Source : Any, reified Target : Any> withMapping(): DataMorph =
        withMapping<Source, Target> { }

    inline fun <reified Source : Any, reified Target : Any> withMapping(
        noinline mappingBlock: MapperBuilder<Source, Target>.() -> Unit,
    ): DataMorph =
        apply { addMapping(Source::class, Target::class, mappingBlock) }

    fun <Source : Any, Target : Any> addMapping(
        sourceClass: KClass<Source>,
        targetClass: KClass<Target>,
        mappingBlock: MapperBuilder<Source, Target>.() -> Unit,
    ) {
        val mapper = MapperBuilder(sourceClass, targetClass, mappingBlock).build()
        mappers[sourceClass to targetClass] = mapper
    }


    inline fun <reified Source : Any, reified Target : Any> mapCatching(source: Source): Result<Target> =
        runCatching { map(source) }

    inline fun <reified Source : Any, reified Target : Any> map(source: Source): Target =
        mapOrNull<Source, Target>(source)
            ?: throw NoSuchElementException("Mapper (${Source::class} -> ${Target::class}) not found")

    inline fun <reified Source : Any, reified Target : Any> mapOrNullCatching(source: Source): Result<Target?> =
        runCatching { mapOrNull(source) }

    @Suppress("UNCHECKED_CAST")
    inline fun <reified Source : Any, reified Target : Any> mapOrNull(source: Source): Target? =
        get(Source::class to Target::class)
            ?.let { mapper ->
                (mapper as Mapper<Source, Target>)
                    .map(source)
            }
}
