package net.brainified;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

@RunWith(VertxUnitRunner.class)
public class GetProductListHandlerIntegrationTest extends IntegrationTest {

  @Test
  public void testGetProducts(TestContext context) {
    doAnswer(invocation -> {
      @SuppressWarnings("unchecked")
      final Handler<AsyncResult<Long>> handler = (Handler<AsyncResult<Long>>) invocation.getArguments()[0];
      handler.handle(Future.succeededFuture(42L));
      return null;
    }).when(serviceMock).getProductCount(Matchers.<Handler<AsyncResult<Long>>>any());

    doAnswer(invocation -> {
      @SuppressWarnings("unchecked")
      final Handler<AsyncResult<List<JsonObject>>> handler = (Handler<AsyncResult<List<JsonObject>>>) invocation.getArguments()[2];
      handler.handle(Future.succeededFuture(Collections.emptyList()));
      return null;
    }).when(serviceMock).getProductList(any(Integer.class), any(Integer.class), Matchers.<Handler<AsyncResult<List<JsonObject>>>>any());

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
    doAnswer(invocation -> {
      @SuppressWarnings("unchecked")
      final Handler<AsyncResult<Long>> handler = (Handler<AsyncResult<Long>>) invocation.getArguments()[0];
      handler.handle(Future.failedFuture("error"));
      return null;
    }).when(serviceMock).getProductCount(Matchers.<Handler<AsyncResult<Long>>>any());

    final Async async = context.async();

    vertx.createHttpClient().getNow(8080, "localhost", "/api/products", response -> {
      context.assertEquals(500, response.statusCode());
      verify(serviceMock, never()).getProductList(any(Integer.class), any(Integer.class), Matchers.<Handler<AsyncResult<List<JsonObject>>>>any());
      async.complete();
    });
  }

  @Test
  public void testGetProducts_getListError(TestContext context) {
    doAnswer(invocation -> {
      @SuppressWarnings("unchecked")
      final Handler<AsyncResult<Long>> handler = (Handler<AsyncResult<Long>>) invocation.getArguments()[0];
      handler.handle(Future.succeededFuture(42L));
      return null;
    }).when(serviceMock).getProductCount(Matchers.<Handler<AsyncResult<Long>>>any());

    doAnswer(invocation -> {
      @SuppressWarnings("unchecked")
      final Handler<AsyncResult<List<JsonObject>>> handler = (Handler<AsyncResult<List<JsonObject>>>) invocation.getArguments()[2];
      handler.handle(Future.failedFuture("error"));
      return null;
    }).when(serviceMock).getProductList(any(Integer.class), any(Integer.class), Matchers.<Handler<AsyncResult<List<JsonObject>>>>any());

    final Async async = context.async();

    vertx.createHttpClient().getNow(8080, "localhost", "/api/products", response -> {
      context.assertEquals(500, response.statusCode());
      async.complete();
    });
  }

}