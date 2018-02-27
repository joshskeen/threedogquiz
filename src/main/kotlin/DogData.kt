import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.experimental.async
import java.net.URL

object DogData {
    private val API = "https://dog.ceo/api"
    private val BREEDS_ENDPOINT = API + "/breeds/list"
    private val IMAGE_ENDPOINT = API + "/breed/{breed}/images/random"
    private val dogList =
            async {
                val json = getMessageElement(URL(BREEDS_ENDPOINT).readText())
                Gson().fromJson(json, ArrayList<String>().javaClass)
            }

    fun randomBreed() = async { dogList.await().shuffled().last() }
    fun randomDogImage() = async {
        val randomBreed = randomBreed().await()
        val response = URL(IMAGE_ENDPOINT.replace("{breed}", randomBreed)).readText()
        println("fetch image for breed ${randomBreed} from url ${response} on thread ${Thread.currentThread().id}")
        val readBytes = URL(getMessageElement(response).asString).readBytes()
        Pair(randomBreed, readBytes)
    }
}

fun getMessageElement(response: String): JsonElement {
    val parser = JsonParser()
    val element = parser.parse(response) as JsonObject
    return element.get("message")
}