package com.github.koswag.morph

import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.result.shouldBeSuccess
import io.kotest.matchers.shouldBe

internal data class DataMorphSource(
    val int: Int,
    val string: String,
    val boolean: Boolean,
)

internal data class DataMorphTarget(
    val int: Int,
)

internal data class DataMorphRichTarget(
    val int: Int,
    val string: String,
)

internal class DataMorphSpec : FeatureSpec({

    feature("Mapper registration") {
        scenario("No mapper registration") {
            val morph = DataMorph()

            morph.canMap(DataMorphSource::class to DataMorphTarget::class) shouldBe false
            morph.canMap(DataMorphSource::class to DataMorphRichTarget::class) shouldBe false
        }

        scenario("Single mapper registration") {
            val morph = DataMorph()
                .withMapping<DataMorphSource, DataMorphTarget>()

            morph.canMap(DataMorphSource::class to DataMorphTarget::class) shouldBe true
            morph.canMap(DataMorphSource::class to DataMorphRichTarget::class) shouldBe false
        }

        scenario("Multiple mapper registration") {
            val morph = DataMorph()
                .withMapping<DataMorphSource, DataMorphTarget>()
                .withMapping<DataMorphSource, DataMorphRichTarget>()

            morph.canMap(DataMorphSource::class to DataMorphTarget::class) shouldBe true
            morph.canMap(DataMorphSource::class to DataMorphRichTarget::class) shouldBe true
        }
    }

    feature("Arbitrary type mapping") {
        scenario("Type is not registered") {
            val source = DataMorphSource(int = 1, string = "abc", boolean = false)
            val morph = DataMorph()

            val result = morph.mapOrNullCatching<DataMorphSource, DataMorphTarget>(source)

            result.shouldBeSuccess {
                it.shouldBeNull()
            }
        }

        scenario("Type is registered") {
            val source = DataMorphSource(int = 1, string = "abc", boolean = false)
            val morph = DataMorph()
                .withMapping<DataMorphSource, DataMorphTarget>()

            val result = morph.mapOrNullCatching<DataMorphSource, DataMorphTarget>(source)

            result.shouldBeSuccess {
                it.shouldNotBeNull()
                it.int shouldBe 1
            }
        }

        scenario("Both types are registered") {
            val source = DataMorphSource(int = 1, string = "abc", boolean = false)
            val morph = DataMorph()
                .withMapping<DataMorphSource, DataMorphTarget>()
                .withMapping<DataMorphSource, DataMorphRichTarget>()

            val targetMappingResult = morph.mapOrNullCatching<DataMorphSource, DataMorphTarget>(source)
            val richTargetMappingResult = morph.mapOrNullCatching<DataMorphSource, DataMorphRichTarget>(source)

            targetMappingResult.shouldBeSuccess {
                it.shouldNotBeNull()
                it.int shouldBe 1
            }

            richTargetMappingResult.shouldBeSuccess {
                it.shouldNotBeNull()
                it.int shouldBe 1
                it.string shouldBe "abc"
            }
        }
    }

})
