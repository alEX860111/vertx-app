package net.brainified;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

@RunWith(VertxUnitRunner.class)
public class HttpServerVerticleTest {

  private Vertx vertx;

  private ProductService serviceMock;

  @Before
  public void setUp(TestContext context) {
    serviceMock = Mockito.mock(ProductService.class);

    vertx = Vertx.vertx();
    final HttpServerVerticle verticle = new HttpServerVerticle(vertx, new ProductHandlerImpl(serviceMock));
    vertx.deployVerticle(verticle, context.asyncAssertSuccess());
  }

  @After
  public void tearDown(TestContext context) {
    vertx.close(context.asyncAssertSuccess());
  }

  @Test
  public void testGetProducts_200(TestContext context) {
    when(serviceMock.getProducts()).thenReturn(Future.succeededFuture(Collections.emptyList()));

    final Async async = context.async();

    vertx.createHttpClient().getNow(8080, "localhost", "/api/products", response -> {
      context.assertEquals(200, response.statusCode());
      response.handler(body -> {
        context.assertTrue(body.toJsonArray().getList().isEmpty());
        verify(serviceMock).getProducts();
        async.complete();
      });
    });
  }

  @Test
  public void testGetProducts_500(TestContext context) {
    when(serviceMock.getProducts()).thenReturn(Future.failedFuture(""));

    final Async async = context.async();

    vertx.createHttpClient().getNow(8080, "localhost", "/api/products", response -> {
      context.assertEquals(500, response.statusCode());
      verify(serviceMock).getProducts();
      async.complete();
    });
  }

  @Test
  public void testGetProduct_200(TestContext context) {
    final Product product = new Product();
    product.setId(1);
    product.setName("name");
    product.setPrice(100);
    when(serviceMock.getProduct(1)).thenReturn(Future.succeededFuture(product));

    final Async async = context.async();

    vertx.createHttpClient().getNow(8080, "localhost", "/api/products/1", response -> {
      context.assertEquals(200, response.statusCode());
      response.handler(body -> {
        final Product resultProduct = Json.decodeValue(body.toString(), Product.class);
        context.assertEquals(1, resultProduct.getId());
        context.assertEquals("name", resultProduct.getName());
        context.assertEquals(100, resultProduct.getPrice());
        verify(serviceMock).getProduct(1);
        async.complete();
      });
    });
  }

  @Test
  public void testGetProduct_400(TestContext context) {
    final Async async = context.async();

    vertx.createHttpClient().getNow(8080, "localhost", "/api/products/x", response -> {
      context.assertEquals(400, response.statusCode());
      verifyZeroInteractions(serviceMock);
      async.complete();
    });
  }

  @Test
  public void testGetProduct_404(TestContext context) {
    when(serviceMock.getProduct(1)).thenReturn(Future.failedFuture("not found"));

    final Async async = context.async();

    vertx.createHttpClient().getNow(8080, "localhost", "/api/products/1", response -> {
      context.assertEquals(404, response.statusCode());
      verify(serviceMock).getProduct(1);
      async.complete();
    });
  }
}