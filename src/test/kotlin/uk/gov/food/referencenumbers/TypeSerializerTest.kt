package uk.gov.food.referencenumbers

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.databind.ObjectMapper
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Test
import sun.security.x509.AuthorityInfoAccessExtension
import uk.gov.food.rn.Authority
import uk.gov.food.rn.Type
import java.io.StringWriter

class TypeSerializerTest {
    @Test
    fun TestTypeSerializerGivesName() {
        var testDisplay = TypeDisplay(Type(0))
        var jsonWriter = StringWriter()
        var jsonGenerator = JsonFactory().createGenerator(jsonWriter)
        var serializerProvider = ObjectMapper().serializerProvider
        TypeDisplaySerializer().serialize(testDisplay, jsonGenerator, serializerProvider)
        jsonGenerator.flush()
        assertThat(jsonWriter.toString(), equalTo("{\"id\":\"000\",\"labels\":[{\"lang\":\"en\",\"name\":\"Test Codes (test-codes)\"}]}"))
    }

}