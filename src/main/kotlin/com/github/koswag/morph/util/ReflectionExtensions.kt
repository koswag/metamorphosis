package com.github.koswag.morph.util

import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

internal fun <T : Any> KClass<T>.getFields(): Collection<KProperty1<T, *>> =
    members.filterIsInstance<KProperty1<T, *>>()
