package uk.gov.food.referencenumbers

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.databind.ObjectMapper
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Test
import sun.security.x509.AuthorityInfoAccessExtension
import uk.gov.food.rn.Authority
import java.io.StringWriter

class AuthoritySerializerTest {
    @Test
    fun TestAuthoritySerializerGivesName() {
        var testDisplay = AuthorityDisplay(Authority(1000))
        var jsonWriter = StringWriter()
        var jsonGenerator = JsonFactory().createGenerator(jsonWriter)
        var serializerProvider = ObjectMapper().serializerProvider
        AuthorityDisplaySerializer().serialize(testDisplay, jsonGenerator, serializerProvider)
        jsonGenerator.flush()
        assertThat(jsonWriter.toString(), equalTo("{\"id\":\"1000\",\"labels\":[{\"lang\":\"en\",\"name\":\"Food Standards Agency\"},{\"lang\":\"cy\",\"name\":\"Asiantaeth Safonau Bwyd\"}]}"))
    }

}