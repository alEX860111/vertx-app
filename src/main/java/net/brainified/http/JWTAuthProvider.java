package net.brainified.http;

import javax.inject.Inject;
import javax.inject.Provider;

import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.ext.auth.jwt.JWTAuth;

final class JWTAuthProvider implements Provider<JWTAuth> {

  private final Vertx vertx;

  @Inject
  public JWTAuthProvider(final Vertx vertx) {
    this.vertx = vertx;
  }

  @Override
  public JWTAuth get() {
    final JsonObject keystore = new JsonObject()
        .put("path", "keystore.jceks")
        .put("type", "jceks")
        .put("password", "secret");
    final JsonObject config = new JsonObject()
        .put("keyStore", keystore);

    return JWTAuth.create(vertx, config);
  }

}
