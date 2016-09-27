package net.brainified;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Optional;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import rx.Observable;

@RunWith(VertxUnitRunner.class)
public class GetProductHandlerIntegrationTest extends IntegrationTest {

  @Test
  public void testGetProduct(TestContext context) {
    final JsonObject product = new JsonObject();
    product.put("_id", "1");
    final JsonObject data = new JsonObject();
    data.put("name", "name");
    data.put("price", 100);
    product.put("data", data);

    when(serviceMock.getProduct(eq("1"))).thenReturn(Observable.just(Optional.of(product)));

    final Async async = context.async();

    vertx.createHttpClient().getNow(8080, "localhost", "/api/products/1", response -> {
      context.assertEquals(200, response.statusCode());
      response.handler(body -> {
        final JsonObject resultProduct = new JsonObject(body.toString());
        context.assertEquals(product, resultProduct);
        async.complete();
      });
    });
  }

  @Test
  public void testGetProduct_notFound(TestContext context) {
    when(serviceMock.getProduct(eq("1"))).thenReturn(Observable.just(Optional.empty()));
    final Async async = context.async();

    vertx.createHttpClient().getNow(8080, "localhost", "/api/products/1", response -> {
      context.assertEquals(404, response.statusCode());
      async.complete();
    });
  }

  @Test
  public void testGetProduct_serverError(TestContext context) {
    when(serviceMock.getProduct(eq("1"))).thenReturn(Observable.error(new RuntimeException("error")));
    final Async async = context.async();

    vertx.createHttpClient().getNow(8080, "localhost", "/api/products/1", response -> {
      context.assertEquals(500, response.statusCode());
      async.complete();
    });
  }

}