package net.brainified.db;

import java.util.List;
import java.util.Optional;

import rx.Observable;

public interface ProductDao {

  Observable<Long> getProductCount();

  Observable<List<Product>> getProductList(Integer page, Integer perpage);

  Observable<Optional<Product>> getProduct(String id);

  Observable<Product> addProduct(Product product);

  Observable<Long> updateProduct(String id, Product product);

  Observable<Long> deleteProduct(String id);

}
