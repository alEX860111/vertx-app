package net.brainified;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import net.brainified.db.ItemContainer;
import net.brainified.db.Product;
import net.brainified.db.SortOrder;
import rx.Observable;

@RunWith(VertxUnitRunner.class)
public class GetProductListHandlerIntegrationTest extends IntegrationTest {

  @Test
  public void testGetProducts(TestContext context) {
    final ItemContainer<Product> container = new ItemContainer<>(42L, Collections.emptyList());
    when(dao.getList(anyInt(), anyInt(), anyString(), any(SortOrder.class))).thenReturn(Observable.just(container));

    final Async async = context.async();

    vertx.createHttpClient().getNow(HTTP_PORT, "localhost", "/products", response -> {
      context.assertEquals(200, response.statusCode());
      response.handler(body -> {
        final JsonObject json = body.toJsonObject();
        context.assertEquals(42L, json.getLong("count"));
        context.assertTrue(json.getJsonArray("items").isEmpty());
        async.complete();
      });
    });
  }

  @Test
  public void testGetProducts_getListError(TestContext context) {
    when(dao.getList(anyInt(), anyInt(), anyString(), any(SortOrder.class))).thenReturn(Observable.error(new RuntimeException("error")));

    final Async async = context.async();

    vertx.createHttpClient().getNow(HTTP_PORT, "localhost", "/products", response -> {
      context.assertEquals(500, response.statusCode());
      async.complete();
    });
  }

}