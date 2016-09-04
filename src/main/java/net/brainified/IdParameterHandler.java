package net.brainified;

import io.vertx.ext.web.RoutingContext;

interface IdParameterHandler {

  void handleIdParameter(RoutingContext routingContext);

}