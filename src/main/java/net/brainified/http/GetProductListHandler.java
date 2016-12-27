package net.brainified.http;

import java.util.Objects;

import javax.inject.Inject;

import com.google.common.base.MoreObjects;
import com.google.common.primitives.Ints;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.Json;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.ext.web.RoutingContext;
import net.brainified.db.ProductContainer;
import net.brainified.db.ProductDao;

@HandlerConfiguration(path = "/api/products", method = HttpMethod.GET)

final class GetProductListHandler implements Handler<RoutingContext> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetProductListHandler.class);

  private static final String PAGE_DEFAULT = "1";
  private static final int PAGE_MIN = 1;

  private static final String PER_PAGE_DEFAULT = "10";
  private static final int PER_PAGE_MIN = 1;

  private final ProductDao dao;

  @Inject
  public GetProductListHandler(final ProductDao dao) {
    this.dao = dao;
  }

  @Override
  public void handle(RoutingContext routingContext) {
    final Integer page = Ints.tryParse(MoreObjects.firstNonNull(routingContext.request().getParam("page"), PAGE_DEFAULT));
    if (Objects.isNull(page) || page < PAGE_MIN) {
      routingContext.response().setStatusCode(400).end("page must be greater than or equal to " + PAGE_MIN);
      return;
    }

    final Integer perpage = Ints.tryParse(MoreObjects.firstNonNull(routingContext.request().getParam("perpage"), PER_PAGE_DEFAULT));
    if (Objects.isNull(perpage) || perpage < PER_PAGE_MIN) {
      routingContext.response().setStatusCode(400).end("perpage must be greater than or equal to " + PER_PAGE_MIN);
      return;
    }

    dao.getProductCount().subscribe(count -> {
      dao.getProductList(page, perpage).subscribe(products -> {
        final ProductContainer container = new ProductContainer();
        container.setProducts(products);
        container.setNumberOfProducts(count);
        routingContext.response()
          .putHeader("Content-Type", "application/json; charset=utf-8")
          .end(Json.encodePrettily(container));
      }, error -> {
        LOGGER.error(error.getMessage());
        routingContext.response().setStatusCode(500).end();
      });
    }, error -> {
      LOGGER.error(error.getMessage());
      routingContext.response().setStatusCode(500).end();
    });

  }

}
