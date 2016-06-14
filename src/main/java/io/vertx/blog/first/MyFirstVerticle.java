package io.vertx.blog.first;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;

public final class MyFirstVerticle extends AbstractVerticle {

	@Override
	public void start(final Future<Void> fut) {
		final Router router = createRouter();

		vertx.createHttpServer().requestHandler(router::accept).listen(config().getInteger("http.port", 8080), result -> {
			if (result.succeeded()) {
				fut.complete();
			} else {
				fut.fail(result.cause());
			}
		});
	}

	private Router createRouter() {
		final Router router = Router.router(vertx);

		router.route("/").handler(this::handleRoot);

		router.route("/assets/*").handler(StaticHandler.create("assets"));

		final WhiskyService service = new WhiskyServiceImpl();

		router.get("/api/whiskies").handler(service::getAll);
		router.route("/api/whiskies*").handler(BodyHandler.create());
		router.post("/api/whiskies").handler(service::addOne);
		router.get("/api/whiskies/:id").handler(service::getOne);
		router.put("/api/whiskies/:id").handler(service::updateOne);
		router.delete("/api/whiskies/:id").handler(service::deleteOne);

		return router;
	}

	private void handleRoot(RoutingContext routingContext) {
		routingContext.response().putHeader("content-type", "text/html")
				.end("<h1>Hello from my first Vert.x 3 application!!!</h1>");
	}

}