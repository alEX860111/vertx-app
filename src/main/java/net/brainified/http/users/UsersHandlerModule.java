package net.brainified.http.users;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;

import io.vertx.core.Handler;
import io.vertx.rxjava.ext.web.RoutingContext;

public final class UsersHandlerModule extends AbstractModule {

  @Override
  protected void configure() {
    final Multibinder<Handler<RoutingContext>> handlers = Multibinder.newSetBinder(binder(), new TypeLiteral<Handler<RoutingContext>>() {});
    handlers.addBinding().to(AddUserHandler.class);
    handlers.addBinding().to(GetUserHandler.class);
    handlers.addBinding().to(UpdateUserHandler.class);
  }

}
