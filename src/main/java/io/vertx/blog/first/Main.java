package io.vertx.blog.first;

import com.google.inject.Guice;
import com.google.inject.Injector;

import io.vertx.core.Vertx;

public final class Main {

	public static void main(String[] args) {
		final Injector injector = Guice.createInjector(new ApplicationModule());
		Vertx.vertx().deployVerticle(injector.getInstance(HttpServerVerticle.class));
	}

}
