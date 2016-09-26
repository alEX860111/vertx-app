package net.brainified;

import java.util.List;

import javax.inject.Inject;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.mongo.MongoClientDeleteResult;
import io.vertx.ext.mongo.MongoClientUpdateResult;

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
    final FindOptions options = new FindOptions()
        .setLimit(perpage)
        .setSkip((page - 1) * perpage)
        .setSort(sort);
    client.findWithOptions(PRODUCTS_COLLECTION, query, options, handler);
  }

  @Override
  public void getProduct(final String id, final Handler<AsyncResult<JsonObject>> handler) {
    final JsonObject query = new JsonObject();
    query.put("_id", id);
    final JsonObject fields = new JsonObject();
    client.findOne(PRODUCTS_COLLECTION, query, fields, handler);
  }

  @Override
  public void addProduct(final JsonObject product, final Handler<AsyncResult<String>> handler) {
    client.insert(PRODUCTS_COLLECTION, product, handler);
  }

  @Override
  public void updateProduct(final String id, final JsonObject data, final Handler<AsyncResult<MongoClientUpdateResult>> handler) {
    final JsonObject query = new JsonObject();
    query.put("_id", id);

    final JsonObject product = new JsonObject();
    product.put("data", data);

    final JsonObject update = new JsonObject();
    update.put("$set", product);

    client.updateCollection(PRODUCTS_COLLECTION, query, update, handler);
  }

  @Override
  public void deleteProduct(final String id, final Handler<AsyncResult<MongoClientDeleteResult>> handler) {
    final JsonObject query = new JsonObject();
    query.put("_id", id);
    client.removeDocument(PRODUCTS_COLLECTION, query, handler);
  }

}
