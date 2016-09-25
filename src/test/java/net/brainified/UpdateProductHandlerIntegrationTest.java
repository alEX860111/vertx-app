package net.brainified;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClientUpdateResult;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

@RunWith(VertxUnitRunner.class)
public class UpdateProductHandlerIntegrationTest extends IntegrationTest {

  @Test
  public void testUpdateProduct(TestContext context) {
    final String id = "1";

    final JsonObject json = new JsonObject();
    json.put("name", "myProduct");
    json.put("price", 100);

    final MongoClientUpdateResult result = Mockito.mock(MongoClientUpdateResult.class);
    when(result.getDocModified()).thenReturn(1L);

    doAnswer(invocation -> {
      @SuppressWarnings("unchecked")
      final Handler<AsyncResult<MongoClientUpdateResult>> handler = (Handler<AsyncResult<MongoClientUpdateResult>>) invocation.getArguments()[2];
      handler.handle(Future.succeededFuture(result));
      return null;
    }).when(serviceMock).updateProduct(eq(id), eq(json), Matchers.<Handler<AsyncResult<MongoClientUpdateResult>>>any());

    final Async async = context.async();

    vertx.createHttpClient().put(8080, "localhost", "/api/products/" + id, response -> {
      context.assertEquals(204, response.statusCode());
      async.complete();
    }).end(json.encode());
  }

  @Test
  public void testUpdateProduct_serverError(TestContext context) {
    final String id = "1";

    final JsonObject json = new JsonObject();
    json.put("name", "myProduct");
    json.put("price", 100);

    final MongoClientUpdateResult result = Mockito.mock(MongoClientUpdateResult.class);
    when(result.getDocModified()).thenReturn(1L);

    doAnswer(invocation -> {
      @SuppressWarnings("unchecked")
      final Handler<AsyncResult<MongoClientUpdateResult>> handler = (Handler<AsyncResult<MongoClientUpdateResult>>) invocation.getArguments()[2];
      handler.handle(Future.failedFuture("error"));
      return null;
    }).when(serviceMock).updateProduct(eq(id), eq(json), Matchers.<Handler<AsyncResult<MongoClientUpdateResult>>>any());

    final Async async = context.async();

    vertx.createHttpClient().put(8080, "localhost", "/api/products/" + id, response -> {
      context.assertEquals(500, response.statusCode());
      async.complete();
    }).end(json.encode());
  }

  @Test
  public void testUpdateProduct_notFound(TestContext context) {
    final String id = "1";

    final JsonObject json = new JsonObject();
    json.put("name", "myProduct");
    json.put("price", 100);

    final MongoClientUpdateResult result = Mockito.mock(MongoClientUpdateResult.class);
    when(result.getDocModified()).thenReturn(0L);

    doAnswer(invocation -> {
      @SuppressWarnings("unchecked")
      final Handler<AsyncResult<MongoClientUpdateResult>> handler = (Handler<AsyncResult<MongoClientUpdateResult>>) invocation.getArguments()[2];
      handler.handle(Future.succeededFuture(result));
      return null;
    }).when(serviceMock).updateProduct(eq(id), eq(json), Matchers.<Handler<AsyncResult<MongoClientUpdateResult>>>any());

    final Async async = context.async();

    vertx.createHttpClient().put(8080, "localhost", "/api/products/" + id, response -> {
      context.assertEquals(404, response.statusCode());
      async.complete();
    }).end(json.encode());
  }

  @Test
  public void testUpdateProduct_sendInvalidBody(TestContext context) {
    final Async async = context.async();

    vertx.createHttpClient().put(8080, "localhost", "/api/products/1", response -> {
      context.assertEquals(400, response.statusCode());
      response.handler(body -> {
        context.assertEquals("Invalid JSON in body", body.toString());
        async.complete();
      });
    }).end();
  }

}