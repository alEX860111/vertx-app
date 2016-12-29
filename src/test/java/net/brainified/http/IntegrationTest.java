package net.brainified.http;

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Provider;

import org.junit.After;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.common.base.Preconditions;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;

import io.vertx.core.Handler;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.rxjava.core.RxHelper;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.ext.web.RoutingContext;
import io.vertx.rxjava.ext.web.handler.BodyHandler;
import net.brainified.db.Dao;
import net.brainified.db.Product;
import net.brainified.http.products.ProductsHandlerModule;

public abstract class IntegrationTest {

  protected Vertx vertx;

  @Mock
  protected Dao<Product> dao;

  @Before
  public void setUp(TestContext context) {
    MockitoAnnotations.initMocks(this);

    final Async async = context.async();
    vertx = Vertx.vertx();
    final Injector injector = Guice.createInjector(new AbstractModule() {
      @Override
      protected void configure() {
        bind(Vertx.class).toInstance(vertx);
        bind(new TypeLiteral<Dao<Product>>() {}).toInstance(dao);
        bind(Router.class).toProvider(RouterTestProvider.class);
      }
    }, new ProductsHandlerModule());
    RxHelper.deployVerticle(vertx, injector.getInstance(HttpServerVerticle.class)).subscribe((s) -> async.complete());
  }

  @After
  public void tearDown(TestContext context) {
    vertx.close(context.asyncAssertSuccess());
  }

  private static final class RouterTestProvider implements Provider<Router> {

    private Vertx vertx;

    private Set<Handler<RoutingContext>> handlers;

    @Inject
    public RouterTestProvider(final Vertx vertx, final Set<Handler<RoutingContext>> handlers) {
      this.vertx = vertx;
      this.handlers = handlers;
    }

    @Override
    public Router get() {
      final Router router = Router.router(vertx);
      router.route("/*").handler(BodyHandler.create());
      handlers.forEach(handler -> registerHandler(router, handler));
      return router;
    }

    private void registerHandler(final Router router, final Handler<RoutingContext> handler) {
      final HandlerConfiguration config = handler.getClass().getAnnotation(HandlerConfiguration.class);
      Preconditions.checkNotNull(config, "Missing HandlerConfiguration");

      router.route(config.method(), config.path()).handler(handler);
    }

  }

}