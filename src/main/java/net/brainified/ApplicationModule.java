package net.brainified;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

final class ApplicationModule extends AbstractModule {
  @Override
  protected void configure() {
    final Vertx vertx = Vertx.vertx();
    bind(Vertx.class).toInstance(vertx);

    bind(Router.class).toProvider(RouterProvider.class);

    bind(ProductService.class).to(ProductServiceImpl.class).in(Scopes.SINGLETON);

    bind(new TypeLiteral<Handler<RoutingContext>>() {}).annotatedWith(GetProductList.class).to(GetProductListHandler.class);
    bind(new TypeLiteral<Handler<RoutingContext>>() {}).annotatedWith(GetProduct.class).to(GetProductHandler.class);
    bind(new TypeLiteral<Handler<RoutingContext>>() {}).annotatedWith(AddProduct.class).to(AddProductHandler.class);
    bind(new TypeLiteral<Handler<RoutingContext>>() {}).annotatedWith(UpdateProduct.class).to(UpdateProductHandler.class);
    bind(new TypeLiteral<Handler<RoutingContext>>() {}).annotatedWith(DeleteProduct.class).to(DeleteProductHandler.class);
  }
}
