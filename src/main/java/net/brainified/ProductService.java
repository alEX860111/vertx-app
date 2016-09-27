package net.brainified;

import java.util.List;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import rx.Observable;

interface ProductService {

  void getProductCount(Handler<AsyncResult<Long>> handler);

  void getProductList(Integer page, Integer perpage, Handler<AsyncResult<List<JsonObject>>> handler);

  Observable<JsonObject> getProduct(String id);

  Observable<String> addProduct(JsonObject product);

  Observable<Long> updateProduct(String id, JsonObject data);

  Observable<Long> deleteProduct(String id);

}
