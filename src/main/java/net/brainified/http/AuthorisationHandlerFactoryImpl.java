package net.brainified.http;

import io.vertx.core.Handler;
import io.vertx.rxjava.ext.web.RoutingContext;
import net.brainified.db.Role;

final class AuthorisationHandlerFactoryImpl implements AuthorisationHandlerFactory {

  @Override
  public Handler<RoutingContext> createAuthorisationHandler(final Role[] allowedRoles) {
    return new AuthorisationHandler(allowedRoles);
  }

}
