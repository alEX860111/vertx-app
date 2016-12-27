package net.brainified.http.login;

import javax.inject.Inject;
import javax.inject.Provider;

import io.vertx.rxjava.ext.auth.jwt.JWTAuth;
import io.vertx.rxjava.ext.web.handler.JWTAuthHandler;

final class JWTAuthHandlerProvider implements Provider<JWTAuthHandler> {

  private final JWTAuth jwtAuth;

  @Inject
  public JWTAuthHandlerProvider(final JWTAuth jwtAuth) {
    this.jwtAuth = jwtAuth;
  }

  @Override
  public JWTAuthHandler get() {
    return JWTAuthHandler.create(jwtAuth);
  }

}
