package com.github.koswag.morph.mapper

import com.github.koswag.morph.mapper.builder.MapperBuilder

interface Mapper<Source : Any, Target : Any> : (Source) -> Target {
    fun map(source: Source): Target

    fun mapCatching(source: Source): Result<Target> =
        runCatching { map(source) }

    override fun invoke(source: Source): Target =
        map(source)

    companion object {
        inline fun <reified Source : Any, reified Target : Any> buildCatching(
            noinline mappingBlock: MapperBuilder<Source, Target>.() -> Unit,
        ): Result<Mapper<Source, Target>> =
            runCatching { Mapper(mappingBlock) }

        inline operator fun <reified Source : Any, reified Target : Any> invoke(
            noinline mappingBlock: MapperBuilder<Source, Target>.() -> Unit,
        ): Mapper<Source, Target> =
            MapperBuilder(Source::class, Target::class, mappingBlock)
                .build()
    }
}
