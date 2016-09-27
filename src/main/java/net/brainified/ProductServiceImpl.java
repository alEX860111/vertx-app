package net.brainified;

import java.util.List;

import javax.inject.Inject;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
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
  public void getProductCount(final Handler<AsyncResult<Long>> handler) {
    final JsonObject query = new JsonObject();
    client.count(PRODUCTS_COLLECTION, query, handler);
  }

  @Override
  public void getProductList(final Integer page, final Integer perpage, final Handler<AsyncResult<List<JsonObject>>> handler) {
    final JsonObject query = new JsonObject();
    final JsonObject sort = new JsonObject();
    sort.put("createdAt", DESC);
    final FindOptions options = new FindOptions().setLimit(perpage).setSkip((page - 1) * perpage).setSort(sort);
    client.findWithOptions(PRODUCTS_COLLECTION, query, options, handler);
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
