package com.github.koswag.morph

import com.github.koswag.morph.exception.IncompleteMappingException
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.collections.shouldContainOnly
import io.kotest.matchers.result.shouldBeFailure
import io.kotest.matchers.result.shouldBeSuccess
import io.kotest.matchers.types.shouldBeInstanceOf
import java.time.LocalDate

internal data class Source(
    val string: String,
    val optString: String?,
    val date: LocalDate,
)

internal data class Target(
    val stringTarget: String,
    val optString: String?,
    val int: Int,
)

internal class MapperSpec : FeatureSpec({

    feature("Complete mapping validation") {
        scenario("Incomplete mapping without mapping inference") {
            val result = Mapper.buildCatching {
                inferMissingMappings = false
                Source::string mappedTo Target::stringTarget
            }

            result.shouldBeFailure {
                it.shouldBeInstanceOf<IncompleteMappingException>()

                it.missingMappings.shouldContainOnly(
                    Target::optString,
                    Target::int,
                )
            }
        }

        scenario("Complete mapping without mapping inference") {
            shouldNotThrow<IncompleteMappingException> {
                // given:
                val mapper = Mapper {
                    inferMissingMappings = false
                    Source::string mappedTo Target::stringTarget
                    Source::optString mappedTo Target::optString
                    Source::date transformedTo Target::int using { it.year }
                }

                val source = Source(
                    string = "string",
                    optString = null,
                    date = LocalDate.of(2000, 12, 10)
                )

                // when:
                val result = mapper.mapCatching(source)

                // then:
                result.shouldBeSuccess {
                    Target(
                        stringTarget = "string",
                        optString = null,
                        int = 2000,
                    )
                }
            }
        }
    }

    feature("Missing mapping inference") {
        scenario("Incomplete mapping with mapping inference") {
            val result = Mapper.buildCatching {
                Source::string mappedTo Target::stringTarget
            }

            result.shouldBeFailure {
                it.shouldBeInstanceOf<IncompleteMappingException>()

                it.missingMappings.shouldContainOnly(
                    Target::int,
                )
            }
        }

        scenario("Complete mapping with mapping inference") {
            shouldNotThrow<IllegalStateException> {
                // given:
                val mapper = Mapper {
                    Source::string mappedTo Target::stringTarget
                    Source::date transformedTo Target::int using { it.year }
                }

                val source = Source(
                    string = "string",
                    optString = null,
                    date = LocalDate.of(2000, 12, 10)
                )

                // when:
                val result = mapper.mapCatching(source)

                // then:
                result.shouldBeSuccess {
                    Target(
                        stringTarget = "string",
                        optString = null,
                        int = 2000,
                    )
                }
            }
        }
    }

})
