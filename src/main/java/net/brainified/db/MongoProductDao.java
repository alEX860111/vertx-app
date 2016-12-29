package net.brainified.db;

import javax.inject.Inject;

import io.vertx.rxjava.ext.mongo.MongoClient;

final class MongoProductDao extends MongoDao<Product> {

  @Inject
  public MongoProductDao(final MongoClient client) {
    super(client, "products", Product.class);
  }

}
