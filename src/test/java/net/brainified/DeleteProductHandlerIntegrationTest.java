package net.brainified;

import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;

import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import rx.Single;

@RunWith(VertxUnitRunner.class)
public class DeleteProductHandlerIntegrationTest extends IntegrationTest {

  @Test
  public void testDeleteProduct(TestContext context) {
    when(dao.delete("1")).thenReturn(Single.just(true));

    final Async async = context.async();

    vertx.createHttpClient().delete(HTTP_PORT, "localhost", "/products/1", response -> {
      context.assertEquals(204, response.statusCode());
      async.complete();
    }).end();
  }

  @Test
  public void testDeleteProduct_notFound(TestContext context) {
    when(dao.delete("1")).thenReturn(Single.just(false));

    final Async async = context.async();

    vertx.createHttpClient().delete(HTTP_PORT, "localhost", "/products/1", response -> {
      context.assertEquals(404, response.statusCode());
      async.complete();
    }).end();
  }

  @Test
  public void testDeleteProduct_serverError(TestContext context) {
    when(dao.delete("1")).thenReturn(Single.error(new RuntimeException("error")));

    final Async async = context.async();

    vertx.createHttpClient().delete(HTTP_PORT, "localhost", "/products/1", response -> {
      context.assertEquals(500, response.statusCode());
      async.complete();
    }).end();
  }

}