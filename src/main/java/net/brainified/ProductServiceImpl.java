package net.brainified;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.inject.Inject;

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

    addProduct("iphone", 79900);
    addProduct("razr", 69900);
    addProduct("galaxy", 74900);
    addProduct("ngage", 49900);
    addProduct("nexus", 64900);
    addProduct("fairphone", 19900);
    addProduct("communicator", 39900);
    addProduct("edge", 59900);
  }

  @Override
  public Future<ProductContainer> getProductList(final Integer page, final Integer perpage) {
    final Future<ProductContainer> future = Future.future();
    vertx.setTimer(DELAY_IN_MS, timerId -> {
      final ProductContainer container = new ProductContainer();
      final int skip = (page - 1) * perpage;
      final List<Product> productList = products.values()
          .stream().sorted((p1, p2) -> p2.getId() - p1.getId())
          .skip(skip)
          .limit(perpage)
          .collect(Collectors.toList());
      container.setProducts(productList);
      container.setNumberOfProducts(products.size());
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

  private void addProduct(final String name, final Integer price) {
    final ProductData data = new ProductData();
    data.setName(name);
    data.setPrice(price);
    final Product product = createProduct(data);
    products.put(product.getId(), product);
  }

  private Product createProduct(final ProductData data) {
    final Product product = new Product();
    product.setId(COUNTER.incrementAndGet());
    product.setData(data);
    return product;
  }

}
