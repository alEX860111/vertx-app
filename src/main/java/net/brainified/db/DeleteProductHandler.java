package net.brainified.db;

import javax.inject.Inject;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.eventbus.Message;

@EventBusHandlerConfiguration(address = "deleteProduct")
final class DeleteProductHandler implements Handler<Message<JsonObject>> {

  private final ProductDao productDao;

  @Inject
  public DeleteProductHandler(final ProductDao productDao) {
    this.productDao = productDao;
  }

  @Override
  public void handle(Message<JsonObject> message) {
    final JsonObject params = message.body();
    final String id = params.getString("id");
    productDao.deleteProduct(id).subscribe(numDeleted -> {
      message.reply(numDeleted);
    }, error -> message.fail(0, error.getMessage()));
  }

}
