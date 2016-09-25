package net.brainified;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;

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
public class GetProductHandlerIntegrationTest extends IntegrationTest {

  @Test
  public void testGetProduct(TestContext context) {
    final JsonObject product = new JsonObject();
    product.put("_id", "1");
    final JsonObject data = new JsonObject();
    data.put("name", "name");
    data.put("price", 100);
    product.put("data", data);

    doAnswer(invocation -> {
      @SuppressWarnings("unchecked")
      final Handler<AsyncResult<JsonObject>> handler = (Handler<AsyncResult<JsonObject>>) invocation.getArguments()[1];
      handler.handle(Future.succeededFuture(product));
      return null;
    }).when(serviceMock).getProduct(eq("1"), Matchers.<Handler<AsyncResult<JsonObject>>>any());

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
    doAnswer(invocation -> {
      @SuppressWarnings("unchecked")
      final Handler<AsyncResult<JsonObject>> handler = (Handler<AsyncResult<JsonObject>>) invocation.getArguments()[1];
      handler.handle(Future.succeededFuture(null));
      return null;
    }).when(serviceMock).getProduct(eq("1"), Matchers.<Handler<AsyncResult<JsonObject>>>any());
    final Async async = context.async();

    vertx.createHttpClient().getNow(8080, "localhost", "/api/products/1", response -> {
      context.assertEquals(404, response.statusCode());
      async.complete();
    });
  }

  @Test
  public void testGetProduct_serverError(TestContext context) {
    doAnswer(invocation -> {
      @SuppressWarnings("unchecked")
      final Handler<AsyncResult<JsonObject>> handler = (Handler<AsyncResult<JsonObject>>) invocation.getArguments()[1];
      handler.handle(Future.failedFuture("error"));
      return null;
    }).when(serviceMock).getProduct(eq("1"), Matchers.<Handler<AsyncResult<JsonObject>>>any());
    final Async async = context.async();

    vertx.createHttpClient().getNow(8080, "localhost", "/api/products/1", response -> {
      context.assertEquals(500, response.statusCode());
      async.complete();
    });
  }

}