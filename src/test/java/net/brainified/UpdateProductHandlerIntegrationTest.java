package net.brainified;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import net.brainified.db.Product;
import rx.Observable;

@RunWith(VertxUnitRunner.class)
public class UpdateProductHandlerIntegrationTest extends IntegrationTest {

  @Test
  public void testUpdateProduct(TestContext context) {
    final String id = "1";

    final JsonObject data = new JsonObject();
    data.put("name", "myProduct");
    data.put("price", 100);

    when(dao.update(any(Product.class))).thenReturn(Observable.just(true));

    final Async async = context.async();

    vertx.createHttpClient().put(HTTP_PORT, "localhost", "/products/" + id, response -> {
      context.assertEquals(204, response.statusCode());
      async.complete();
    }).end(data.encode());
  }

  @Test
  public void testUpdateProduct_serverError(TestContext context) {
    final String id = "1";

    final JsonObject data = new JsonObject();
    data.put("name", "myProduct");
    data.put("price", 100);

    when(dao.update(any(Product.class))).thenReturn(Observable.error(new RuntimeException("error")));

    final Async async = context.async();

    vertx.createHttpClient().put(HTTP_PORT, "localhost", "/products/" + id, response -> {
      context.assertEquals(500, response.statusCode());
      async.complete();
    }).end(data.encode());
  }

  @Test
  public void testUpdateProduct_notFound(TestContext context) {
    final String id = "1";

    final JsonObject data = new JsonObject();
    data.put("name", "myProduct");
    data.put("price", 100);

    when(dao.update(any(Product.class))).thenReturn(Observable.just(false));

    final Async async = context.async();

    vertx.createHttpClient().put(HTTP_PORT, "localhost", "/products/" + id, response -> {
      context.assertEquals(404, response.statusCode());
      async.complete();
    }).end(data.encode());
  }

  @Test
  public void testUpdateProduct_sendInvalidBody(TestContext context) {
    final Async async = context.async();

    vertx.createHttpClient().put(HTTP_PORT, "localhost", "/products/1", response -> {
      context.assertEquals(400, response.statusCode());
      response.handler(body -> {
        context.assertTrue(body.toString().contains("Invalid JSON in body."));
        verifyZeroInteractions(dao);
        async.complete();
      });
    }).end();
  }

}