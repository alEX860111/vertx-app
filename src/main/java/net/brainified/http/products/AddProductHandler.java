package net.brainified.http.products;

import javax.inject.Inject;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.Json;
import io.vertx.rxjava.ext.web.RoutingContext;
import net.brainified.db.Dao;
import net.brainified.db.Product;
import net.brainified.db.Role;
import net.brainified.http.HandlerConfiguration;
import net.brainified.http.RoutingContextHelper;

@HandlerConfiguration(path = "/products", method = HttpMethod.POST, allowedRoles = Role.ADMIN)
final class AddProductHandler implements Handler<RoutingContext> {

  private final RoutingContextHelper routingContextHelper;

  private final Dao<Product> dao;

  @Inject
  public AddProductHandler(final RoutingContextHelper routingContextHelper, final Dao<Product> dao) {
    this.routingContextHelper = routingContextHelper;
    this.dao = dao;
  }

  @Override
  public void handle(final RoutingContext routingContext) {
    final Product product = routingContextHelper.getBody(routingContext, Product.class);

    dao.add(product).subscribe(savedProduct -> {
      routingContext
        .response()
        .setStatusCode(201)
        .putHeader("Content-Type", "application/json; charset=utf-8")
        .putHeader("Location", routingContext.request().absoluteURI() + "/" + savedProduct.get_id())
        .end(Json.encodePrettily(savedProduct));
    }, routingContext::fail);

  }

}
