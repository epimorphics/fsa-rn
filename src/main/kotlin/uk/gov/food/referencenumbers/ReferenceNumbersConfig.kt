package uk.gov.food.referencenumbers

import com.mitchellbosecke.pebble.PebbleEngine
import com.mitchellbosecke.pebble.loader.ClasspathLoader
import com.mitchellbosecke.pebble.spring4.PebbleViewResolver
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@ConfigurationProperties(prefix = "fsa-rn")
@Configuration
class ReferenceNumbersConfig {
    var instance: Int = 1
}
