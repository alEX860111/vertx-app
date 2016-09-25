package net.brainified;

import java.util.List;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClientDeleteResult;
import io.vertx.ext.mongo.MongoClientUpdateResult;

interface ProductService {

  void getProductCount(Handler<AsyncResult<Long>> handler);

  void getProductList(Integer page, Integer perpage, Handler<AsyncResult<List<JsonObject>>> handler);

  void getProduct(String id, Handler<AsyncResult<JsonObject>> handler);

  void addProduct(JsonObject product, Handler<AsyncResult<String>> handler);

  void updateProduct(String id, JsonObject data, Handler<AsyncResult<MongoClientUpdateResult>> handler);

  void deleteProduct(String id, Handler<AsyncResult<MongoClientDeleteResult>> handler);

}
