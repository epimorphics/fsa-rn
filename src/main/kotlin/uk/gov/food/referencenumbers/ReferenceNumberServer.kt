package uk.gov.food.referencenumbers
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonView
import uk.gov.food.rn.RN

@JsonInclude(JsonInclude.Include.NON_EMPTY)
class FSARN(@JsonProperty("fsa-rn") val rn: String)

class InvalidParameterException(message : String) : Exception(message)

class JsonRN {}

class JsonLDRN {}

class RNModel(rnval: String) {
    inner class Authority(id : Int) {
        @JsonView(JsonRN::class)
        @JsonProperty("id")
        private val id: String = id.toString()

        @JsonView(JsonRN::class)
        @JsonProperty("status")
        private var status: String = "Value"

        @JsonView(JsonRN::class)
        @JsonProperty("labels")
        private var labels: List<Label> = ArrayList<Label>()

        init {
            try {
                this.labels = RegistryCache.getAuthorityLabels(id)
                this.status = "Valid"
            } catch (e: Exception) {
                this.status = "Invalid Authority"
                this.labels = emptyList()
            }
        }

        @JsonView(JsonLDRN::class)
        @JsonProperty("@id")
        fun getID() = "https://data.food.gov.uk/codes/reference-number/authority/${id}"

        @JsonView(JsonLDRN::class)
        @JsonProperty("skos:notation")
        fun getNotation() = id

        @JsonView(JsonLDRN::class)
        @JsonProperty("@type")
        fun getType() = "skos:Concept"

        @JsonView(JsonLDRN::class)
        @JsonProperty("rn:status")
        fun getStatus() = status

        @JsonView(JsonLDRN::class)
        @JsonProperty("skos:prefLabel")
        fun getLabels() = labels
    }

    inner class Type(id: Int) {
        @JsonView(JsonRN::class)
        @JsonProperty("id")
        private val id : String = String.format("%03d", id)

        @JsonView(JsonRN::class)
        @JsonProperty("status")
        private var status: String

        @JsonView(JsonRN::class)
        @JsonProperty("labels")
        private var labels: List<Label>

        init {
            try {
                this.labels = RegistryCache.getTypeLabels(id)
                this.status = "Valid"
            } catch (e: Exception) {
                this.status = "Invalid Type"
                this.labels = emptyList()
            }
        }

        @JsonView(JsonLDRN::class)
        @JsonProperty("@id")
        fun getID() = "https://data.food.gov.uk/codes/reference-number/type/${id}"

        @JsonView(JsonLDRN::class)
        @JsonProperty("skos:notation")
        fun getNotation() = id

        @JsonView(JsonLDRN::class)
        @JsonProperty("rdfs:label")
        fun getLabel() = "food-rrn"

        @JsonView(JsonLDRN::class)
        @JsonProperty("rn:status")
        fun getStatus() = status

        @JsonView(JsonLDRN::class)
        @JsonProperty("skos:prefLabel")
        fun getLabels() = labels
    }

    var rn = RN(rnval)

    @JsonProperty("@type")
    @JsonView(JsonLDRN::class)
    val rntype: String = "rn:RN"

    @JsonView(JsonRN::class)
    @JsonProperty("authority")
    private val authority = this.Authority(rn.authority.id)

    @JsonView(JsonRN::class)
    @JsonProperty("type")
    private val type = this.Type(rn.type.id)

    @JsonView(JsonRN::class)
    @JsonProperty("referenceNumber")
    val rnString = rn.encodedForm

    @JsonView(JsonRN::class)
    @JsonProperty("timeStamp")
    private val timestamp= rn.instant.instant.toOffsetDateTime().toString()

    @JsonView(JsonRN::class)
    @JsonProperty("instance")
    private val instance =  String.format("%03d", rn.instance.id)

    @JsonProperty("version")
    @JsonView(JsonRN::class)
    private val version = rn.version.id

    @JsonView(JsonLDRN::class)
    @JsonProperty("rn:timestamp")
    fun getTimeStamp() = timestamp

    @JsonView(JsonLDRN::class)
    @JsonProperty("@id")
    fun getID() = "https://data.food.gov.uk/reference-number/decode/${rnString}"

    @JsonView(JsonLDRN::class)
    @JsonProperty("rn:instance")
    fun getInstance() = instance

    @JsonProperty("rn:version")
    @JsonView(JsonLDRN::class)
    fun getVersion() = version

    @JsonProperty("rn:type")
    @JsonView(JsonLDRN::class)
    fun getType() = type

    @JsonProperty("rn:authority")
    @JsonView(JsonLDRN::class)
    fun getAuthority() = authority
}
