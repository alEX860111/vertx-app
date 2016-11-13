package net.brainified.db;

import javax.inject.Inject;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.eventbus.Message;

@EventBusHandlerConfiguration(address = "updateProduct")
final class UpdateProductHandler implements Handler<Message<JsonObject>> {

  private ProductDao productDao;

  @Inject
  public UpdateProductHandler(final ProductDao productDao) {
    this.productDao = productDao;
  }

  @Override
  public void handle(Message<JsonObject> message) {
    final JsonObject params = message.body();
    final String id = params.getString("id");
    final JsonObject data = params.getJsonObject("data");
    productDao.updateProduct(id, data).subscribe(numModified -> {
      message.reply(numModified);
    }, error -> message.fail(0, error.getMessage()));
  }

}
