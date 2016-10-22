package net.brainified;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.rxjava.core.eventbus.Message;
import rx.Observable;

@RunWith(VertxUnitRunner.class)
public class AddProductHandlerIntegrationTest extends IntegrationTest {

  @Test
  public void testAddProduct(TestContext context) {
    final JsonObject data = new JsonObject();
    data.put("name", "myProduct");
    data.put("price", 100);

    final String id = "id";

    @SuppressWarnings("unchecked")
    final Message<String> message = Mockito.mock(Message.class);
    when(message.body()).thenReturn(id);
    when(eventBusMock.<String>sendObservable(eq("addProduct"), any(JsonObject.class))).thenReturn(Observable.just(message));

    final Async async = context.async();

    vertx.createHttpClient().post(8080, "localhost", "/api/products", response -> {
      context.assertEquals(201, response.statusCode());
      response.handler(body -> {
        final JsonObject resultProduct = new JsonObject(body.toString());
        context.assertEquals(id, resultProduct.getValue("_id"));
        context.assertFalse(resultProduct.getString("createdAt").isEmpty());
        context.assertEquals(data, resultProduct.getValue("data"));
        context.assertEquals("http://localhost:8080/api/products/" + resultProduct.getValue("_id"), response.getHeader("Location"));
        async.complete();
      });
    }).end(data.encode());
  }

  @Test
  public void testAddProduct_sendInvalidBody(TestContext context) {
    final Async async = context.async();

    vertx.createHttpClient().post(8080, "localhost", "/api/products", response -> {
      context.assertEquals(400, response.statusCode());
      response.handler(body -> {
        context.assertEquals("Invalid JSON in body", body.toString());
        verifyZeroInteractions(eventBusMock);
        async.complete();
      });
    }).end();
  }

  @Test
  public void testAddProduct_serverError(TestContext context) {
    final JsonObject data = new JsonObject();
    data.put("name", "myProduct");
    data.put("price", 100);

    when(eventBusMock.<String>sendObservable(eq("addProduct"), any(JsonObject.class))).thenReturn(Observable.error(new RuntimeException("error")));

    final Async async = context.async();

    vertx.createHttpClient().post(8080, "localhost", "/api/products", response -> {
      context.assertEquals(500, response.statusCode());
      async.complete();
    }).end(data.encode());
  }

}