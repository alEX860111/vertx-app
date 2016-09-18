package net.brainified;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

@RunWith(VertxUnitRunner.class)
public class GetProductListHandlerIntegrationTest extends IntegrationTest {

  @Test
  public void testGetProducts(TestContext context) {
    final ProductContainer container = new ProductContainer();
    container.setProducts(Collections.emptyList());
    when(serviceMock.getProductList(any(Integer.class), any(Integer.class))).thenReturn(Future.succeededFuture(container));

    final Async async = context.async();

    vertx.createHttpClient().getNow(8080, "localhost", "/api/products", response -> {
      context.assertEquals(200, response.statusCode());
      response.handler(body -> {
        final JsonObject json = body.toJsonObject();
        context.assertTrue(json.getJsonArray("products").isEmpty());
        async.complete();
      });
    });
  }

  @Test
  public void testGetProducts_serverError(TestContext context) {
    when(serviceMock.getProductList(any(Integer.class), any(Integer.class))).thenReturn(Future.failedFuture(""));

    final Async async = context.async();

    vertx.createHttpClient().getNow(8080, "localhost", "/api/products", response -> {
      context.assertEquals(500, response.statusCode());
      async.complete();
    });
  }

}