package net.brainified;

import io.vertx.ext.web.RoutingContext;

final class IdParameterHandlerImpl implements IdParameterHandler {

  @Override
  public void handleIdParameter(final RoutingContext routingContext) {
    try {
      final Integer id = Integer.valueOf(routingContext.request().getParam("id"));
      routingContext.put("id", id);
      routingContext.next();
    } catch (final NumberFormatException e) {
      routingContext.response().setStatusCode(400).end("Invalid product id");
    }
  }

}
