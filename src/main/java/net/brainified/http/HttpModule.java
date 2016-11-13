package net.brainified.http;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;

import io.vertx.core.Handler;
import io.vertx.core.Verticle;
import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.ext.web.RoutingContext;

public final class HttpModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(Router.class).toProvider(RouterProvider.class);

    final Multibinder<Handler<RoutingContext>> handlers = Multibinder.newSetBinder(binder(), new TypeLiteral<Handler<RoutingContext>>() {
    });
    handlers.addBinding().to(GetProductListHandler.class);
    handlers.addBinding().to(GetProductHandler.class);
    handlers.addBinding().to(AddProductHandler.class);
    handlers.addBinding().to(UpdateProductHandler.class);
    handlers.addBinding().to(DeleteProductHandler.class);

    bind(Verticle.class).annotatedWith(HttpVerticle.class).to(HttpServerVerticle.class);
  }
}
