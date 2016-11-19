package net.brainified.db;

import javax.inject.Inject;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.eventbus.Message;

@EventBusHandlerConfiguration(address = "getProduct")
final class GetProductHandler implements Handler<Message<JsonObject>> {

  private final ProductDao productDao;

  @Inject
  public GetProductHandler(final ProductDao productDao) {
    this.productDao = productDao;
  }

  @Override
  public void handle(Message<JsonObject> message) {
    final JsonObject params = message.body();
    final String id = params.getString("id");
    productDao.getProduct(id).subscribe(product -> {
      message.reply(product.orElse(null));
    }, error -> message.fail(0, error.getMessage()));
  }

}
