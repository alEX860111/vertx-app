package net.brainified.db;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;

import io.vertx.rxjava.ext.mongo.MongoClient;

public final class DBModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(MongoClient.class).toProvider(MongoClientProvider.class).in(Scopes.SINGLETON);
    bind(new TypeLiteral<Dao<Product>>() {}).to(MongoProductDao.class);
    bind(UserDao.class).to(MongoUserDao.class);
  }

}
