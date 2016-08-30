package net.brainified;

import io.vertx.core.Future;

interface ProductService {

  Future<ProductContainer> getProducts();

  Future<Product> getProduct(int id);

  Future<Product> addProduct(String name, Integer price);

  Future<Product> updateProduct(int id, String name, Integer price);

  Future<Product> deleteProduct(int id);

}
