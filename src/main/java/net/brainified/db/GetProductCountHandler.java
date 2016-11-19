package net.brainified.db;

import javax.inject.Inject;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.eventbus.Message;

@EventBusHandlerConfiguration(address = "getProductCount")
final class GetProductCountHandler implements Handler<Message<JsonObject>> {

  private final ProductDao productDao;

  @Inject
  public GetProductCountHandler(final ProductDao productDao) {
    this.productDao = productDao;
  }

  @Override
  public void handle(Message<JsonObject> message) {
    productDao.getProductCount().subscribe(count -> {
      message.reply(count);
    }, error -> message.fail(0, error.getMessage()));
  }

}
