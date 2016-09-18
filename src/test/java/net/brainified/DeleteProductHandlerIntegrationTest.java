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
public class DeleteProductHandlerIntegrationTest extends IntegrationTest {

  @Test
  public void testDeleteProduct(TestContext context) {
    final Product product = new Product();
    product.setId(1);
    final ProductData data = new ProductData();
    data.setName("name");
    data.setPrice(100);
    product.setData(data);
    when(serviceMock.deleteProduct(product.getId())).thenReturn(Future.succeededFuture(product));

    final Async async = context.async();

    vertx.createHttpClient().delete(8080, "localhost", "/api/products/" + product.getId(), response -> {
      context.assertEquals(200, response.statusCode());
      response.handler(body -> {
        final Product resultProduct = Json.decodeValue(body.toString(), Product.class);
        context.assertEquals(product.getId(), resultProduct.getId());
        context.assertEquals(product.getData().getName(), resultProduct.getData().getName());
        context.assertEquals(product.getData().getPrice(), resultProduct.getData().getPrice());
        async.complete();
      });
    }).end();
  }

  @Test
  public void testDeleteProduct_sendInvalidProductId(TestContext context) {
    final Async async = context.async();

    vertx.createHttpClient().delete(8080, "localhost", "/api/products/x", response -> {
      context.assertEquals(400, response.statusCode());
      response.handler(body -> {
        context.assertEquals("Invalid id", body.toString());
        async.complete();
      });
    }).end();
  }

  @Test
  public void testDeleteProduct_notFound(TestContext context) {
    final int id = 1;

    when(serviceMock.deleteProduct(id)).thenReturn(Future.failedFuture(""));

    final Async async = context.async();

    vertx.createHttpClient().delete(8080, "localhost", "/api/products/" + id, response -> {
      context.assertEquals(404, response.statusCode());
      async.complete();
    }).end();
  }
}