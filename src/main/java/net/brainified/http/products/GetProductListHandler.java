package net.brainified.http.products;

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
import net.brainified.db.Dao;
import net.brainified.db.Product;
import net.brainified.db.SortOrder;
import net.brainified.http.HandlerConfiguration;
import net.brainified.http.RoutingContextHelper;

@HandlerConfiguration(path = "/api/products", method = HttpMethod.GET)

final class GetProductListHandler implements Handler<RoutingContext> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetProductListHandler.class);

  private static final String PAGE_DEFAULT = "1";
  private static final int PAGE_MIN = 1;

  private static final String PER_PAGE_DEFAULT = "10";
  private static final int PER_PAGE_MIN = 1;

  private RoutingContextHelper routingContextHelper;

  private final Dao<Product> dao;

  @Inject
  public GetProductListHandler(final RoutingContextHelper routingContextHelper, final Dao<Product> dao) {
    this.routingContextHelper = routingContextHelper;
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

    final SortOrder sortOrder = routingContextHelper.getParamAsEnum(routingContext, "sortorder", SortOrder::valueOf)
        .orElse(SortOrder.DESC);

    final Product.SortKey sortKey = routingContextHelper.getParamAsEnum(routingContext, "sortkey", Product.SortKey::valueOf)
        .orElse(Product.SortKey.CREATEDAT);

    dao.getCount().subscribe(count -> {
      dao.getList(page, perpage, sortKey.getSortKey(), sortOrder).subscribe(products -> {
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
