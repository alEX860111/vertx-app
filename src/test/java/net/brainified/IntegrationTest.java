package net.brainified;

import org.junit.After;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import com.google.inject.util.Modules;

import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.rxjava.core.RxHelper;
import io.vertx.rxjava.core.Vertx;
import net.brainified.db.Dao;
import net.brainified.db.Product;
import net.brainified.http.HttpServerVerticle;

public abstract class IntegrationTest {

  protected Vertx vertx;

  @Mock
  protected Dao<Product> dao;

  @Before
  public void setUp(TestContext context) {
    System.setProperty("environment", "test");

    MockitoAnnotations.initMocks(this);

    final Async async = context.async();
    final Injector injector = Guice.createInjector(Modules.override(new ApplicationModule()).with(new AbstractModule() {

      @Override
      protected void configure() {
        bind(new TypeLiteral<Dao<Product>>() { }).toInstance(dao);
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