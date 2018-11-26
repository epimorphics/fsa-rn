package uk.gov.food.referencenumbers

import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.ModelAndView

@RestController
class DecodeController(val config: ReferenceNumbersConfig) {

    val log : org.slf4j.Logger = LoggerFactory.getLogger(DecodeController::class.java)

    @GetMapping(value = arrayOf("/decode/{rn}", "/decode/{rn}.html"), produces=arrayOf("text/html"))
    fun getHTML(@PathVariable rn: String) : ModelAndView {
        var mav = ModelAndView()
        var rnModel : RNModel = RNModel(rn)
        mav.viewName= "rn"
        mav.addObject("rn", rnModel)
        return mav
    }

    @GetMapping(value = arrayOf("/decode/{rn}", "/decode/{rn}.json"), produces=arrayOf("application/json"))
    fun getJSON(@PathVariable rn: String) : String {
        var rnModel : RNModel = RNModel(rn)
        var mapper = ObjectMapper()
        mapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false)
        return mapper.writerWithView(JsonRN::class.java).writeValueAsString(rnModel)
    }

    @GetMapping(value = arrayOf("/decode/{rn}", "/decode/{rn}.jsonld"), produces=arrayOf("application/ld+json"))
    fun getJSONLD(@PathVariable rn: String) : String {
        var rn = RNModel(rn)
        var mapper = ObjectMapper()
        mapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false)
        return mapper.writerWithView(JsonLDRN::class.java).writeValueAsString(rn)
    }
}
