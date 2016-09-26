package net.brainified;

import org.junit.After;
import org.junit.Before;
import org.mockito.Mockito;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.util.Modules;

import io.vertx.rxjava.core.RxHelper;
import io.vertx.rxjava.core.Vertx;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;

public abstract class IntegrationTest {

  protected Vertx vertx;

  protected ProductService serviceMock;

  @Before
  public void setUp(TestContext context) {
    final Async async = context.async();
    serviceMock = Mockito.mock(ProductService.class);
    final Injector injector = Guice.createInjector(Modules.override(new ApplicationModule()).with(new AbstractModule() {
      @Override
      protected void configure() {
        bind(ProductService.class).toInstance(serviceMock);
      }
    }));
    vertx = injector.getInstance(Vertx.class);
    RxHelper.deployVerticle(vertx, injector.getInstance(HttpServerVerticle.class)).subscribe((s) -> async.complete());
  }

  @After
  public void tearDown(TestContext context) {
    vertx.close(context.asyncAssertSuccess());
  }

}