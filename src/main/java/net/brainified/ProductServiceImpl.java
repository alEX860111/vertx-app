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

  private static final int DELAY_IN_MS = 100;

  private final AtomicInteger COUNTER = new AtomicInteger();

  private final Vertx vertx;

  private final Map<Integer, Product> products = new LinkedHashMap<>();

  @Inject
  public ProductServiceImpl(final Vertx vertx) {
    this.vertx = vertx;

    final ProductData dataIphone = new ProductData();
    dataIphone.setName("iphone");
    dataIphone.setPrice(100);
    final Product iphone = createProduct(dataIphone);
    products.put(iphone.getId(), iphone);

    final ProductData dataRazr = new ProductData();
    dataRazr.setName("razr");
    dataRazr.setPrice(200);
    final Product razr = createProduct(dataRazr);
    products.put(razr.getId(), razr);
  }

  @Override
  public Future<ProductContainer> getProducts() {
    final Future<ProductContainer> future = Future.future();
    vertx.setTimer(DELAY_IN_MS, timerId -> {
      final ProductContainer container = new ProductContainer();
      final List<Product> productList = Lists.newArrayList(products.values());
      container.setProducts(productList);
      future.complete(container);
    });
    return future;
  }

  @Override
  public Future<Product> getProduct(final Integer id) {
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
  public Future<Product> addProduct(final ProductData data) {
    final Future<Product> future = Future.future();
    vertx.setTimer(DELAY_IN_MS, timerId -> {
      final Product product = createProduct(data);
      products.put(product.getId(), product);
      future.complete(product);
    });
    return future;
  }

  @Override
  public Future<Product> updateProduct(final Integer id, final ProductData data) {
    final Future<Product> future = Future.future();
    vertx.setTimer(DELAY_IN_MS, timerId -> {
      if (products.containsKey(id)) {
        final Product product = products.get(id);
        product.setData(data);
        future.complete(product);
      } else {
        future.fail("not found");
      }
    });
    return future;
  }

  @Override
  public Future<Product> deleteProduct(final Integer id) {
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

  private Product createProduct(final ProductData data) {
    final Product product = new Product();
    product.setId(COUNTER.incrementAndGet());
    product.setData(data);
    return product;
  }

}
