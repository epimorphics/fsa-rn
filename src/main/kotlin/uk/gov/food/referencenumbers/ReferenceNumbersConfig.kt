package uk.gov.food.referencenumbers

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@ConfigurationProperties(prefix = "fsa-rn")
@Configuration
class ReferenceNumbersConfig {
    var instance: Int = 1
}
