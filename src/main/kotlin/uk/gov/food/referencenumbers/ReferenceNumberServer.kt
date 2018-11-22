package uk.gov.food.referencenumbers
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import io.micrometer.core.instrument.Metrics
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.view.json.MappingJackson2JsonView
import uk.gov.food.rn.*

@JsonInclude(JsonInclude.Include.NON_EMPTY)
class FSARN(@JsonProperty("fsa-rn") val rn: String)

class InvalidParameterException(message : String) : Exception(message)

@RestController
class GenerateController(val config: ReferenceNumbersConfig) {

    @Autowired
    lateinit var badWordConfig : BadWords

    val log : org.slf4j.Logger = LoggerFactory.getLogger(GenerateController::class.java)

    fun ContainsBadWord(word : String) : Boolean {
        var rnstring = word.replace("-", "")
        return badWordConfig.badwords.any { rnstring.contains(it, ignoreCase = true) }
    }

    fun GetReferenceNumber(authority: Authority, type: Type) : FSARN {
        Metrics.globalRegistry.counter("fsa-rn.authority", "authority", authority.id.toString()).increment()
        Metrics.globalRegistry.counter("fsa-rn.type", "type", type.id.toString()).increment()
        var x = RNFactory.getFactory(authority, Instance(config.instance), type)
        var generated : RN? = null
        var containsBadWord : Boolean = true
        var ttl : Int = 5
        while (containsBadWord) {
            if (ttl <= 0) {
                throw RNException("Could not generate reference number, all attempts at generation contained bad words")
            }
            ttl -= 1
            generated = x.generateReferenceNumber()
            containsBadWord = ContainsBadWord(generated.toString())
        }
        return FSARN(generated.toString())
    }

    @GetMapping("/generate/{authority}/{type}")
    fun get(@PathVariable authority: Int, @PathVariable type: String) : (ResponseEntity<Any?>) {
        if (type.length != 3) {
            throw InvalidParameterException("Type parameter invalid length, example: 105")
        }
        var rn = GetReferenceNumber(Authority(authority), Type(type))
        var responseHeaders = HttpHeaders()
        responseHeaders.set("Cache-Control", "no-cache,no-store,must-revalidate")
        responseHeaders.set("pragma", "no-cache")
        responseHeaders.set("Expires", "0")
        return ResponseEntity(rn, responseHeaders, HttpStatus.OK)
    }
}

fun RNModelMap(rn : String) : ModelMap {
    var jsonModel = ModelMap()
    var RNModel = RN(rn)
    var typeValid: String
    var authorityValid: String
    var types: List<Label>
    var authorities: List<Label>
    try {
        types = RegistryCache.getTypeLabels(RNModel.type.id)
        typeValid = "Valid"
    } catch (e: Exception) {
        typeValid = "Invalid Type"
        types = emptyList()
    }
    try {
        authorities = RegistryCache.getAuthorityLabels(RNModel.authority.id)
        authorityValid = "Valid"
    } catch (e: Exception) {
        authorityValid = "Invalid Authority"
        authorities = emptyList()
    }

    jsonModel.addAttribute("referenceNumber", RNModel.toString())
    jsonModel.addAttribute("timeStamp", RNModel.instant.instant.toOffsetDateTime().toString())
    jsonModel.addAttribute("instance", String.format("%03d", RNModel.instance.id))
    var jsonTypeModel = ModelMap()
    jsonTypeModel.addAttribute("id", String.format("%03d", RNModel.type.id))
    jsonTypeModel.addAttribute("status", typeValid)
    jsonTypeModel.addAttribute("labels", types)
    jsonModel.addAttribute("type", jsonTypeModel)
    var jsonAuthorityModel = ModelMap()
    jsonAuthorityModel.addAttribute("id", RNModel.authority.id.toString())
    jsonAuthorityModel.addAttribute("status", authorityValid)
    jsonAuthorityModel.addAttribute("labels", authorities)
    jsonModel.addAttribute("authority", jsonAuthorityModel)
    jsonModel.addAttribute("version", RNModel.version.id)
    return jsonModel
}

fun JSONLDModelMap(rn : String) : ModelMap {
    var jsonModel = RNModelMap(rn)
    var jsonldModel = ModelMap()
    jsonldModel.addAttribute("@type", "rn:RN")
    jsonldModel.addAttribute("@id", "https://data.food.gov.uk/reference-number/decode/${jsonModel.get("referenceNumber")}")
    jsonldModel.addAttribute("rn:timestamp", jsonModel.get("timeStamp"))
    jsonldModel.addAttribute("rn:instance", jsonModel.get("instance"))
    var jsonldTypeModel = ModelMap()
    var jsonTypeModel = jsonModel.get("type") as ModelMap
    jsonldTypeModel.addAttribute("@id", "https://data.food.gov.uk/codes/reference-number/type/${jsonTypeModel.get("id")}")
    jsonldTypeModel.addAttribute("skos:notation", jsonTypeModel.get("id"))
    jsonldTypeModel.addAttribute("rdfs:label", "food-rrn")
    jsonldTypeModel.addAttribute("rn:status", jsonTypeModel.get("status"))
    val ldlabeltransform = fun (label: Label): ModelMap {
        var m = ModelMap()
        m.addAttribute("@language", label.lang)
        m.addAttribute("@value", label.name)
        return m
    }
    val types = jsonTypeModel.get("labels") as List<Label>
    jsonldTypeModel.addAttribute("skos:prefLabel", types.map(ldlabeltransform))
    var jsonldAuthorityModel = ModelMap()
    var jsonAuthorityModel = jsonModel.get("authority") as ModelMap
    jsonldAuthorityModel.addAttribute("@id", "https://data.food.gov.uk/codes/reference-number/authority/${jsonAuthorityModel.get("id")}")
    jsonldAuthorityModel.addAttribute("@type", "skos:Concept")
    jsonldAuthorityModel.addAttribute("skos:notation", jsonAuthorityModel.get("id"))
    jsonldAuthorityModel.addAttribute("rn:status", jsonAuthorityModel.get("status"))
    val authorities = jsonAuthorityModel.get("labels") as List<Label>
    jsonldAuthorityModel.addAttribute("skos:prefLabel", authorities.map(ldlabeltransform))
    jsonldModel.addAttribute("rn:type", jsonldTypeModel)
    jsonldModel.addAttribute("rn:authority", jsonldAuthorityModel)
    jsonldModel.addAttribute("rn:version", jsonModel.get("version"))
    return jsonldModel
}

@RestController
class DecodeController(val config: ReferenceNumbersConfig) {
    @GetMapping(value = arrayOf("/decode/{rn}", "/decode/{rn}.html"), produces=arrayOf("text/html"))
    fun getHTML(@PathVariable rn: String) : ModelAndView {
        var mav = ModelAndView()
        var mm = RNModelMap(rn)
        mav.viewName= "rn"
        mav.addAllObjects(mm)
        return mav
    }

    @GetMapping(value = arrayOf("/decode/{rn}", "/decode/{rn}.json"), produces=arrayOf("application/json"))
    fun getJSON(@PathVariable rn: String) : ModelAndView {
        var mav = ModelAndView()
        var mm = RNModelMap(rn)
        var view = MappingJackson2JsonView()
        mav.setView(view)
        mav.addAllObjects(mm)
        return mav
    }

    @GetMapping(value = arrayOf("/decode/{rn}", "/decode/{rn}.jsonld"), produces=arrayOf("application/ld+json"))
    fun getJSONLD(@PathVariable rn: String) : ModelAndView {
        var mav = ModelAndView()
        var mm = JSONLDModelMap(rn)
        var view = MappingJackson2JsonView()
        mav.setView(view)
        mav.addAllObjects(mm)
        return mav
    }
}
