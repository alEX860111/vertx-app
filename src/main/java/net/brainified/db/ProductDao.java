package net.brainified.db;

import java.util.List;
import java.util.Optional;

import io.vertx.core.json.JsonObject;
import rx.Observable;

interface ProductDao {

  Observable<Long> getProductCount();

  Observable<List<JsonObject>> getProductList(Integer page, Integer perpage);

  Observable<Optional<JsonObject>> getProduct(String id);

  Observable<String> addProduct(JsonObject product);

  Observable<Long> updateProduct(String id, JsonObject data);

  Observable<Long> deleteProduct(String id);

}
