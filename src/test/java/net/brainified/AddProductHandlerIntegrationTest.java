package net.brainified;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.util.Modules;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

@RunWith(VertxUnitRunner.class)
public class AddProductHandlerIntegrationTest {

  private Vertx vertx;

  private ProductService serviceMock;

  @Before
  public void setUp(TestContext context) {
    serviceMock = Mockito.mock(ProductService.class);
    final Injector injector = Guice.createInjector(Modules.override(new ApplicationModule()).with(new AbstractModule() {
      @Override
      protected void configure() {
        bind(ProductService.class).toInstance(serviceMock);
      }
    }));
    vertx = injector.getInstance(Vertx.class);
    vertx.deployVerticle(injector.getInstance(HttpServerVerticle.class), context.asyncAssertSuccess());
  }

  @After
  public void tearDown(TestContext context) {
    vertx.close(context.asyncAssertSuccess());
  }

  @Test
  public void testAddProduct(TestContext context) {
    final JsonObject json = new JsonObject();
    json.put("name", "myProduct");
    json.put("price", 100);

    final Product product = new Product();
    product.setId(1);
    final ProductData data = new ProductData();
    data.setName("myProduct");
    data.setPrice(100);
    product.setData(data);
    when(serviceMock.addProduct(any(ProductData.class))).thenReturn(Future.succeededFuture(product));

    final Async async = context.async();

    vertx.createHttpClient().post(8080, "localhost", "/api/products", response -> {
      context.assertEquals(201, response.statusCode());
      response.handler(body -> {
        final Product resultProduct = Json.decodeValue(body.toString(), Product.class);
        context.assertEquals(product.getId(), resultProduct.getId());
        context.assertEquals(json.getString("name"), resultProduct.getData().getName());
        context.assertEquals(json.getInteger("price"), resultProduct.getData().getPrice());
        async.complete();
      });
    }).end(json.encode());
  }

  @Test
  public void testAddProduct_sendInvalidBody(TestContext context) {
    final Async async = context.async();

    vertx.createHttpClient().post(8080, "localhost", "/api/products", response -> {
      context.assertEquals(400, response.statusCode());
      response.handler(body -> {
        context.assertEquals("Invalid JSON in body", body.toString());
        async.complete();
      });
    }).end();
  }

  @Test
  public void testAddProduct_serverError(TestContext context) {
    final JsonObject json = new JsonObject();
    json.put("name", "myProduct");
    json.put("price", 100);

    when(serviceMock.addProduct(any(ProductData.class))).thenReturn(Future.failedFuture("failed"));

    final Async async = context.async();

    vertx.createHttpClient().post(8080, "localhost", "/api/products", response -> {
      context.assertEquals(500, response.statusCode());
      async.complete();
    }).end(json.encode());
  }

}