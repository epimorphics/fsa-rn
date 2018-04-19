package uk.gov.food.referencenumbers

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "fsa-rn")
data class ReferenceNumbersConfig(var instance: Int)
