package net.brainified.db;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.eventbus.Message;
import io.vertx.rxjava.ext.mongo.MongoClient;

public final class DBModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(MongoClient.class).toProvider(MongoClientProvider.class);
    bind(ProductDao.class).to(ProductDaoImpl.class).in(Scopes.SINGLETON);
    final Multibinder<Handler<Message<JsonObject>>> handlers = Multibinder.newSetBinder(binder(), new TypeLiteral<Handler<Message<JsonObject>>>() {
    });
    handlers.addBinding().to(GetProductCountHandler.class);
    handlers.addBinding().to(GetProductListHandler.class);
    handlers.addBinding().to(GetProductHandler.class);
    handlers.addBinding().to(AddProductHandler.class);
    handlers.addBinding().to(UpdateProductHandler.class);
    handlers.addBinding().to(DeleteProductHandler.class);
  }
}
