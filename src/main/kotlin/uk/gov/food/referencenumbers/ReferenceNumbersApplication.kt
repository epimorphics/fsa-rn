package uk.gov.food.referencenumbers


import uk.gov.food.rn.*
import java.time.ZonedDateTime
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ReferenceNumbersApplication

fun main(args: Array<String>) {
    runApplication<ReferenceNumbersApplication>(*args)
}
