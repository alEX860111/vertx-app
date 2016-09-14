package net.brainified;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.vertx.core.Vertx;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

@RunWith(VertxUnitRunner.class)
public class ProductServiceImplTest {

  private ProductServiceImpl service;

  private Vertx vertx;

  @Before
  public void setUp() {
    vertx = Vertx.vertx();
    service = new ProductServiceImpl(vertx);
  }

  @After
  public void tearDown(TestContext context) {
    vertx.close(context.asyncAssertSuccess());
  }

  @Test
  public void testGetProductList(final TestContext context) {
    final Async async = context.async();
    service.getProductList(1, 10).setHandler(productsResult -> {
      assertTrue(productsResult.succeeded());
      final List<Product> products = productsResult.result().getProducts();
      assertFalse(products.isEmpty());
      async.complete();
    });
  }

  @Test
  public void testGetProduct(final TestContext context) {
    final Async async = context.async();
    service.getProductList(1, 10).setHandler(productsResult -> {
      final List<Product> products = productsResult.result().getProducts();
      service.getProduct(products.get(0).getId()).setHandler(productResult -> {
        assertTrue(productResult.succeeded());
        assertNotNull(productResult.result());
        async.complete();
      });
    });
  }

  @Test
  public void testGetProduct_notFound(final TestContext context) {
    final Async async = context.async();
    service.getProduct(42).setHandler(productResult -> {
      assertTrue(productResult.failed());
      assertNull(productResult.result());
      async.complete();
    });
  }

  @Test
  public void testAddProduct(final TestContext context) {
    final Async async = context.async();
    final ProductData data = new ProductData();
    data.setName("myNewProduct");
    data.setPrice(900);
    service.addProduct(data).setHandler(productResult -> {
      assertTrue(productResult.succeeded());
      final Product product = productResult.result();
      assertNotNull(product);
      assertTrue(product.getId() > 0);
      assertEquals("myNewProduct", product.getData().getName());
      assertEquals(Integer.valueOf(900), product.getData().getPrice());

      async.complete();
    });
  }

  @Test
  public void testUpdateProduct(final TestContext context) {
    final Async async = context.async();
    service.getProductList(1, 10).setHandler(productsResult -> {
      final List<Product> products = productsResult.result().getProducts();
      final ProductData data = new ProductData();
      data.setName("myNewProduct");
      data.setPrice(900);
      service.updateProduct(products.get(0).getId(), data).setHandler(productResult -> {
        assertTrue(productResult.succeeded());
        final Product product = productResult.result();
        assertNotNull(product);
        assertEquals(products.get(0).getId(), product.getId());
        assertEquals("myNewProduct", product.getData().getName());
        assertEquals(Integer.valueOf(900), product.getData().getPrice());
        async.complete();
      });
    });
  }

  @Test
  public void testUpdateProduct_notFound(final TestContext context) {
    final Async async = context.async();
    final ProductData data = new ProductData();
    data.setName("myNewProduct");
    data.setPrice(900);
    service.updateProduct(42, data).setHandler(productResult -> {
      assertTrue(productResult.failed());
      assertNull(productResult.result());
      async.complete();
    });
  }

  @Test
  public void testDeleteProduct(final TestContext context) {
    final Async async = context.async();
    service.getProductList(1, 10).setHandler(productsResult -> {
      final List<Product> products = productsResult.result().getProducts();
      service.deleteProduct(products.get(0).getId()).setHandler(productResult -> {
        assertTrue(productResult.succeeded());
        final Product product = productResult.result();
        assertNotNull(product);
        assertEquals(products.get(0).getId(), product.getId());
        async.complete();
      });
    });
  }

  @Test
  public void testDeleteProduct_notFound(final TestContext context) {
    final Async async = context.async();
    service.deleteProduct(42).setHandler(productResult -> {
      assertTrue(productResult.failed());
      assertNull(productResult.result());
      async.complete();
    });
  }

}
