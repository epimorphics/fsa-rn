package uk.gov.food.referencenumbers

import io.micrometer.core.instrument.Metrics
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import uk.gov.food.rn.*

@RestController
class GenerateController(val config: ReferenceNumbersConfig) {

    val log : org.slf4j.Logger = LoggerFactory.getLogger(GenerateController::class.java)

    @Autowired
    lateinit var badWordConfig : BadWords

    fun ContainsBadWord(word : String) : Boolean {
        var rnstring = word.replace("-", "")
        return badWordConfig.badwords.any { rnstring.contains(it, ignoreCase = true) }
    }

    fun GetReferenceNumber(authority: Authority, typeString: String) : FSARN {
        if (typeString.length != 3) {
            throw InvalidParameterException("Type parameter invalid length, example: 105")
        }
        var type: Type = Type(typeString)
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
        var rn = GetReferenceNumber(Authority(authority), type)
        var responseHeaders = HttpHeaders()
        responseHeaders.set("Cache-Control", "no-cache,no-store,must-revalidate")
        responseHeaders.set("pragma", "no-cache")
        responseHeaders.set("Expires", "0")
        return ResponseEntity(rn, responseHeaders, HttpStatus.OK)
    }
}
