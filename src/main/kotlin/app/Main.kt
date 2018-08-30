package app

import io.javalin.Handler
import io.javalin.apibuilder.ApiBuilder.*
import io.javalin.Javalin
import khttp.responses.Response
import org.json.JSONArray
import io.javalin.rendering.template.JavalinPebble
import io.javalin.rendering.JavalinRenderer
import java.util.*
import kotlin.concurrent.timer



fun main(args: Array<String>) {

    //timer(initialDelay = 1000, period = 10000) {
    //    System.out.print("hello")
    //    //getGameNewsAndUpdate()
    //}

    val pebble = JavalinPebble

    JavalinRenderer.register(pebble, ".peb", ".pebble", ".html")

    val app = Javalin.create().start(7000)

    app.routes {

        get("/") { ctx ->
            ctx.render("news.peb", mapOf("pulses" to getGameNewsAndUpdate()))
        }

    }

}

fun getGameNewsAndUpdate () : Any {

    val parameters = mapOf("fields" to "*", "limit" to "50", "order" to "published_at:desc") // anything over 50 gave me response 400

    val response : Response = khttp.get(
            url = "https://api-endpoint.igdb.com/pulses/",
            headers = mapOf("user-key" to "806879ca5122ba2a8accaa412e6a2f2d", "Accept" to "application/json"),
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

        list.add(map)
    }

    return list
}


