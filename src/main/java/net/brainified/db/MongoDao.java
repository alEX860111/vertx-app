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
import rx.Single;

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
  public Single<ItemContainer<T>> getList(final Integer page, final Integer perpage, final String sortKey, final SortOrder sortOrder) {
    final JsonObject query = new JsonObject();
    final JsonObject sort = new JsonObject().put(sortKey, sortOrder.getValue());

    final FindOptions options = new FindOptions()
        .setLimit(perpage)
        .setSkip((page - 1) * perpage)
        .setSort(sort);

    final Single<Long> countObservable = client.rxCount(collectionName, query);
    final Single<List<JsonObject>> documentsObservable = client.rxFindWithOptions(collectionName, query, options);

    return Single.zip(countObservable, documentsObservable, (count, documents) -> {
      final List<T> items = documents
          .stream()
          .map(document -> Json.mapper.convertValue(document, clazz))
          .collect(Collectors.toList());
        return new ItemContainer<>(count, items);
    });

  }

  @Override
  public Single<Optional<T>> getById(final String id) {
    final JsonObject query = new JsonObject().put("_id", id);
    return getOne(query);
  }

  @Override
  public Single<Optional<T>> getByKey(final String key, final String value) {
    final JsonObject query = new JsonObject().put(key, value);
    return getOne(query);
  }

  private Single<Optional<T>> getOne(final JsonObject query) {
    final JsonObject fields = new JsonObject();

    return client.rxFindOne(collectionName, query, fields).map(document -> {
      if (Objects.isNull(document)) {
        return Optional.empty();
      }
      final T object = Json.mapper.convertValue(document, clazz);
      return Optional.of(object);
    });
  }

  @Override
  public Single<T> add(final T object) {
    object.setCreatedAt(Instant.now().toString());

    final JsonObject document = new JsonObject(Json.encode(object));
    document.remove("_id");

    return client.rxInsert(collectionName, document).map(id -> {
      object.set_id(id);
      return object;
    });
  }

  @Override
  public Single<Boolean> update(final T object) {
    final JsonObject query = new JsonObject().put("_id", object.get_id());

    final JsonObject document = new JsonObject(Json.encodePrettily(object));
    document.remove("_id");
    document.remove("createdAt");

    final JsonObject update = new JsonObject().put("$set", document);

    return client.rxUpdateCollection(collectionName, query, update).map(result -> {
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
  public Single<Boolean> delete(final String id) {
    final JsonObject query = new JsonObject().put("_id", id);
    return client.rxRemoveDocument(collectionName, query).map(result -> {
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
