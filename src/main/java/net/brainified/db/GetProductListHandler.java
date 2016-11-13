package net.brainified.db;

import javax.inject.Inject;


import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.eventbus.Message;

@EventBusHandlerConfiguration(address = "getProductList")
final class GetProductListHandler implements Handler<Message<JsonObject>> {

  private ProductDao productDao;

  @Inject
  public GetProductListHandler(final ProductDao productDao) {
    this.productDao = productDao;
  }

  @Override
  public void handle(Message<JsonObject> message) {
    final JsonObject params = message.body();
    final Integer page = params.getInteger("page");
    final Integer perpage = params.getInteger("perpage");
    productDao.getProductList(page, perpage).subscribe(products -> {
      message.reply(new JsonArray(products));
    }, error -> message.fail(0, error.getMessage()));
  }

}
