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
    @GetMapping("/fsa-rn/{authority}/{type}")
    fun get(@PathVariable authority: Int, @PathVariable type: Int) : (ResponseEntity<FSARN>) {
        var x = RNFactory.getFactory(Authority(authority), Instance(config.instance), Type(type))
        var rn = FSARN(x.generateReferenceNumber().toString())
        var responseHeaders = HttpHeaders()
        responseHeaders.set("Cache-Control", "no-cache,no-store,must-revalidate")
        responseHeaders.set("pragma", "no-cache")
        responseHeaders.set("Expires", "0")
        return ResponseEntity(rn, responseHeaders, HttpStatus.OK)
    }
}

class DecodedRN(val instance: Instance, val timestamp: TimeStamp, val type: Type, val authority: Authority)

@RestController
class DecodeController(val config: ReferenceNumbersConfig) {
    @GetMapping("/fsa-rn-decode/{rn}")
    fun get(@PathVariable rn: String) : (ResponseEntity<DecodedRN>) {
        var x = RN(rn)
        var drn = DecodedRN(x.getInstance(), x.getInstant(), x.getType(), x.getAuthority())
        return ResponseEntity.ok(drn)
    }
}
