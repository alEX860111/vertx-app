package net.brainified.http.products;

import javax.inject.Inject;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.rxjava.ext.web.RoutingContext;
import net.brainified.db.Dao;
import net.brainified.db.Product;
import net.brainified.db.Role;
import net.brainified.http.HandlerConfiguration;
import net.brainified.http.RoutingContextHelper;

@HandlerConfiguration(path = "/products/:id", method = HttpMethod.PUT, allowedRoles = Role.ADMIN)
final class UpdateProductHandler implements Handler<RoutingContext> {

  private final RoutingContextHelper routingContextHelper;

  private final Dao<Product> dao;

  @Inject
  public UpdateProductHandler(final RoutingContextHelper routingContextHelper, final Dao<Product> dao) {
    this.routingContextHelper = routingContextHelper;
    this.dao = dao;
  }

  @Override
  public void handle(RoutingContext routingContext) {
    final Product product = routingContextHelper.getBody(routingContext, Product.class);

    product.set_id(routingContext.request().getParam("id"));

    dao.update(product).subscribe(updated -> {
      final int statusCode = updated ? 204 : 404;
      routingContext.response().setStatusCode(statusCode).end();
    }, routingContext::fail);
  }

}
