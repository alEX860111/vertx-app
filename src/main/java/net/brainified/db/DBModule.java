package net.brainified.db;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

import io.vertx.rxjava.ext.mongo.MongoClient;

public final class DBModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(MongoClient.class).toProvider(MongoClientProvider.class).in(Scopes.SINGLETON);
    bind(ProductDao.class).to(MongoProductDao.class);
    bind(UserDao.class).to(MongoUserDao.class);
  }

}
