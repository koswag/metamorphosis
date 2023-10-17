package com.github.koswag.morph

interface Mapper<Source : Any, Target : Any> : (Source) -> Target {
    fun map(source: Source): Target

    fun mapCatching(source: Source): Result<Target> =
        runCatching { map(source) }

    override fun invoke(source: Source): Target =
        map(source)
}
