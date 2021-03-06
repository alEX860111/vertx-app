package net.brainified;

import com.google.inject.AbstractModule;

import io.vertx.rxjava.core.Vertx;
import net.brainified.db.DBModule;
import net.brainified.http.HttpModule;

final class ApplicationModule extends AbstractModule {
  @Override
  protected void configure() {
    final Vertx vertx = Vertx.vertx();
    bind(Vertx.class).toInstance(vertx);

    install(new DBModule());
    install(new HttpModule());
  }
}
