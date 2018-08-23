package uk.gov.food.referencenumbers
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import uk.gov.food.rn.*
import com.fasterxml.jackson.annotation.*
import java.time.ZonedDateTime

@JsonInclude(JsonInclude.Include.NON_EMPTY)
class FSARN(@JsonProperty("fsa-rn") val rn: String)

@RestController
class GreetingController(val config: ReferenceNumbersConfig) {
    @GetMapping("/generate/{authority}/{type}")
    fun get(@PathVariable authority: Int, @PathVariable type: String) : (ResponseEntity<FSARN>) {
        if (type.length != 3) {
            throw RNException("Type parameter invalid length, example: 005")
        }
        var x = RNFactory.getFactory(Authority(authority), Instance(config.instance), Type(type))
        var rn = FSARN(x.generateReferenceNumber().toString())
        var responseHeaders = HttpHeaders()
        responseHeaders.set("Cache-Control", "no-cache,no-store,must-revalidate")
        responseHeaders.set("pragma", "no-cache")
        responseHeaders.set("Expires", "0")
        return ResponseEntity(rn, responseHeaders, HttpStatus.OK)
    }
}

@JsonSerialize(using = DecodedRNSerializer::class)
data class DecodedRN(val referenceNumber: RN, val instance: Instance, val timestamp: TimeStamp, val type: TypeDisplay, val authority: Authority)

@RestController
class DecodeController(val config: ReferenceNumbersConfig) {
    @GetMapping(value = arrayOf("/decode/{rn}", "/decode/{rn}.html"), produces=arrayOf("text/html"))
    fun get(@PathVariable rn: String) : (ResponseEntity<DecodedRN>) {
        var x = RN(rn)
        var drn = DecodedRN(x.getInstance(), x.getInstant(), TypeDisplay(x.getType()), x.getAuthority())
        return ResponseEntity.ok(drn)
    }
}
