package net.brainified;

import java.util.List;

import io.vertx.core.json.JsonObject;
import rx.Observable;

interface ProductService {

  Observable<Long> getProductCount();

  Observable<List<JsonObject>> getProductList(Integer page, Integer perpage);

  Observable<JsonObject> getProduct(String id);

  Observable<String> addProduct(JsonObject product);

  Observable<Long> updateProduct(String id, JsonObject data);

  Observable<Long> deleteProduct(String id);

}
