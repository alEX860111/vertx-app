package net.brainified;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;

import io.vertx.core.Handler;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.eventbus.EventBus;
import io.vertx.rxjava.ext.mongo.MongoClient;
import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.ext.web.RoutingContext;

final class ApplicationModule extends AbstractModule {
  @Override
  protected void configure() {
    final Vertx vertx = Vertx.vertx();
    bind(Vertx.class).toInstance(vertx);
    bind(EventBus.class).toInstance(vertx.eventBus());

    bind(Router.class).toProvider(RouterProvider.class);
    bind(MongoClient.class).toProvider(MongoClientProvider.class);

    bind(ProductDao.class).to(ProductDaoImpl.class).in(Scopes.SINGLETON);

    bind(new TypeLiteral<Handler<RoutingContext>>() {}).annotatedWith(GetProductList.class).to(GetProductListHandler.class);
    bind(new TypeLiteral<Handler<RoutingContext>>() {}).annotatedWith(GetProduct.class).to(GetProductHandler.class);
    bind(new TypeLiteral<Handler<RoutingContext>>() {}).annotatedWith(AddProduct.class).to(AddProductHandler.class);
    bind(new TypeLiteral<Handler<RoutingContext>>() {}).annotatedWith(UpdateProduct.class).to(UpdateProductHandler.class);
    bind(new TypeLiteral<Handler<RoutingContext>>() {}).annotatedWith(DeleteProduct.class).to(DeleteProductHandler.class);
  }
}
