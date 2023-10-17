package com.github.koswag.morph

import com.github.koswag.morph.builder.MapperBuilder

interface Mapper<Source : Any, Target : Any> : (Source) -> Target {
    fun map(source: Source): Target

    fun mapCatching(source: Source): Result<Target> =
        runCatching { map(source) }

    override fun invoke(source: Source): Target =
        map(source)

    companion object {
        inline operator fun <reified Source : Any, reified Target : Any> invoke(
            noinline mappingBlock: MapperBuilder<Source, Target>.() -> Unit,
        ): Mapper<Source, Target> =
            MapperBuilder(Source::class, Target::class, mappingBlock)
                .build()
    }
}
