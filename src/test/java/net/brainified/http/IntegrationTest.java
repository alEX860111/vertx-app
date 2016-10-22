package net.brainified.http;

import org.junit.After;
import org.junit.Before;
import org.mockito.Mockito;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.rxjava.core.RxHelper;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.eventbus.EventBus;

public abstract class IntegrationTest {

  protected Vertx vertx;

  protected EventBus eventBusMock;

  @Before
  public void setUp(TestContext context) {
    final Async async = context.async();
    vertx = Vertx.vertx();
    eventBusMock = Mockito.mock(EventBus.class);
    final Injector injector = Guice.createInjector(new AbstractModule() {
      @Override
      protected void configure() {
        bind(Vertx.class).toInstance(vertx);
        bind(EventBus.class).toInstance(eventBusMock);
      }
    }, new HttpModule());
    RxHelper.deployVerticle(vertx, injector.getInstance(HttpServerVerticle.class)).subscribe((s) -> async.complete());
  }

  @After
  public void tearDown(TestContext context) {
    vertx.close(context.asyncAssertSuccess());
  }

}