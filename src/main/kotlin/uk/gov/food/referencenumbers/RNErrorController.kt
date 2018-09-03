package uk.gov.food.referencenumbers

import com.mitchellbosecke.pebble.PebbleEngine
import com.mitchellbosecke.pebble.extension.Test
import com.mitchellbosecke.pebble.loader.ClasspathLoader
import com.mitchellbosecke.pebble.template.PebbleTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import uk.gov.food.rn.RNException
import uk.gov.food.rn.Type
import java.io.StringWriter
import javax.servlet.RequestDispatcher
import javax.servlet.http.HttpServletRequest

@Controller
open class RNErrorController : ResponseEntityExceptionHandler() {
    fun genericResponse(exception: Exception, statusCode: HttpStatus) : ResponseEntity<Any> {
        var loader = ClasspathLoader()
        loader.prefix = "templates"
        loader.suffix = ".peb"
        var engine : PebbleEngine = PebbleEngine.Builder()
                .loader(loader)
                .build()
        var compiledTemplate : PebbleTemplate = engine.getTemplate("err")
        var context = HashMap<String, Any>()
        context.put("status", statusCode.value())
        context.put("exception", "${exception.message}")
        var writer = StringWriter()
        compiledTemplate.evaluate(writer, context)
        return ResponseEntity.status(statusCode).header("Content-Type", "text/html").body(writer.toString())
    }
}

@ControllerAdvice
class ExceptionHandler: RNErrorController() {
    @ExceptionHandler(Exception::class)
    fun handleException(exception: Exception): ResponseEntity<Any> = genericResponse(exception, HttpStatus.INTERNAL_SERVER_ERROR)
}

@ControllerAdvice
class RNExceptionHandler: RNErrorController() {
    @ExceptionHandler(RNException::class)
    fun handleException(exception: RNException): ResponseEntity<Any> = genericResponse(exception, HttpStatus.BAD_REQUEST)
}

@ControllerAdvice
class InvalidParameterExceptionHandler: RNErrorController() {
    @ExceptionHandler(InvalidParameterException::class)
    fun handleException(exception: InvalidParameterException): ResponseEntity<Any> = genericResponse(exception, HttpStatus.BAD_REQUEST)
}
