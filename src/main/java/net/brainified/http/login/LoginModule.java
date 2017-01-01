package net.brainified.http.login;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;

import io.vertx.core.Handler;
import io.vertx.rxjava.ext.auth.jwt.JWTAuth;
import io.vertx.rxjava.ext.web.RoutingContext;
import io.vertx.rxjava.ext.web.handler.JWTAuthHandler;

public final class LoginModule extends AbstractModule {

  @Override
  protected void configure() {
    final Multibinder<Handler<RoutingContext>> handlers = Multibinder.newSetBinder(binder(), new TypeLiteral<Handler<RoutingContext>>() {});
    handlers.addBinding().to(LoginHandler.class);

    bind(JWTAuth.class).toProvider(JWTAuthProvider.class);
    bind(JWTAuthHandler.class).toProvider(JWTAuthHandlerProvider.class);
    bind(LoginService.class).to(LoginServiceImpl.class);
    bind(HashService.class).to(HashServiceImpl.class);
  }

}
