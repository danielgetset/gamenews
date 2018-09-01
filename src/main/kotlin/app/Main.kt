package app

import com.beust.klaxon.Klaxon
import io.javalin.apibuilder.ApiBuilder.*
import io.javalin.Javalin
import khttp.responses.Response
import org.json.JSONArray
import io.javalin.rendering.template.JavalinPebble
import io.javalin.rendering.JavalinRenderer
import java.io.File

fun main(args: Array<String>) {

    val pebble = JavalinPebble

    JavalinRenderer.register(pebble, ".peb", ".pebble", ".html", ".xml")

    val app = Javalin.create().start(getHerokuAssignedPort())

    app.routes {

        get( "/") { ctx ->
            ctx.render("news.peb", mapOf("pulses" to getGameNewsAndUpdate())).contentType("xml")
        }

    }

}

//fun generateRss () {
//
//    //timer(initialDelay = 0, period = 60000) { // every 60 second
//
//        print("Generating News RSS")
//        System.out.print("##### TESTING ###")
//
//        val res = khttp.get(url = "http://localhost:" + getHerokuAssignedPort() + "/news")
//
//        val file = File("/tmp/public/news.xml")
//
//        file.writeText(res.text)
//
//    //}
//
//}

fun pathtofile (): String {

    val processBuilder = ProcessBuilder()

    if (processBuilder.environment()["OTHER_VAR"] == "production") {

        return "/app/target/classes/"

    } else return "./src/main/resources/"
}

fun getGameNewsAndUpdate () : Any {

    val settingsFile = File(pathtofile() + "private/settings")

    val settings = Klaxon().parse<Settings>(settingsFile.readText())

    val apiKey  = settings!!.apiKey

    val parameters = mapOf("fields" to "*", "limit" to "50", "order" to "published_at:desc") // anything over 50 gave me response 400

    val response : Response = khttp.get(
            url = "https://api-endpoint.igdb.com/pulses/",
            headers = mapOf("user-key" to apiKey, "Accept" to "application/json"),
            params = parameters)

    val pulses : JSONArray = response.jsonArray

    val list = mutableListOf<Map<String, Any>>()

    for (i in 0..(pulses.length() - 1)) {
        val item = pulses.getJSONObject(i)

        val map = mapOf(
                "id" to item["id"],
                "pulsesource" to item["pulse_source"],
                "title" to item["title"],
                "summary" to item["summary"],
                "url" to item["url"],
                "uid" to item["uid"],
                "publishedat" to item["published_at"]
                // author doesn't exist? : "author" to item["author"]
        )

        //val stamp = Timestamp(System.currentTimeMillis())
        //val date = Date(stamp.getTime())
        //println(date)

        list.add(map)
    }

    return list
}

private fun getHerokuAssignedPort(): Int {
    val processBuilder = ProcessBuilder()
    return if (processBuilder.environment()["PORT"] != null) {
        Integer.parseInt(processBuilder.environment()["PORT"])
    } else 7000
}

class Settings(val userName: String, val apiKey: String)

