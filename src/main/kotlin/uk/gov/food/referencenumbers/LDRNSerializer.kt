package uk.gov.food.referencenumbers

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import uk.gov.food.rn.Instance
import uk.gov.food.rn.Type

class LDRNSerializer: StdSerializer<LDRN> {
    constructor() : this(null)
    constructor(a : Class<LDRN>?) : super(a)
    override fun serialize(rn: LDRN, jgen: JsonGenerator, provider: SerializerProvider) {
        jgen.writeStartObject()
        jgen.writeStringField("rdf:type", "rn:RN")
        jgen.writeStringField("@id", "https://data.food.gov.uk/reference-number/decode/${rn.referenceNumber.encodedForm}")
        jgen.writeStringField("rn:timestamp", rn.timestamp.instant.toString())
        jgen.writeStringField("rn:instance", String.format("%03d", rn.instance.id))
        jgen.writeNumberField("rn:version", rn.version.id)
        jgen.writeObjectFieldStart("rn:type")
            jgen.writeStringField("@id", "http://data.food.gov.uk/codes/reference-number/type/${rn.type}")
            jgen.writeStringField("rdfs:label", "food-rrn")
            jgen.writeStringField("skos:notation", rn.type.toString())
            jgen.writeArrayFieldStart("skos:prefLabel")
                for (i in 0..rn.type.labels.size-1) {
                    jgen.writeStartObject()
                    jgen.writeStringField("@language", rn.type.labels.get(i).lang)
                    jgen.writeStringField("@value", rn.type.labels.get(i).name)
                    jgen.writeEndObject()
                }
            jgen.writeEndArray()
        jgen.writeEndObject()
        jgen.writeObjectFieldStart("rn:authority")
            jgen.writeStringField("@id", "http://data.food.gov.uk/codes/reference-number/authority/${rn.authority}")
            jgen.writeStringField("@type", "skos:Concept")
            jgen.writeArrayFieldStart("skos:prefLabel")
                for (i in 0..rn.authority.labels.size-1) {
                    jgen.writeStartObject()
                    jgen.writeStringField("@language", rn.authority.labels.get(i).lang)
                    jgen.writeStringField("@value", rn.authority.labels.get(i).name)
                    jgen.writeEndObject()
                }
            jgen.writeEndArray()
            jgen.writeStringField("skos:notation", rn.authority.toString())
        jgen.writeEndObject()
        jgen.writeObjectFieldStart("@context")
            jgen.writeStringField("xsd",  "http://www.w3.org/2001/XMLSchema#")
            jgen.writeStringField("skos",  "http://www.w3.org/2004/02/skos/core#")
            jgen.writeStringField("rdfs",  "http://www.w3.org/2000/01/rdf-schema#")
            jgen.writeStringField("dct",  "http://purl.org/dc/terms/")
            jgen.writeStringField("rdf",  "http://www.w3.org/1999/02/22-rdf-syntax-ns#")
            jgen.writeStringField("rn",  "https://data.food.gov.uk/codes/reference-number/def/rn/")
        jgen.writeEndObject()
        jgen.writeEndObject()

    }
}