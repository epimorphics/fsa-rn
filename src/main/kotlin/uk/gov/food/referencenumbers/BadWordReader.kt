package uk.gov.food.referencenumbers

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.core.env.MapPropertySource
import org.springframework.core.io.support.EncodedResource
import org.springframework.core.io.support.PropertySourceFactory
import java.io.IOException



@Configuration
@PropertySource("classpath:badwords.json", factory = JsonPropertySourceFactory::class)
@ConfigurationProperties
class BadWords {
    @Value("\${badwords}")
    lateinit var badwords: List<String>
}

class JsonPropertySourceFactory : PropertySourceFactory {
    @Throws(IOException::class)
    override fun createPropertySource(name: String?, resource: EncodedResource): org.springframework.core.env.PropertySource<*> {
        val readValue = ObjectMapper().readValue(resource.inputStream, Map::class.java)
        return MapPropertySource("json-property", readValue as MutableMap<String, Any>)
    }
}
