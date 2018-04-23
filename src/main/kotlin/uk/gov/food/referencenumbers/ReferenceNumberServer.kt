package uk.gov.food.referencenumbers
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import uk.gov.food.rn.*
import java.time.ZonedDateTime

@RestController
class GreetingController(val config: ReferenceNumbersConfig) {
    @GetMapping("/fsa-rn/{authority}/{type}")
    fun get(@PathVariable authority: Int, @PathVariable type: Int) : (ResponseEntity<String>) {
        var x = RNFactory.getFactory(Authority(authority), Instance(config.instance), Type(type))
        return ResponseEntity.ok(x.generateReferenceNumber().toString())
    }
}
