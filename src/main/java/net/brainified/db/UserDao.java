package net.brainified.db;

import javax.inject.Inject;

import io.vertx.rxjava.ext.mongo.MongoClient;

final class UserDao extends MongoDao<User> {

  @Inject
  public UserDao(final MongoClient client) {
    super(client, "users", User.class);
  }

}
