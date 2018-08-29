package uk.gov.food.referencenumbers

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import uk.gov.food.rn.RNException
import khttp.get
import org.json.JSONException
import uk.gov.food.rn.Authority
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAmount
import java.time.temporal.TemporalUnit
import java.util.concurrent.TimeUnit

@JsonSerialize(using = AuthorityDisplaySerializer::class)
class AuthorityDisplay(t : Authority) : Authority(t.id) {

    var labels: List<Label>
    lateinit var status: String
    init {
        try {
            this.labels = RegistryCache.getAuthorityLabels(id)
            this.status = "Valid"
        }  catch (e : Exception) {
            this.labels = ArrayList<Label>()
            this.status = "Invalid Authority"
        }
    }

    override fun toString() : String {
        return String.format("%04d", id)
    }
}

class AuthorityDisplaySerializer: StdSerializer<AuthorityDisplay> {
    constructor() : this(null)
    constructor(a : Class<AuthorityDisplay>?) : super(a)
    override fun serialize(authority: AuthorityDisplay, jgen: JsonGenerator, provider: SerializerProvider) {
        jgen.writeStartObject();

        jgen.writeStringField( "id", String.format("%04d",authority.id))
        jgen.writeStringField( "status", authority.status)
        jgen.writeArrayFieldStart("labels")
        for (i in 0..authority.labels.size-1) {
           jgen.writeStartObject()
           jgen.writeStringField("lang", authority.labels.get(i).lang)
           jgen.writeStringField("name", authority.labels.get(i).name)
           jgen.writeEndObject()
        }
        jgen.writeEndArray()
        jgen.writeEndObject()
    }
}