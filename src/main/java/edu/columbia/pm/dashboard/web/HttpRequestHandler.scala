package edu.columbia.pm.dashboard.web

import io.vertx.core.Handler
import io.vertx.core.http.HttpServerRequest

class HttpRequestHandler extends Handler[HttpServerRequest] {
  override def handle(req: HttpServerRequest): Unit = {
    val resource = if (req.uri == "/") "web/index.html" else "web" + req.uri
    req.response.sendFile(resource)
  }
}
