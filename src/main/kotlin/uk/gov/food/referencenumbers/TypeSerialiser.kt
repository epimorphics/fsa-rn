package uk.gov.food.referencenumbers

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import uk.gov.food.rn.RNException
import uk.gov.food.rn.Type
import khttp.get
import org.json.JSONException


@JsonSerialize(using = TypeDisplaySerializer::class)
class TypeDisplay(t : Type) : Type(t.id) {

    lateinit var labels: List<Label>

    init {
        try {
            this.labels = RegistryCache.getTypeLabels(id)
        } catch (e : JSONException) {
            throw RNException("Type in reference number is not valid")
        }
    }

    override fun toString() : String {
        return String.format("%03d", id)
    }
}

class TypeDisplaySerializer: StdSerializer<TypeDisplay> {
    constructor() : this(null)
    constructor(a : Class<TypeDisplay>?) : super(a)
    override fun serialize(type: TypeDisplay, jgen: JsonGenerator, provider: SerializerProvider) {
        jgen.writeStartObject();

        jgen.writeStringField( "id", String.format("%03d",type.id))
        jgen.writeArrayFieldStart("labels")
        for (i in 0..type.labels.size-1) {
            jgen.writeStartObject()
            jgen.writeStringField("lang", type.labels.get(i).lang)
            jgen.writeStringField("name", type.labels.get(i).name)
            jgen.writeEndObject()
        }
        jgen.writeEndArray()
        jgen.writeEndObject()
    }
}