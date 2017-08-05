package net.brainified.http;

import io.vertx.core.Handler;
import io.vertx.rxjava.ext.web.RoutingContext;
import net.brainified.db.Role;

interface AuthorisationHandlerFactory {

  Handler<RoutingContext> createAuthorisationHandler(Role[] allowedRoles);

}