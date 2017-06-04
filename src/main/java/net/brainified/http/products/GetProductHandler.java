package net.brainified.http.products;

import javax.inject.Inject;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.Json;
import io.vertx.rxjava.ext.web.RoutingContext;
import net.brainified.db.Dao;
import net.brainified.db.Product;
import net.brainified.http.HandlerConfiguration;

@HandlerConfiguration(path = "/products/:id", method = HttpMethod.GET, requiresAuthentication = true)
final class GetProductHandler implements Handler<RoutingContext> {

  private final Dao<Product> dao;

  @Inject
  public GetProductHandler(final Dao<Product> dao) {
    this.dao = dao;
  }

  @Override
  public void handle(final RoutingContext routingContext) {
    final String id = routingContext.request().getParam("id");

    dao.getById(id).subscribe(product -> {
      if (product.isPresent()) {
        routingContext.response()
          .putHeader("Content-Type", "application/json; charset=utf-8")
          .end(Json.encodePrettily(product.get()));
      } else {
        routingContext.response().setStatusCode(404).end();
      }
    }, routingContext::fail);

  }

}
