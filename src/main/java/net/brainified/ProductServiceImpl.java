package net.brainified;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import io.vertx.core.Future;
import io.vertx.core.Vertx;

final class ProductServiceImpl implements ProductService {

  private static final AtomicInteger COUNTER = new AtomicInteger();

  private static final int DELAY_IN_MS = 100;

  private Vertx vertx;

  private final Map<Integer, Product> products = new LinkedHashMap<>();

  @Inject
  public ProductServiceImpl(final Vertx vertx) {
    this.vertx = vertx;

    final Product iphone = createProduct("iphone", 100);
    products.put(iphone.getId(), iphone);
    final Product razr = createProduct("razr", 200);
    products.put(razr.getId(), razr);
  }

  @Override
  public Future<List<Product>> getProducts() {
    final Future<List<Product>> future = Future.future();
    vertx.setTimer(DELAY_IN_MS, timerId -> {
      final List<Product> productList = Lists.newArrayList(products.values());
      future.complete(productList);
    });
    return future;
  }

  @Override
  public Future<Product> getProduct(final int id) {
    final Future<Product> future = Future.future();
    vertx.setTimer(DELAY_IN_MS, timerId -> {
      final Product product = products.get(id);
      if (Objects.nonNull(product)) {
        future.complete(product);
      } else {
        future.fail("not found");
      }
    });
    return future;
  }

  @Override
  public Future<Product> addProduct(final String name, final Integer price) {
    final Future<Product> future = Future.future();
    vertx.setTimer(DELAY_IN_MS, timerId -> {
      final Product product = createProduct(name, price);
      products.put(product.getId(), product);
      future.complete(product);
    });
    return future;
  }

  @Override
  public Future<Product> updateProduct(final int id, String name, final Integer price) {
    final Future<Product> future = Future.future();
    vertx.setTimer(DELAY_IN_MS, timerId -> {
      if (products.containsKey(id)) {
        final Product product = products.get(id);
        product.setName(name);
        product.setPrice(price);
        future.complete(product);
      } else {
        future.fail("not found");
      }
    });
    return future;
  }

  @Override
  public Future<Product> deleteProduct(final int id) {
    final Future<Product> future = Future.future();
    vertx.setTimer(DELAY_IN_MS, timerId -> {
      final Product product = products.remove(id);
      if (Objects.nonNull(product)) {
        future.complete(product);
      } else {
        future.fail("not found");
      }
    });
    return future;
  }

  private Product createProduct(final String name, final Integer price) {
    final Product product = new Product();
    product.setId(COUNTER.incrementAndGet());
    product.setName(name);
    product.setPrice(price);
    return product;
  }

}
