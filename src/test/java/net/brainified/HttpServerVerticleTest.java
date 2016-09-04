package net.brainified;

import static org.mockito.Mockito.when;

import java.util.Collections;

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
public class HttpServerVerticleTest {

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
  public void testGetProducts(TestContext context) {
    final ProductContainer container = new ProductContainer();
    container.setProducts(Collections.emptyList());
    when(serviceMock.getProducts()).thenReturn(Future.succeededFuture(container));

    final Async async = context.async();

    vertx.createHttpClient().getNow(8080, "localhost", "/api/products", response -> {
      context.assertEquals(200, response.statusCode());
      response.handler(body -> {
        final JsonObject json = body.toJsonObject();
        context.assertTrue(json.getJsonArray("products").isEmpty());
        async.complete();
      });
    });
  }

  @Test
  public void testGetProducts_serverError(TestContext context) {
    when(serviceMock.getProducts()).thenReturn(Future.failedFuture(""));

    final Async async = context.async();

    vertx.createHttpClient().getNow(8080, "localhost", "/api/products", response -> {
      context.assertEquals(500, response.statusCode());
      async.complete();
    });
  }

  @Test
  public void testGetProduct(TestContext context) {
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
        context.assertEquals("Invalid product id", body.toString());
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

  @Test
  public void testAddProduct(TestContext context) {
    final JsonObject json = new JsonObject();
    json.put("name", "myProduct");
    json.put("price", 100);

    final Product product = new Product();
    product.setId(1);
    product.setName(json.getString("name"));
    product.setPrice(json.getInteger("price"));
    when(serviceMock.addProduct(json.getString("name"), json.getInteger("price"))).thenReturn(Future.succeededFuture(product));

    final Async async = context.async();

    vertx.createHttpClient().post(8080, "localhost", "/api/products", response -> {
      context.assertEquals(201, response.statusCode());
      response.handler(body -> {
        final Product resultProduct = Json.decodeValue(body.toString(), Product.class);
        context.assertEquals(product.getId(), resultProduct.getId());
        context.assertEquals(json.getString("name"), resultProduct.getName());
        context.assertEquals(json.getInteger("price"), resultProduct.getPrice());
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

    when(serviceMock.addProduct(json.getString("name"), json.getInteger("price"))).thenReturn(Future.failedFuture("failed"));

    final Async async = context.async();

    vertx.createHttpClient().post(8080, "localhost", "/api/products", response -> {
      context.assertEquals(500, response.statusCode());
      async.complete();
    }).end(json.encode());
  }

  @Test
  public void testUpdateProduct(TestContext context) {
    final int id = 1;

    final JsonObject json = new JsonObject();
    json.put("name", "myProduct");
    json.put("price", 100);

    final Product product = new Product();
    product.setId(id);
    product.setName(json.getString("name"));
    product.setPrice(json.getInteger("price"));
    when(serviceMock.updateProduct(id, json.getString("name"), json.getInteger("price"))).thenReturn(Future.succeededFuture(product));

    final Async async = context.async();

    vertx.createHttpClient().put(8080, "localhost", "/api/products/" + id, response -> {
      context.assertEquals(200, response.statusCode());
      response.handler(body -> {
        final Product resultProduct = Json.decodeValue(body.toString(), Product.class);
        context.assertEquals(product.getId(), resultProduct.getId());
        context.assertEquals(json.getString("name"), resultProduct.getName());
        context.assertEquals(json.getInteger("price"), resultProduct.getPrice());
        async.complete();
      });
    }).end(json.encode());
  }

  @Test
  public void testUpdateProduct_sendInvalidProductId(TestContext context) {
    final JsonObject json = new JsonObject();
    json.put("name", "myProduct");
    json.put("price", 100);

    final Async async = context.async();

    vertx.createHttpClient().put(8080, "localhost", "/api/products/x", response -> {
      context.assertEquals(400, response.statusCode());
      response.handler(body -> {
        context.assertEquals("Invalid product id", body.toString());
        async.complete();
      });
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

  @Test
  public void testUpdateProduct_notFound(TestContext context) {
    final int id = 1;

    final JsonObject json = new JsonObject();
    json.put("name", "myProduct");
    json.put("price", 100);

    when(serviceMock.updateProduct(id, json.getString("name"), json.getInteger("price"))).thenReturn(Future.failedFuture("not found"));

    final Async async = context.async();

    vertx.createHttpClient().put(8080, "localhost", "/api/products/" + id, response -> {
      context.assertEquals(404, response.statusCode());
      async.complete();
    }).end(json.encode());
  }

  @Test
  public void testDeleteProduct(TestContext context) {
    final Product product = new Product();
    product.setId(1);
    product.setName("name");
    product.setPrice(100);
    when(serviceMock.deleteProduct(product.getId())).thenReturn(Future.succeededFuture(product));

    final Async async = context.async();

    vertx.createHttpClient().delete(8080, "localhost", "/api/products/" + product.getId(), response -> {
      context.assertEquals(200, response.statusCode());
      response.handler(body -> {
        final Product resultProduct = Json.decodeValue(body.toString(), Product.class);
        context.assertEquals(product.getId(), resultProduct.getId());
        context.assertEquals(product.getName(), resultProduct.getName());
        context.assertEquals(product.getPrice(), resultProduct.getPrice());
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
        context.assertEquals("Invalid product id", body.toString());
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