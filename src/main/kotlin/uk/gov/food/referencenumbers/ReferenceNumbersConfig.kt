package uk.gov.food.referencenumbers

import io.pebbletemplates.pebble.PebbleEngine
import io.pebbletemplates.pebble.loader.ClasspathLoader
import io.pebbletemplates.spring.servlet.PebbleViewResolver
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@ConfigurationProperties(prefix = "fsa-rn")
@Configuration
class ReferenceNumbersConfig {
    var instance: Int = 1

    @Bean
    fun pebbleEngine(): PebbleEngine {
        var loader = ClasspathLoader()
        loader.prefix = "templates"
        loader.suffix = ".peb"
        return PebbleEngine.Builder()
            .loader(loader).build()
    }

    @Bean
    fun viewResolver(): PebbleViewResolver {
        val viewResolver = PebbleViewResolver(pebbleEngine())
        viewResolver.setPrefix("templates/")
        viewResolver.setSuffix(".peb")
        return viewResolver
    }
}
