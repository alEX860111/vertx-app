package net.brainified.http.users;

import javax.inject.Inject;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.rxjava.ext.web.RoutingContext;
import net.brainified.db.Dao;
import net.brainified.db.Role;
import net.brainified.db.User;
import net.brainified.http.HandlerConfiguration;
import net.brainified.http.RoutingContextHelper;
import net.brainified.http.login.HashService;
import rx.Observable;

@HandlerConfiguration(path = "/users/:id", method = HttpMethod.PUT, allowedRoles = { Role.USER, Role.ADMIN })
final class UpdateUserHandler implements Handler<RoutingContext> {

  private final RoutingContextHelper routingContextHelper;

  private final Dao<User> dao;

  private final HashService service;

  @Inject
  public UpdateUserHandler(final RoutingContextHelper routingContextHelper, final Dao<User> dao,
      final HashService service) {
    this.routingContextHelper = routingContextHelper;
    this.dao = dao;
    this.service = service;
  }

  @Override
  public void handle(RoutingContext routingContext) {
    final UpdateUserRequest updateUserRequest = routingContextHelper.getBody(routingContext, UpdateUserRequest.class);
    final String userId = routingContext.request().getParam("id");

    dao.getById(userId).flatMap(userOptional -> {
      if (!userOptional.isPresent()) {
        routingContext.response().setStatusCode(404).end();
        return Observable.never();
      }

      final User user = userOptional.get();
      final String passwordHash = service.hash(updateUserRequest.getOldPassword());
      if (!user.getPasswordHash().equals(passwordHash)) {
        routingContext.response().setStatusCode(403).end();
        return Observable.never();
      }

      user.setPasswordHash(service.hash(updateUserRequest.getNewPassword()));
      return dao.update(user);
    }).subscribe(updated -> {
      final int statusCode = updated ? 204 : 404;
      routingContext.response().setStatusCode(statusCode).end();
    }, routingContext::fail);

  }

}
