package net.brainified;

import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import rx.Observable;

@RunWith(VertxUnitRunner.class)
public class UpdateProductHandlerIntegrationTest extends IntegrationTest {

  @Test
  public void testUpdateProduct(TestContext context) {
    final String id = "1";

    final JsonObject data = new JsonObject();
    data.put("name", "myProduct");
    data.put("price", 100);

    when(serviceMock.updateProduct(id, data)).thenReturn(Observable.just(1L));

    final Async async = context.async();

    vertx.createHttpClient().put(8080, "localhost", "/api/products/" + id, response -> {
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

    when(serviceMock.updateProduct(id, data)).thenReturn(Observable.error(new RuntimeException("error")));

    final Async async = context.async();

    vertx.createHttpClient().put(8080, "localhost", "/api/products/" + id, response -> {
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

    when(serviceMock.updateProduct(id, data)).thenReturn(Observable.just(0L));

    final Async async = context.async();

    vertx.createHttpClient().put(8080, "localhost", "/api/products/" + id, response -> {
      context.assertEquals(404, response.statusCode());
      async.complete();
    }).end(data.encode());
  }

  @Test
  public void testUpdateProduct_sendInvalidBody(TestContext context) {
    final Async async = context.async();

    vertx.createHttpClient().put(8080, "localhost", "/api/products/1", response -> {
      context.assertEquals(400, response.statusCode());
      response.handler(body -> {
        context.assertEquals("Invalid JSON in body", body.toString());
        verifyZeroInteractions(serviceMock);
        async.complete();
      });
    }).end();
  }

}