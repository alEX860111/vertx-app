package net.brainified.db;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

import javax.inject.Inject;

import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.ext.mongo.MongoClient;
import rx.Observable;

final class MongoUserDao implements UserDao {

  private static final String USERS_COLLECTION = "users";

  private final MongoClient client;

  @Inject
  public MongoUserDao(final MongoClient client) {
    this.client = client;
  }

  @Override
  public Observable<Optional<User>> searchUser(final String username) {
    final JsonObject query = new JsonObject().put("username", username);
    return findUser(query);
  }

  @Override
  public Observable<Optional<User>> getUser(final String id) {
    final JsonObject query = new JsonObject().put("_id", id);
    return findUser(query);
  }

  @Override
  public Observable<User> addUser(final User user) {
    user.setCreatedAt(Instant.now().toString());

    final JsonObject document = new JsonObject(Json.encodePrettily(user));
    document.remove("_id");
 
    return client.insertObservable(USERS_COLLECTION, document).map(id -> {
      user.set_id(id);
      return user;
    });
  }


  
  private Observable<Optional<User>> findUser(final JsonObject query) {
    final JsonObject fields = new JsonObject();
    return client.findOneObservable(USERS_COLLECTION, query, fields).map(document -> {
      if (Objects.isNull(document)) {
        return Optional.empty();
      }
      final User user = Json.decodeValue(Json.encode(document), User.class);
      return Optional.of(user);
    });
  }

}
