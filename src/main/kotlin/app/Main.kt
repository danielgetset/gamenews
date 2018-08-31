package app

import io.javalin.apibuilder.ApiBuilder.*
import io.javalin.Javalin
import khttp.responses.Response
import org.json.JSONArray
import io.javalin.rendering.template.JavalinPebble
import io.javalin.rendering.JavalinRenderer
import java.io.File
import kotlin.concurrent.timer



fun main(args: Array<String>) {

    val pebble = JavalinPebble

    JavalinRenderer.register(pebble, ".peb", ".pebble", ".html", ".xml")

    val app = Javalin.create().enableStaticFiles("/public").start(getHerokuAssignedPort())

    app.before("/") { ctx ->

        generateRss()
    }

    app.routes {

        get("/") { ctx ->
            ctx.redirect("/news.xml") // go directly to news rss file
        }

        get( "/news") { ctx ->
            ctx.render("news.peb", mapOf("pulses" to getGameNewsAndUpdate()))
        }

    }

}

private fun getHerokuAssignedPort(): Int {
    val processBuilder = ProcessBuilder()
    return if (processBuilder.environment()["PORT"] != null) {
        Integer.parseInt(processBuilder.environment()["PORT"])
    } else 7000
}

fun generateRss () {

    timer(initialDelay = 0, period = 30000) { // every 30 second

        System.out.print("Generating News RSS")

        val res = khttp.get(url = "http://localhost:" + getHerokuAssignedPort() + "/news")

        val file = File("news.xml")

        file.writeText(res.text)
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


