package net.brainified.http;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.rxjava.core.eventbus.Message;
import rx.Observable;

@RunWith(VertxUnitRunner.class)
public class GetProductListHandlerIntegrationTest extends IntegrationTest {

  @Test
  public void testGetProducts(TestContext context) {
    @SuppressWarnings("unchecked")
    final Message<Long> productCountMessage = Mockito.mock(Message.class);
    when(productCountMessage.body()).thenReturn(42L);
    when(eventBusMock.<Long>sendObservable(eq("getProductCount"), any(JsonObject.class))).thenReturn(Observable.just(productCountMessage));

    @SuppressWarnings("unchecked")
    final Message<JsonArray> productListMessage = Mockito.mock(Message.class);
    when(productListMessage.body()).thenReturn(new JsonArray(Collections.emptyList()));
    when(eventBusMock.<JsonArray>sendObservable(eq("getProductList"), any(JsonObject.class))).thenReturn(Observable.just(productListMessage));

    final Async async = context.async();

    vertx.createHttpClient().getNow(8080, "localhost", "/api/products", response -> {
      context.assertEquals(200, response.statusCode());
      response.handler(body -> {
        final JsonObject json = body.toJsonObject();
        context.assertEquals(42L, json.getLong("numberOfProducts"));
        context.assertTrue(json.getJsonArray("products").isEmpty());
        async.complete();
      });
    });
  }

  @Test
  public void testGetProducts_countError(TestContext context) {
    when(eventBusMock.<Long>sendObservable(eq("getProductCount"), any(JsonObject.class))).thenReturn(Observable.error(new RuntimeException("error")));

    final Async async = context.async();

    vertx.createHttpClient().getNow(8080, "localhost", "/api/products", response -> {
      context.assertEquals(500, response.statusCode());
      verify(eventBusMock, never()).<JsonArray>sendObservable(eq("getProductList"), any(JsonObject.class));
      async.complete();
    });
  }

  @Test
  public void testGetProducts_getListError(TestContext context) {
    @SuppressWarnings("unchecked")
    final Message<Long> productCountMessage = Mockito.mock(Message.class);
    when(productCountMessage.body()).thenReturn(42L);
    when(eventBusMock.<Long>sendObservable(eq("getProductCount"), any(JsonObject.class))).thenReturn(Observable.just(productCountMessage));

    when(eventBusMock.<JsonArray>sendObservable(eq("getProductList"), any(JsonObject.class)))
        .thenReturn(Observable.error(new RuntimeException("error")));

    final Async async = context.async();

    vertx.createHttpClient().getNow(8080, "localhost", "/api/products", response -> {
      context.assertEquals(500, response.statusCode());
      async.complete();
    });
  }

}