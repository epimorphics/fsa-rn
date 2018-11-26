package uk.gov.food.referencenumbers

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonView

class Label(name: String, lang: String) {
    @JsonProperty("name")
    @JsonView(JsonRN::class)
    val name: String = name

    @JsonProperty("lang")
    @JsonView(JsonRN::class)
    val lang: String = lang

    @JsonProperty("@value")
    @JsonView(JsonLDRN::class)
    private fun getNameLD() : String {
        return name
    }

    @JsonProperty("@language")
    @JsonView(JsonLDRN::class)
    private fun getLangLD() : String {
        return lang
    }
}
