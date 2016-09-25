package net.brainified;

import org.junit.After;
import org.junit.Before;
import org.mockito.Mockito;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.util.Modules;

import io.vertx.core.Vertx;
import io.vertx.ext.unit.TestContext;

public abstract class IntegrationTest {

  protected Vertx vertx;

  protected ProductService serviceMock;

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

}