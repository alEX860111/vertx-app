package net.brainified;

import io.vertx.ext.web.RoutingContext;

final class IdParameterHandlerImpl implements IdParameterHandler {

  @Override
  public void handleIdParameter(final RoutingContext context) {
    try {
      final Integer id = Integer.valueOf(context.request().getParam("id"));
      context.put("id", id);
      context.next();
    } catch (final NumberFormatException e) {
      context.response().setStatusCode(400).end("Invalid id");
    }
  }

}
