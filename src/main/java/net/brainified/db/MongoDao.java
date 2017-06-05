package net.brainified.db;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.rxjava.ext.mongo.MongoClient;
import rx.Observable;

class MongoDao<T extends MongoObject> implements Dao<T> {

  private final MongoClient client;

  private final String collectionName;

  private final Class<T> clazz;

  public MongoDao(final MongoClient client, final String collectionName, final Class<T> clazz) {
    this.client = client;
    this.collectionName = collectionName;
    this.clazz = clazz;
  }

  @Override
  public Observable<Long> getCount() {
    final JsonObject query = new JsonObject();
    return client.countObservable(collectionName, query);
  }

  @Override
  public Observable<List<T>> getList(final Integer page, final Integer perpage, final String sortKey, final SortOrder sortOrder) {
    final JsonObject query = new JsonObject();
    final JsonObject sort = new JsonObject().put(sortKey, sortOrder.getValue());

    final FindOptions options = new FindOptions()
        .setLimit(perpage)
        .setSkip((page - 1) * perpage)
        .setSort(sort);

    return client.findWithOptionsObservable(collectionName, query, options).map(documents -> {
      return documents
          .stream()
          .map(document -> Json.mapper.convertValue(document, clazz))
          .collect(Collectors.toList());
    });
  }

  @Override
  public Observable<Optional<T>> getById(final String id) {
    final JsonObject query = new JsonObject().put("_id", id);
    return getOne(query);
  }

  @Override
  public Observable<Optional<T>> getByKey(final String key, final String value) {
    final JsonObject query = new JsonObject().put(key, value);
    return getOne(query);
  }

  private Observable<Optional<T>> getOne(final JsonObject query) {
    final JsonObject fields = new JsonObject();

    return client.findOneObservable(collectionName, query, fields).map(document -> {
      if (Objects.isNull(document)) {
        return Optional.empty();
      }
      final T object = Json.mapper.convertValue(document, clazz);
      return Optional.of(object);
    });
  }

  @Override
  public Observable<T> add(final T object) {
    object.setCreatedAt(Instant.now().toString());

    final JsonObject document = new JsonObject(Json.encode(object));
    document.remove("_id");

    return client.insertObservable(collectionName, document).map(id -> {
      object.set_id(id);
      return object;
    });
  }

  @Override
  public Observable<Boolean> update(final T object) {
    final JsonObject query = new JsonObject().put("_id", object.get_id());

    final JsonObject document = new JsonObject(Json.encodePrettily(object));
    document.remove("_id");
    document.remove("createdAt");

    final JsonObject update = new JsonObject().put("$set", document);

    return client.updateCollectionObservable(collectionName, query, update).map(result -> {
      if (result.getDocMatched() == 1) {
        return true;
      }
      if (result.getDocMatched() == 0) {
        return false;
      }
      throw new IllegalStateException(String.format("Modified %s documents for id '%s'. The id should be unique.",
          result.getDocModified(), object.get_id()));
    });
  }

  @Override
  public Observable<Boolean> delete(final String id) {
    final JsonObject query = new JsonObject().put("_id", id);
    return client.removeDocumentObservable(collectionName, query).map(result -> {
      if (result.getRemovedCount() == 1) {
        return true;
      }
      if (result.getRemovedCount() == 0) {
        return false;
      }
      throw new IllegalStateException(
          String.format("Removed %s documents for id '%s'. The id should be unique.", result.getRemovedCount(), id));
    });
  }

}
