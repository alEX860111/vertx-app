package io.vertx.blog.first;

import javax.inject.Inject;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;

final class HttpServerVerticle extends AbstractVerticle {

	private final WhiskyHandler handler;

	@Inject
	public HttpServerVerticle(final WhiskyHandler handler) {
		this.handler = handler;
	}

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

		router.route("/assets/*").handler(StaticHandler.create("assets"));

		router.get("/api/whiskies").handler(handler::getAll);
		router.route("/api/whiskies*").handler(BodyHandler.create());
		router.post("/api/whiskies").handler(handler::addOne);
		router.get("/api/whiskies/:id").handler(handler::getOne);
		router.put("/api/whiskies/:id").handler(handler::updateOne);
		router.delete("/api/whiskies/:id").handler(handler::deleteOne);

		return router;
	}

}