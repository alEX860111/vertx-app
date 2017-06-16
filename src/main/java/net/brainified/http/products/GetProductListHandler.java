package net.brainified.http.products;

import javax.inject.Inject;

import com.google.common.collect.Range;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.Json;
import io.vertx.rxjava.ext.web.RoutingContext;
import net.brainified.db.Dao;
import net.brainified.db.Product;
import net.brainified.db.SortOrder;
import net.brainified.http.HandlerConfiguration;
import net.brainified.http.RoutingContextHelper;

@HandlerConfiguration(path = "/products", method = HttpMethod.GET, requiresAuthentication = true)
final class GetProductListHandler implements Handler<RoutingContext> {

  private static final Integer PAGE_MIN = 1;
  private static final Integer PAGE_DEFAULT = 1;

  private static final Integer PER_PAGE_MIN = 1;
  private static final Integer PER_PAGE_DEFAULT = 10;

  private final RoutingContextHelper routingContextHelper;

  private final Dao<Product> dao;

  @Inject
  public GetProductListHandler(final RoutingContextHelper routingContextHelper, final Dao<Product> dao) {
    this.routingContextHelper = routingContextHelper;
    this.dao = dao;
  }

  @Override
  public void handle(RoutingContext routingContext) {
    final Integer page = routingContextHelper.getParamAsInteger(routingContext, "page", Range.atLeast(PAGE_MIN)).orElse(PAGE_DEFAULT);
    final Integer perpage = routingContextHelper.getParamAsInteger(routingContext, "perpage", Range.atLeast(PER_PAGE_MIN)).orElse(PER_PAGE_DEFAULT);
    final SortOrder sortOrder = routingContextHelper.getParamAsEnum(routingContext, "sortorder", SortOrder.class).orElse(SortOrder.DESC);
    final Product.SortKey sortKey = routingContextHelper.getParamAsEnum(routingContext, "sortkey", Product.SortKey.class).orElse(Product.SortKey.CREATEDAT);

    dao.getList(page, perpage, sortKey.getSortKey(), sortOrder).subscribe(itemContainer -> {
      final ProductContainer container = new ProductContainer();
      container.setProducts(itemContainer.getItems());
      container.setNumberOfProducts(itemContainer.getCount());
      routingContext.response()
        .putHeader("Content-Type", "application/json; charset=utf-8")
        .end(Json.encodePrettily(container));
    }, routingContext::fail);

  }

}
