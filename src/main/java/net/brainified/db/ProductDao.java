package net.brainified.db;

import javax.inject.Inject;

import io.vertx.rxjava.ext.mongo.MongoClient;

final class ProductDao extends MongoDao<Product> {

  @Inject
  public ProductDao(final MongoClient client) {
    super(client, "products", Product.class);
  }

}
