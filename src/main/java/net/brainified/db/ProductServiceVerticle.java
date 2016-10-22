package net.brainified.db;

import javax.inject.Inject;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.eventbus.EventBus;
import io.vertx.rxjava.core.eventbus.Message;

final class ProductServiceVerticle extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProductServiceVerticle.class);

  private final EventBus eventBus;

  private final ProductDao productDao;

  @Inject
  public ProductServiceVerticle(final EventBus eventBus, final ProductDao productService) {
    this.eventBus = eventBus;
    this.productDao = productService;
  }

  @Override
  public void start() throws Exception {
    eventBus.<JsonObject>consumer("getProductCount").handler(this::handleGetProductCount);
    eventBus.<JsonObject>consumer("getProductList").handler(this::handleGetProductList);
    eventBus.<JsonObject>consumer("getProduct").handler(this::handleGetProduct);
    eventBus.<JsonObject>consumer("addProduct").handler(this::handleAddProduct);
    eventBus.<JsonObject>consumer("updateProduct").handler(this::handleUpdateProduct);
    eventBus.<JsonObject>consumer("deleteProduct").handler(this::handleDeleteProduct);
    LOGGER.info(this.getClass() + " initialized.");
  }

  private void handleGetProductCount(final Message<JsonObject> message) {
    productDao.getProductCount().subscribe(count -> {
      message.reply(count);
    }, error -> message.fail(0, error.getMessage()));
  }

  private void handleGetProductList(final Message<JsonObject> message) {
    final JsonObject params = message.body();
    final Integer page = params.getInteger("page");
    final Integer perpage = params.getInteger("perpage");
    productDao.getProductList(page, perpage).subscribe(products -> {
      message.reply(new JsonArray(products));
    }, error -> message.fail(0, error.getMessage()));
  }

  private void handleGetProduct(final Message<JsonObject> message) {
    final JsonObject params = message.body();
    final String id = params.getString("id");
    productDao.getProduct(id).subscribe(product -> {
      message.reply(product.orElse(null));
    }, error -> message.fail(0, error.getMessage()));
  }

  private void handleAddProduct(final Message<JsonObject> message) {
    final JsonObject params = message.body();
    final JsonObject product = params.getJsonObject("product");
    productDao.addProduct(product).subscribe(id -> {
      message.reply(id);
    }, error -> message.fail(0, error.getMessage()));
  }

  private void handleUpdateProduct(final Message<JsonObject> message) {
    final JsonObject params = message.body();
    final String id = params.getString("id");
    final JsonObject data = params.getJsonObject("data");
    productDao.updateProduct(id, data).subscribe(numModified -> {
      message.reply(numModified);
    }, error -> message.fail(0, error.getMessage()));
  }

  private void handleDeleteProduct(final Message<JsonObject> message) {
    final JsonObject params = message.body();
    final String id = params.getString("id");
    productDao.deleteProduct(id).subscribe(numDeleted -> {
      message.reply(numDeleted);
    }, error -> message.fail(0, error.getMessage()));
  }

}
