package uk.gov.food.referencenumbers

import com.mitchellbosecke.pebble.PebbleEngine
import com.mitchellbosecke.pebble.loader.ClasspathLoader
import com.mitchellbosecke.pebble.template.PebbleTemplate
import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import java.io.StringWriter
import javax.servlet.RequestDispatcher
import javax.servlet.http.HttpServletRequest

@Controller
class RNErrorController : ErrorController {

    @RequestMapping("/error")
    @ResponseBody
    fun error(request : HttpServletRequest) : String {
        var loader = ClasspathLoader()
        loader.prefix = "templates"
        loader.suffix = ".peb"
        var engine : PebbleEngine = PebbleEngine.Builder()
                .loader(loader)
                .build()
        var compiledTemplate : PebbleTemplate = engine.getTemplate("err")
        var statusCode = request.getAttribute("javax.servlet.error.status_code") as Integer
        var exception  = request.getAttribute("javax.servlet.error.exception") as? Exception
        var context = HashMap<String, Any>()
        var exceptionMessage : String = if (exception === null) "N/A" else exception.localizedMessage
        context.put("status", statusCode)
        context.put("exception", exceptionMessage)
        var writer = StringWriter()
        compiledTemplate.evaluate(writer, context)
        return writer.toString()
    }

    override fun getErrorPath(): String {
        return "/error"
    }
}