package io.vertx.blog.first;

import java.util.Objects;
import java.util.Optional;

import javax.inject.Inject;

import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

final class WhiskyHandlerImpl implements WhiskyHandler {

	private final WhiskyService service;

	@Inject
	public WhiskyHandlerImpl(final WhiskyService service) {
		this.service = service;
	}

	@Override
	public void addOne(RoutingContext routingContext) {
		final Whisky whisky = Json.decodeValue(routingContext.getBodyAsString(), Whisky.class);
		service.addOne(whisky);
		routingContext.response().setStatusCode(201).putHeader("content-type", "application/json; charset=utf-8")
				.end(Json.encodePrettily(whisky));
	}

	@Override
	public void getOne(final RoutingContext routingContext) {
		final Integer id;
		try {
			id = Integer.valueOf(routingContext.request().getParam("id"));
		} catch (final NumberFormatException e) {
			routingContext.response().setStatusCode(400).end();
			return;
		}

		final Optional<Whisky> whisky = service.getOne(id);
		if (whisky.isPresent()) {
			routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
					.end(Json.encodePrettily(whisky.get()));
		} else {
			routingContext.response().setStatusCode(404).end();
		}

	}

	@Override
	public void updateOne(RoutingContext routingContext) {
		final String id = routingContext.request().getParam("id");
		JsonObject json = routingContext.getBodyAsJson();
		if (Objects.isNull(id) || Objects.isNull(json)) {
			routingContext.response().setStatusCode(400).end();
		} else {
			final Integer idAsInteger = Integer.valueOf(id);
			Optional<Whisky> whiskyOptional = service.updateOne(idAsInteger, json.getString("name"),
					json.getString("origin"));
			if (whiskyOptional.isPresent()) {
				routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
						.end(Json.encodePrettily(whiskyOptional.get()));
			} else {
				routingContext.response().setStatusCode(404).end();
			}
		}
	}

	@Override
	public void deleteOne(RoutingContext routingContext) {
		String id = routingContext.request().getParam("id");
		if (Objects.isNull(id)) {
			routingContext.response().setStatusCode(400).end();
		} else {
			Integer idAsInteger = Integer.valueOf(id);
			service.deleteOne(idAsInteger);
		}
		routingContext.response().setStatusCode(204).end();
	}

	@Override
	public void getAll(final RoutingContext routingContext) {
		routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
				.end(Json.encodePrettily(service.getAll()));
	}

}
