package net.brainified.db;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.rxjava.ext.mongo.MongoClient;
import rx.Observable;

final class MongoProductDao implements ProductDao {

  private static final int DESC = -1;

  private static final String PRODUCTS_COLLECTION = "products";

  private final MongoClient client;

  @Inject
  public MongoProductDao(final MongoClient client) {
    this.client = client;
  }

  @Override
  public Observable<Long> getProductCount() {
    final JsonObject query = new JsonObject();
    return client.countObservable(PRODUCTS_COLLECTION, query);
  }

  @Override
  public Observable<List<Product>> getProductList(final Integer page, final Integer perpage) {
    final JsonObject query = new JsonObject();
    final JsonObject sort = new JsonObject().put("createdAt", DESC);
    final FindOptions options = new FindOptions().setLimit(perpage).setSkip((page - 1) * perpage).setSort(sort);
    return client.findWithOptionsObservable(PRODUCTS_COLLECTION, query, options).map(documents -> {
      return documents.stream().map(document -> {
        return Json.decodeValue(Json.encode(document), Product.class);
      }).collect(Collectors.toList());
    });
  }

  @Override
  public Observable<Optional<Product>> getProduct(final String id) {
    final JsonObject query = new JsonObject().put("_id", id);
    final JsonObject fields = new JsonObject();
    return client.findOneObservable(PRODUCTS_COLLECTION, query, fields).map(document -> {
      if (Objects.isNull(document)) {
        return Optional.empty();
      }
      final Product product = Json.decodeValue(Json.encode(document), Product.class);
      return Optional.of(product);
    });
  }

  @Override
  public Observable<Product> addProduct(final ProductData data) {
    final Product product = new Product();
    product.setData(data);
    product.setCreatedAt(Instant.now().toString());

    final JsonObject document = new JsonObject(Json.encodePrettily(product));
    document.remove("_id");
 
    return client.insertObservable(PRODUCTS_COLLECTION, document).map(id -> {
      product.set_id(id);
      return product;
    });
  }

  @Override
  public Observable<Long> updateProduct(final String id, final ProductData data) {
    final JsonObject query = new JsonObject().put("_id", id);

    final JsonObject product = new JsonObject().put("data", new JsonObject(Json.encodePrettily(data)));
    final JsonObject update = new JsonObject().put("$set", product);

    return client.updateCollectionObservable(PRODUCTS_COLLECTION, query, update).map(result -> {
      return result.getDocModified();
    });
  }

  @Override
  public Observable<Long> deleteProduct(final String id) {
    final JsonObject query = new JsonObject().put("_id", id);
    return client.removeDocumentObservable(PRODUCTS_COLLECTION, query).map(result -> {
      return result.getRemovedCount();
    });
  }

}
