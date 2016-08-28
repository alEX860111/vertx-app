package net.brainified;

import static org.junit.Assert.assertEquals;
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
  public void testGetProducts(final TestContext context) {
    final Async async = context.async();
    service.getProducts().setHandler(productsResult -> {
      assertTrue(productsResult.succeeded());
      final List<Product> products = productsResult.result();
      assertEquals(2, products.size());
      assertEquals("iphone", products.get(0).getName());
      assertEquals(Integer.valueOf(100), products.get(0).getPrice());
      assertEquals("razr", products.get(1).getName());
      assertEquals(Integer.valueOf(200), products.get(1).getPrice());
      async.complete();
    });
  }

  @Test
  public void testGetProduct(final TestContext context) {
    final Async async = context.async();
    service.getProducts().setHandler(productsResult -> {
      final List<Product> products = productsResult.result();
      service.getProduct(products.get(0).getId()).setHandler(productResult -> {
        assertTrue(productResult.succeeded());
        final Product product = productResult.result();
        assertEquals("iphone", product.getName());
        assertEquals(Integer.valueOf(100), product.getPrice());
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
    service.addProduct("myNewProduct", 900).setHandler(productResult -> {
      assertTrue(productResult.succeeded());
      final Product product = productResult.result();
      assertNotNull(product);
      assertTrue(product.getId() > 0);
      assertEquals("myNewProduct", product.getName());
      assertEquals(Integer.valueOf(900), product.getPrice());

      async.complete();
    });
  }

  @Test
  public void testUpdateProduct(final TestContext context) {
    final Async async = context.async();
    service.getProducts().setHandler(productsResult -> {
      final List<Product> products = productsResult.result();
      assertEquals(2, products.size());
      service.updateProduct(products.get(0).getId(), "myNewProduct", 900).setHandler(productResult -> {
        assertTrue(productResult.succeeded());
        final Product product = productResult.result();
        assertNotNull(product);
        assertEquals(products.get(0).getId(), product.getId());
        assertEquals("myNewProduct", product.getName());
        assertEquals(Integer.valueOf(900), product.getPrice());
        async.complete();
      });
    });
  }

  @Test
  public void testUpdateProduct_notFound(final TestContext context) {
    final Async async = context.async();
    service.updateProduct(42, "myNewProduct", 900).setHandler(productResult -> {
      assertTrue(productResult.failed());
      assertNull(productResult.result());
      async.complete();
    });
  }

  @Test
  public void testDeleteProduct(final TestContext context) {
    final Async async = context.async();
    service.getProducts().setHandler(productsResult -> {
      final List<Product> products = productsResult.result();
      assertEquals(2, products.size());
      service.deleteProduct(products.get(0).getId()).setHandler(productResult -> {
        assertTrue(productResult.succeeded());
        final Product product = productResult.result();
        assertNotNull(product);
        assertEquals(products.get(0).getId(), product.getId());
        assertEquals("iphone", products.get(0).getName());
        assertEquals(Integer.valueOf(100), products.get(0).getPrice());
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
