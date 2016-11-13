package net.brainified.db;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

import io.vertx.rxjava.ext.mongo.MongoClient;

public final class DBModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(MongoClient.class).toProvider(MongoClientProvider.class);
    bind(ProductDao.class).to(ProductDaoImpl.class).in(Scopes.SINGLETON);
  }
}
