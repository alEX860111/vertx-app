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
import io.vertx.ext.mongo.MongoClientDeleteResult;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

@RunWith(VertxUnitRunner.class)
public class DeleteProductHandlerIntegrationTest extends IntegrationTest {

  @Test
  public void testDeleteProduct(TestContext context) {
    final MongoClientDeleteResult result = Mockito.mock(MongoClientDeleteResult.class);
    when(result.getRemovedCount()).thenReturn(1L);

    doAnswer(invocation -> {
      @SuppressWarnings("unchecked")
      final Handler<AsyncResult<MongoClientDeleteResult>> handler = (Handler<AsyncResult<MongoClientDeleteResult>>) invocation.getArguments()[1];
      handler.handle(Future.succeededFuture(result));
      return null;
    }).when(serviceMock).deleteProduct(eq("1"), Matchers.<Handler<AsyncResult<MongoClientDeleteResult>>>any());

    final Async async = context.async();

    vertx.createHttpClient().delete(8080, "localhost", "/api/products/1", response -> {
      context.assertEquals(204, response.statusCode());
      async.complete();
    }).end();
  }

  @Test
  public void testDeleteProduct_notFound(TestContext context) {
    final MongoClientDeleteResult result = Mockito.mock(MongoClientDeleteResult.class);
    when(result.getRemovedCount()).thenReturn(0L);

    doAnswer(invocation -> {
      @SuppressWarnings("unchecked")
      final Handler<AsyncResult<MongoClientDeleteResult>> handler = (Handler<AsyncResult<MongoClientDeleteResult>>) invocation.getArguments()[1];
      handler.handle(Future.succeededFuture(result));
      return null;
    }).when(serviceMock).deleteProduct(eq("1"), Matchers.<Handler<AsyncResult<MongoClientDeleteResult>>>any());

    final Async async = context.async();

    vertx.createHttpClient().delete(8080, "localhost", "/api/products/1", response -> {
      context.assertEquals(404, response.statusCode());
      async.complete();
    }).end();
  }

  @Test
  public void testDeleteProduct_serverError(TestContext context) {
    doAnswer(invocation -> {
      @SuppressWarnings("unchecked")
      final Handler<AsyncResult<MongoClientDeleteResult>> handler = (Handler<AsyncResult<MongoClientDeleteResult>>) invocation.getArguments()[1];
      handler.handle(Future.failedFuture("error"));
      return null;
    }).when(serviceMock).deleteProduct(eq("1"), Matchers.<Handler<AsyncResult<MongoClientDeleteResult>>>any());

    final Async async = context.async();

    vertx.createHttpClient().delete(8080, "localhost", "/api/products/1", response -> {
      context.assertEquals(500, response.statusCode());
      async.complete();
    }).end();
  }

}