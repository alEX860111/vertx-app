package net.brainified.http;

import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;

import io.vertx.core.json.Json;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import net.brainified.db.Product;
import rx.Observable;

@RunWith(VertxUnitRunner.class)
public class GetProductHandlerIntegrationTest extends IntegrationTest {

  @Test
  public void testGetProduct(TestContext context) {
    final Product product = new Product();
    product.set_id("1");
    product.setName("name");
    product.setPrice(100d);

    when(dao.getProduct("1")).thenReturn(Observable.just(Optional.of(product)));

    final Async async = context.async();

    vertx.createHttpClient().getNow(8080, "localhost", "/api/products/1", response -> {
      context.assertEquals(200, response.statusCode());
      response.handler(body -> {
        context.assertEquals(Json.encodePrettily(product), body.toString());
        async.complete();
      });
    });
  }

  @Test
  public void testGetProduct_notFound(TestContext context) {
    when(dao.getProduct("1")).thenReturn(Observable.just(Optional.empty()));

    final Async async = context.async();

    vertx.createHttpClient().getNow(8080, "localhost", "/api/products/1", response -> {
      context.assertEquals(404, response.statusCode());
      async.complete();
    });
  }

  @Test
  public void testGetProduct_serverError(TestContext context) {
    when(dao.getProduct("1")).thenReturn(Observable.error(new RuntimeException("error")));
    final Async async = context.async();

    vertx.createHttpClient().getNow(8080, "localhost", "/api/products/1", response -> {
      context.assertEquals(500, response.statusCode());
      async.complete();
    });
  }

}