package net.brainified;

import javax.inject.Inject;
import javax.inject.Provider;

import io.vertx.rxjava.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.ext.mongo.MongoClient;

final class MongoClientProvider implements Provider<MongoClient> {

  private final Vertx vertx;

  @Inject
  public MongoClientProvider(final Vertx vertx) {
    this.vertx = vertx;
  }

  @Override
  public MongoClient get() {
    final JsonObject config = new JsonObject();
    return MongoClient.createShared(vertx, config);
  }

}
