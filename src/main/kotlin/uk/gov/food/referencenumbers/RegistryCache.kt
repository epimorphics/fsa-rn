package uk.gov.food.referencenumbers

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.collect.MinMaxPriorityQueue.maximumSize
import khttp.get
import khttp.responses.Response
import org.apache.commons.collections.map.LRUMap
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import sun.misc.Cache
import uk.gov.food.rn.RNException
import uk.gov.food.rn.Type
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalUnit
import java.util.concurrent.TimeUnit

data class Label(var name: String, var lang: String)
data class CachedType(var validUntil: Instant, var labels: List<Label>)
data class CachedAuthority(var validUntil: Instant, var labels: List<Label>)

class TypeNotPresentInRegistryException(message: String) : Exception(message)

val CACHE_UNIT : TemporalUnit = ChronoUnit.DAYS
val CACHE_DURATION : Long = 30L
val CACHE_SIZE: Int = 30

object RegistryCache {
    fun getJSON(value : JSONObject, key : String) : Any {
        try {
            var value : JSONObject = value.getJSONObject(key)
            return value
        } catch (e : JSONException){

        }
        try {
            var value : JSONArray = value.getJSONArray(key)
            return value
        } catch (e : JSONException){
            throw RNException("Could not parse registry data, either malformed or unavailable")
        }

    }

    fun registryValueHandler(type : String, keylength: Int) : (key: Int?) -> List<Label>? {
        return fun (key : Int?) : List<Label>? {
            var response : Response = get(url = "https://data.food.gov.uk/codes/reference-number/${type}/${String.format("%0${keylength}d", key)}?_format=jsonld", headers = mapOf("Accept" to "application/ld+json"))
            if (response.statusCode != 200) {
                throw TypeNotPresentInRegistryException("Invalid ${type}, ${type} with id=${key} could not be found in the registry")
            }
            var prefLabel = getJSON(response.jsonObject, "skos:prefLabel")
            var labels = ArrayList<Label>()
            if (prefLabel is JSONObject) {
                var name = prefLabel.getString("@value")
                var lang = prefLabel.getString("@language")
                labels.add(Label(name, lang))
            } else if (prefLabel is JSONArray) {
                for (index in 0..prefLabel.length()-1) {
                    var name = prefLabel.getJSONObject(index).getString("@value")
                    var lang = prefLabel.getJSONObject(index).getString("@language")
                    labels.add(Label(name, lang))
                }
            }
            return labels
        }
    }

    var authorities = CacheBuilder.newBuilder()
            .maximumSize(30)
            .expireAfterWrite(1, TimeUnit.DAYS)
            .build(CacheLoader.from(registryValueHandler("authority", 4)))

    var types = CacheBuilder.newBuilder()
            .maximumSize(30)
            .expireAfterWrite(1, TimeUnit.DAYS)
            .build(CacheLoader.from(registryValueHandler("type", 3)))

    fun getTypeLabels(id: Int) : List<Label> {
        return types.get(id)
    }

    fun getAuthorityLabels(id: Int) : List<Label> {
       return authorities.get(id)
    }
}

