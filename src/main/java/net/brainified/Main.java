package net.brainified;

import com.google.inject.Guice;
import com.google.inject.Injector;

import io.vertx.core.Vertx;

public final class Main {

	public static void main(String[] args) {
		final Injector injector = Guice.createInjector(new ApplicationModule());
		final Vertx vertx = injector.getInstance(Vertx.class);
		vertx.deployVerticle(injector.getInstance(HttpServerVerticle.class));
	}

}
