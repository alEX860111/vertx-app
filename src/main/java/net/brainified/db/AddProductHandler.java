package net.brainified.db;

import javax.inject.Inject;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.eventbus.Message;

@EventBusHandlerConfiguration(address = "addProduct")
final class AddProductHandler implements Handler<Message<JsonObject>> {

  private final ProductDao productDao;

  @Inject
  public AddProductHandler(final ProductDao productDao) {
    this.productDao = productDao;
  }

  @Override
  public void handle(Message<JsonObject> message) {
    final JsonObject params = message.body();
    final JsonObject data = params.getJsonObject("data");
    productDao.addProduct(data).subscribe(product -> {
      message.reply(product);
    }, error -> message.fail(0, error.getMessage()));
  }

}
