package net.brainified;

import java.util.List;

import javax.inject.Inject;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.rxjava.ext.mongo.MongoClient;
import rx.Observable;

final class ProductServiceImpl implements ProductService {

  private static final int DESC = -1;

  private static final String PRODUCTS_COLLECTION = "products";

  private final MongoClient client;

  @Inject
  public ProductServiceImpl(MongoClient client) {
    this.client = client;
  }

  @Override
  public Observable<Long> getProductCount() {
    final JsonObject query = new JsonObject();
    return client.countObservable(PRODUCTS_COLLECTION, query);
  }

  @Override
  public Observable<List<JsonObject>> getProductList(final Integer page, final Integer perpage) {
    final JsonObject query = new JsonObject();
    final JsonObject sort = new JsonObject();
    sort.put("createdAt", DESC);
    final FindOptions options = new FindOptions().setLimit(perpage).setSkip((page - 1) * perpage).setSort(sort);
    return client.findWithOptionsObservable(PRODUCTS_COLLECTION, query, options);
  }

  @Override
  public Observable<JsonObject> getProduct(final String id) {
    final JsonObject query = new JsonObject();
    query.put("_id", id);
    final JsonObject fields = new JsonObject();
    return client.findOneObservable(PRODUCTS_COLLECTION, query, fields);
  }

  @Override
  public Observable<String> addProduct(final JsonObject product) {
    return client.insertObservable(PRODUCTS_COLLECTION, product);
  }

  @Override
  public Observable<Long> updateProduct(final String id, final JsonObject data) {
    final JsonObject query = new JsonObject().put("_id", id);

    final JsonObject product = new JsonObject().put("data", data);
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
