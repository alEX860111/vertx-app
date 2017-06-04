package net.brainified;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import net.brainified.db.Product;
import rx.Observable;

@RunWith(VertxUnitRunner.class)
public class AddProductHandlerIntegrationTest extends IntegrationTest {

  @Test
  public void testAddProduct(TestContext context) {
    final JsonObject data = new JsonObject();
    data.put("name", "myProduct");
    data.put("price", 100);

    final String id = "id";
    final Product product = new Product();
    product.set_id(id);

    when(dao.add(any(Product.class))).thenReturn(Observable.just(product));

    final Async async = context.async();

    vertx.createHttpClient().post(HTTP_PORT, "localhost", "/api/products", response -> {
      context.assertEquals(201, response.statusCode());
      response.handler(body -> {
        final JsonObject resultProduct = new JsonObject(body.toString());

        context.assertEquals(id, resultProduct.getValue("_id"));
        context.assertEquals("http://localhost:" + HTTP_PORT + "/api/products/" + resultProduct.getValue("_id"), response.getHeader("Location"));
        async.complete();
      });
    }).end(data.encode());
  }

  @Test
  public void testAddProduct_sendInvalidBody(TestContext context) {
    final Async async = context.async();

    vertx.createHttpClient().post(HTTP_PORT, "localhost", "/api/products", response -> {
      context.assertEquals(400, response.statusCode());
      response.handler(body -> {
        context.assertTrue(body.toString().contains("Invalid JSON in body."));
        verifyZeroInteractions(dao);
        async.complete();
      });
    }).end();
  }

  @Test
  public void testAddProduct_serverError(TestContext context) {
    final JsonObject data = new JsonObject();
    data.put("name", "myProduct");
    data.put("price", 100);

    when(dao.add(any(Product.class))).thenReturn(Observable.error(new RuntimeException("error")));

    final Async async = context.async();

    vertx.createHttpClient().post(HTTP_PORT, "localhost", "/api/products", response -> {
      context.assertEquals(500, response.statusCode());
      async.complete();
    }).end(data.encode());
  }

}