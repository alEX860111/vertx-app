package net.brainified.http;

import com.google.inject.AbstractModule;

import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.ext.web.handler.AuthHandler;
import io.vertx.rxjava.ext.web.handler.JWTAuthHandler;
import net.brainified.http.login.LoginModule;
import net.brainified.http.products.ProductsHandlerModule;
import net.brainified.http.users.UsersHandlerModule;

public final class HttpModule extends AbstractModule {

  @Override
  protected void configure() {
    install(new ProductsHandlerModule());
    install(new UsersHandlerModule());
    install(new LoginModule());

    bind(Router.class).toProvider(RouterProvider.class);
    bind(RoutingContextHelper.class).to(RoutingContextHelperImpl.class);
    bind(AuthHandler.class).to(JWTAuthHandler.class);

    bind(StatusCodeRegistry.class).toProvider(StatusCodeRegistryProvider.class);
  }

}
