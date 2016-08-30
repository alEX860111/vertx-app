package net.brainified;

import java.util.List;

import javax.inject.Inject;

import io.vertx.core.Future;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

final class ProductHandlerImpl implements ProductHandler {

  private final ProductService service;

  @Inject
  public ProductHandlerImpl(final ProductService service) {
    this.service = service;
  }

  @Override
  public void getProducts(final RoutingContext routingContext) {
    final Future<List<Product>> future = service.getProducts();
    future.setHandler(productsResult -> {
      if (productsResult.succeeded()) {
        final List<Product> products = productsResult.result();
        routingContext.response().putHeader("content-type", "application/json; charset=utf-8").end(Json.encodePrettily(products));
      } else {
        routingContext.response().setStatusCode(500).end();
      }
    });
  }

  @Override
  public void getProduct(final RoutingContext routingContext) {
    final Integer id;
    try {
      id = Integer.valueOf(routingContext.request().getParam("id"));
    } catch (final NumberFormatException e) {
      routingContext.response().setStatusCode(400).end("Invalid product id");
      return;
    }

    final Future<Product> future = service.getProduct(id);
    future.setHandler(productResult -> {
      if (productResult.succeeded()) {
        final Product product = productResult.result();
        routingContext.response().putHeader("content-type", "application/json; charset=utf-8").end(Json.encodePrettily(product));
      } else {
        routingContext.response().setStatusCode(404).end();
      }
    });
  }

  @Override
  public void addProduct(final RoutingContext routingContext) {
    final JsonObject json;
    try {
      json = routingContext.getBodyAsJson();
    } catch (final DecodeException e) {
      routingContext.response().setStatusCode(400).end("Invalid JSON in body");
      return;
    }

    final Future<Product> future = service.addProduct(json.getString("name"), json.getInteger("price"));
    future.setHandler(productResult -> {
      if (productResult.succeeded()) {
        final Product product = productResult.result();
        routingContext.response().setStatusCode(201).putHeader("content-type", "application/json; charset=utf-8").end(Json.encodePrettily(product));
      } else {
        routingContext.response().setStatusCode(500).end();
      }
    });
  }

  @Override
  public void updateProduct(final RoutingContext routingContext) {
    final Integer id;
    try {
      id = Integer.valueOf(routingContext.request().getParam("id"));
    } catch (final NumberFormatException e) {
      routingContext.response().setStatusCode(400).end("Invalid product id");
      return;
    }

    final JsonObject json;
    try {
      json = routingContext.getBodyAsJson();
    } catch (final DecodeException e) {
      routingContext.response().setStatusCode(400).end("Invalid JSON in body");
      return;
    }

    final Future<Product> future = service.updateProduct(id, json.getString("name"), json.getInteger("price"));
    future.setHandler(productResult -> {
      if (productResult.succeeded()) {
        final Product product = productResult.result();
        routingContext.response().putHeader("content-type", "application/json; charset=utf-8").end(Json.encodePrettily(product));
      } else {
        routingContext.response().setStatusCode(404).end();
      }
    });

  }

  @Override
  public void deleteProduct(final RoutingContext routingContext) {
    final Integer id;
    try {
      id = Integer.valueOf(routingContext.request().getParam("id"));
    } catch (final NumberFormatException e) {
      routingContext.response().setStatusCode(400).end("Invalid product id");
      return;
    }

    final Future<Product> future = service.deleteProduct(id);
    future.setHandler(productResult -> {
      if (productResult.succeeded()) {
        routingContext.response().setStatusCode(204).end();
      } else {
        routingContext.response().setStatusCode(404).end();
      }
    });
  }

}
