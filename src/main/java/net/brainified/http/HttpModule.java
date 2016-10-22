package net.brainified.http;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;

import io.vertx.core.Handler;
import io.vertx.core.Verticle;
import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.ext.web.RoutingContext;

public final class HttpModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(Router.class).toProvider(RouterProvider.class);

    bind(new TypeLiteral<Handler<RoutingContext>>() {}).annotatedWith(GetProductList.class).to(GetProductListHandler.class);
    bind(new TypeLiteral<Handler<RoutingContext>>() {}).annotatedWith(GetProduct.class).to(GetProductHandler.class);
    bind(new TypeLiteral<Handler<RoutingContext>>() {}).annotatedWith(AddProduct.class).to(AddProductHandler.class);
    bind(new TypeLiteral<Handler<RoutingContext>>() {}).annotatedWith(UpdateProduct.class).to(UpdateProductHandler.class);
    bind(new TypeLiteral<Handler<RoutingContext>>() {}).annotatedWith(DeleteProduct.class).to(DeleteProductHandler.class);

    bind(Verticle.class).annotatedWith(HttpVerticle.class).to(HttpServerVerticle.class);
  }
}
