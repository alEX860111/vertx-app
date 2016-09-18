package net.brainified;

import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;

import io.vertx.core.Future;
import io.vertx.core.json.Json;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

@RunWith(VertxUnitRunner.class)
public class GetProductHandlerIntegrationTest extends IntegrationTest {

  @Test
  public void testGetProduct(TestContext context) {
    final Product product = new Product();
    product.setId(1);
    final ProductData data = new ProductData();
    data.setName("name");
    data.setPrice(100);
    product.setData(data);
    when(serviceMock.getProduct(1)).thenReturn(Future.succeededFuture(product));

    final Async async = context.async();

    vertx.createHttpClient().getNow(8080, "localhost", "/api/products/1", response -> {
      context.assertEquals(200, response.statusCode());
      response.handler(body -> {
        final Product resultProduct = Json.decodeValue(body.toString(), Product.class);
        context.assertEquals(1, resultProduct.getId());
        context.assertEquals("name", resultProduct.getData().getName());
        context.assertEquals(100, resultProduct.getData().getPrice());
        async.complete();
      });
    });
  }

  @Test
  public void testGetProduct_sendInvalidProductId(TestContext context) {
    final Async async = context.async();

    vertx.createHttpClient().getNow(8080, "localhost", "/api/products/x", response -> {
      context.assertEquals(400, response.statusCode());
      response.handler(body -> {
        context.assertEquals("Invalid id", body.toString());
        async.complete();
      });
    });
  }

  @Test
  public void testGetProduct_notFound(TestContext context) {
    when(serviceMock.getProduct(1)).thenReturn(Future.failedFuture("not found"));

    final Async async = context.async();

    vertx.createHttpClient().getNow(8080, "localhost", "/api/products/1", response -> {
      context.assertEquals(404, response.statusCode());
      async.complete();
    });
  }

}