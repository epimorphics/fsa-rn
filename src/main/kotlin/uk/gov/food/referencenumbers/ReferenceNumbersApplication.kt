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
/*
fun main(args: Array<String>) {
    var x = RN(Authority(1000), Instance(1), Type(99), ZonedDateTime.now())
    print(x.toString())
}*/
