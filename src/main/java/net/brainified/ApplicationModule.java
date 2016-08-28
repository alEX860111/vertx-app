package net.brainified;

import com.google.inject.AbstractModule;

import io.vertx.core.Vertx;

final class ApplicationModule extends AbstractModule {
	@Override
	protected void configure() {
	  final Vertx vertx = Vertx.vertx();
	  bind(Vertx.class).toInstance(vertx);
		bind(ProductService.class).to(ProductServiceImpl.class);
		bind(ProductHandler.class).to(ProductHandlerImpl.class);
	}
}
