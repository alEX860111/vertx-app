package net.brainified.http.products;

import java.util.Objects;

import javax.inject.Inject;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.google.common.primitives.Ints;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.Json;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.ext.web.RoutingContext;
import net.brainified.db.Dao;
import net.brainified.db.Product;
import net.brainified.db.SortOrder;
import net.brainified.http.HandlerConfiguration;

@HandlerConfiguration(path = "/api/products", method = HttpMethod.GET)

final class GetProductListHandler implements Handler<RoutingContext> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetProductListHandler.class);

  private static final String PAGE_DEFAULT = "1";
  private static final int PAGE_MIN = 1;

  private static final String PER_PAGE_DEFAULT = "10";
  private static final int PER_PAGE_MIN = 1;

  private final Dao<Product> dao;

  @Inject
  public GetProductListHandler(final Dao<Product> dao) {
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

    final String sortOrderParam = MoreObjects.firstNonNull(routingContext.request().getParam("sortorder"), "DESC");
    final SortOrder sortOrder = SortOrder.fromString(sortOrderParam);
    if (Objects.isNull(sortOrder)) {
      routingContext.response().setStatusCode(400).end("sortorder must be 'asc' or 'desc'");
      return;
    }

    final String sortKey = MoreObjects.firstNonNull(routingContext.request().getParam("sortkey"), "createdAt");
    if (Strings.isNullOrEmpty(sortKey)) {
      routingContext.response().setStatusCode(400).end("sortkey may not be empty");
      return;
    }

    dao.getCount().subscribe(count -> {
      dao.getList(page, perpage, sortKey, sortOrder).subscribe(products -> {
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
