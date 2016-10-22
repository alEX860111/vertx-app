package net.brainified;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
public class DeleteProductHandlerIntegrationTest extends IntegrationTest {

  @Test
  public void testDeleteProduct(TestContext context) {
    @SuppressWarnings("unchecked")
    final Message<Long> message = Mockito.mock(Message.class);
    when(message.body()).thenReturn(1L);
    when(eventBusMock.<Long>sendObservable(eq("deleteProduct"), any(JsonObject.class))).thenReturn(Observable.just(message));

    final Async async = context.async();

    vertx.createHttpClient().delete(8080, "localhost", "/api/products/1", response -> {
      context.assertEquals(204, response.statusCode());
      async.complete();
    }).end();
  }

  @Test
  public void testDeleteProduct_notFound(TestContext context) {
    @SuppressWarnings("unchecked")
    final Message<Long> message = Mockito.mock(Message.class);
    when(message.body()).thenReturn(0L);
    when(eventBusMock.<Long>sendObservable(eq("deleteProduct"), any(JsonObject.class))).thenReturn(Observable.just(message));

    final Async async = context.async();

    vertx.createHttpClient().delete(8080, "localhost", "/api/products/1", response -> {
      context.assertEquals(404, response.statusCode());
      async.complete();
    }).end();
  }

  @Test
  public void testDeleteProduct_serverError(TestContext context) {
    when(eventBusMock.<Long>sendObservable(eq("deleteProduct"), any(JsonObject.class))).thenReturn(Observable.error(new RuntimeException("error")));

    final Async async = context.async();

    vertx.createHttpClient().delete(8080, "localhost", "/api/products/1", response -> {
      context.assertEquals(500, response.statusCode());
      async.complete();
    }).end();
  }

}