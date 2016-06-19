package io.vertx.blog.first;

import com.google.inject.AbstractModule;

final class ApplicationModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(WhiskyService.class).to(WhiskyServiceImpl.class);
		bind(WhiskyHandler.class).to(WhiskyHandlerImpl.class);
	}
}
