package uk.gov.food.referencenumbers

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import uk.gov.food.rn.Instance
import uk.gov.food.rn.Type

class DecodedRNSerializer: StdSerializer<DecodedRN> {
    constructor() : this(null)
    constructor(a : Class<DecodedRN>?) : super(a)
    override fun serialize(rn: DecodedRN, jgen: JsonGenerator, provider: SerializerProvider) {
        jgen.writeStartObject()
        jgen.writeStringField("referenceNumber", rn.referenceNumber.encodedForm)
        jgen.writeStringField("timestamp", rn.timestamp.instant.toString())
        jgen.writeStringField("instance", String.format("%03d",rn.instance.id))
        jgen.writeObjectField("type", rn.type)
        jgen.writeObjectField("authority", rn.authority)
        jgen.writeNumberField("version", rn.version.id)
        jgen.writeEndObject()
    }
}