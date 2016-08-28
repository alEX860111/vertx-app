package net.brainified;

import io.vertx.ext.web.RoutingContext;

interface ProductHandler {

  void getProducts(RoutingContext routingContext);

  void getProduct(RoutingContext routingContext);

  void addProduct(RoutingContext routingContext);

  void updateProduct(RoutingContext routingContext);

  void deleteProduct(RoutingContext routingContext);

}