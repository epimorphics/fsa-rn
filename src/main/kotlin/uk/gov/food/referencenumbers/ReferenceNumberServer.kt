package uk.gov.food.referencenumbers
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import io.micrometer.core.instrument.Metrics
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
class GreetingController(val config: ReferenceNumbersConfig) {
    @GetMapping("/generate/{authority}/{type}")
    fun get(@PathVariable authority: Int, @PathVariable type: String) : (ResponseEntity<Any?>) {
        if (type.length != 3) {
            throw InvalidParameterException("Type parameter invalid length, example: 105")
        }
        Metrics.globalRegistry.counter("fsa-rn.authority", "authority", authority.toString()).increment()
        Metrics.globalRegistry.counter("fsa-rn.type", "type", type).increment()
        var x = RNFactory.getFactory(Authority(authority), Instance(config.instance), Type(type))
        var rn = FSARN(x.generateReferenceNumber().toString())
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
    fun getJSONLD(@PathVariable rn: String) : (ResponseEntity<LDRN>) {
        var x = RN(rn)
        var drn = LDRN(x, x.getInstance(), x.getInstant(), TypeDisplay(x.getType()), AuthorityDisplay(x.getAuthority()), x.getVersion())
        return ResponseEntity.ok(drn)
    }
}
