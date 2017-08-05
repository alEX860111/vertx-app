package net.brainified.http;

import java.util.Collection;
import java.util.Collections;

import com.google.common.collect.Sets;

import io.vertx.core.Handler;
import io.vertx.rxjava.ext.web.RoutingContext;
import net.brainified.db.Role;

final class AuthorisationHandler implements Handler<RoutingContext> {

  private final Collection<Role> allowedRoles;

  public AuthorisationHandler(final Role[] allowedRoles) {
    this.allowedRoles = Collections.unmodifiableSet(Sets.newHashSet(allowedRoles));
  }

  @Override
  public void handle(RoutingContext routingContext) {
    final Role role = Role.valueOf(routingContext.user().principal().getString("role"));
    if (!allowedRoles.contains(role)) {
      routingContext.response().setStatusCode(403).end();
    } else {
      routingContext.next();
    }
  }

}
