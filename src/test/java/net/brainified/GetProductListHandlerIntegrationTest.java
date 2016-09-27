package net.brainified;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import rx.Observable;

@RunWith(VertxUnitRunner.class)
public class GetProductListHandlerIntegrationTest extends IntegrationTest {

  @Test
  public void testGetProducts(TestContext context) {
    when(serviceMock.getProductCount()).thenReturn(Observable.just(42L));
    when(serviceMock.getProductList(any(Integer.class), any(Integer.class))).thenReturn(Observable.just(Collections.emptyList()));

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
    when(serviceMock.getProductCount()).thenReturn(Observable.error(new RuntimeException("error")));

    final Async async = context.async();

    vertx.createHttpClient().getNow(8080, "localhost", "/api/products", response -> {
      context.assertEquals(500, response.statusCode());
      verify(serviceMock, never()).getProductList(any(Integer.class), any(Integer.class));
      async.complete();
    });
  }

  @Test
  public void testGetProducts_getListError(TestContext context) {
    when(serviceMock.getProductCount()).thenReturn(Observable.just(42L));
    when(serviceMock.getProductList(any(Integer.class), any(Integer.class))).thenReturn(Observable.error(new RuntimeException("error")));

    final Async async = context.async();

    vertx.createHttpClient().getNow(8080, "localhost", "/api/products", response -> {
      context.assertEquals(500, response.statusCode());
      async.complete();
    });
  }

}