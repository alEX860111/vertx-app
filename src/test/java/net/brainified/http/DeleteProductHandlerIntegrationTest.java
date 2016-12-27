package net.brainified.http;

import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;

import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import rx.Observable;

@RunWith(VertxUnitRunner.class)
public class DeleteProductHandlerIntegrationTest extends IntegrationTest {

  @Test
  public void testDeleteProduct(TestContext context) {
    when(dao.deleteProduct("1")).thenReturn(Observable.just(1L));

    final Async async = context.async();

    vertx.createHttpClient().delete(8080, "localhost", "/api/products/1", response -> {
      context.assertEquals(204, response.statusCode());
      async.complete();
    }).end();
  }

  @Test
  public void testDeleteProduct_notFound(TestContext context) {
    when(dao.deleteProduct("1")).thenReturn(Observable.just(0L));

    final Async async = context.async();

    vertx.createHttpClient().delete(8080, "localhost", "/api/products/1", response -> {
      context.assertEquals(404, response.statusCode());
      async.complete();
    }).end();
  }

  @Test
  public void testDeleteProduct_serverError(TestContext context) {
    when(dao.deleteProduct("1")).thenReturn(Observable.error(new RuntimeException("error")));

    final Async async = context.async();

    vertx.createHttpClient().delete(8080, "localhost", "/api/products/1", response -> {
      context.assertEquals(500, response.statusCode());
      async.complete();
    }).end();
  }

}