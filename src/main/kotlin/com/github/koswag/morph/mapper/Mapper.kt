package com.github.koswag.morph.mapper

import com.github.koswag.morph.mapper.builder.MapperBuilder

/**
 * Represents a mapping from type [Source] to type [Target].
 */
interface Mapper<Source : Any, Target : Any> : (Source) -> Target {

    /**
     * Maps a value of type [Source] to type [Target].
     * @param source object of type [Source]
     * @return Mapping result of type [Target]
     */
    fun map(source: Source): Target

    /**
     * Maps a value of type [Source] to type [Target] catching any exception.
     * @param source object of type [Source]
     * @return Mapped value of type [Target] wrapped in a [Result]
     */
    fun mapCatching(source: Source): Result<Target> =
        runCatching { map(source) }

    /**
     * Maps a value of type [Source] to type [Target].
     * @param source object of type [Source]
     * @return Mapping result of type [Target]
     */
    override fun invoke(source: Source): Target =
        map(source)

    companion object {
        /**
         * Builds a mapper from type [Source] to type [Target] via property mapping inference
         * catching any mapping validation error.
         * @return Mapper from type [Source] to type [Target] wrapped in a [Result]
         */
        inline fun <reified Source : Any, reified Target : Any> buildCatching(): Result<Mapper<Source, Target>> =
            buildCatching { }

        /**
         * Builds a mapper from type [Source] to type [Target] catching any mapping validation error.
         * @param mappingBlock mapper builder block
         * @return Mapper from type [Source] to type [Target] wrapped in a [Result]
         */
        inline fun <reified Source : Any, reified Target : Any> buildCatching(
            noinline mappingBlock: MapperBuilder<Source, Target>.() -> Unit,
        ): Result<Mapper<Source, Target>> =
            runCatching { Mapper(mappingBlock) }


        /**
         * Builds a mapper from type [Source] to type [Target] via property mapping inference.
         * @return Mapper from type [Source] to type [Target]
         */
        inline operator fun <reified Source : Any, reified Target : Any> invoke(): Mapper<Source, Target> =
            Mapper { }

        /**
         * Builds a mapper from type [Source] to type [Target].
         * @param mappingBlock mapper builder block
         * @return Mapper from type [Source] to type [Target]
         */
        inline operator fun <reified Source : Any, reified Target : Any> invoke(
            noinline mappingBlock: MapperBuilder<Source, Target>.() -> Unit,
        ): Mapper<Source, Target> =
            MapperBuilder(Source::class, Target::class, mappingBlock)
                .build()
    }
}
