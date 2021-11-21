package com.ak.async

import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.ext.web.Router
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import org.slf4j.Logger

class MainVerticle : AbstractVerticle() {

  private val port = 8000
  private val servicePrefix = "[MAIN]"
  private val logger: Logger = org.slf4j.LoggerFactory.getLogger(MainVerticle::class.java)

  override fun start(startPromise: Promise<Void>) {
    // Create a Router
    val router = Router.router(vertx)

    // Mount the handler for all incoming requests at every path and HTTP method
    router.route().handler { context ->
      // Get the address of the request
      val address = context.request().connection().remoteAddress().toString()
      // Get the query parameter "name"
      val queryParams = context.queryParams()
      val name = queryParams.get("name") ?: "unknown"
      // Write a json response
      context.json(
        json {
          obj(
            "name" to name,
            "address" to address,
            "message" to "Hello $name connected from $address"
          )
        }
      )
    }

    // Create the HTTP server
    vertx.createHttpServer()
      // Handle every request using the router
      .requestHandler(router)
      // Start listening
      .listen(port)
      // Print the port
      .onSuccess {
        logger.info("$servicePrefix HTTP server started on port $port")
      }
  }
}
