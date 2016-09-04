package net.brainified;

import io.vertx.core.Future;

interface ProductService {

  Future<ProductContainer> getProducts();

  Future<Product> getProduct(Integer id);

  Future<Product> addProduct(ProductData data);

  Future<Product> updateProduct(Integer id, ProductData data);

  Future<Product> deleteProduct(Integer id);

}
