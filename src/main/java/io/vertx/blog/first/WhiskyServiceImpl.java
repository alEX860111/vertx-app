package io.vertx.blog.first;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

final class WhiskyServiceImpl implements WhiskyService {

	private final Map<Integer, Whisky> products = new LinkedHashMap<>();

	public WhiskyServiceImpl() {
		final Whisky bowmore = new Whisky("Bowmore 15 Years Laimrig", "Scotland, Islay");
		products.put(bowmore.getId(), bowmore);
		final Whisky talisker = new Whisky("Talisker 57° North", "Scotland, Island");
		products.put(talisker.getId(), talisker);
	}

	@Override
	public void addOne(RoutingContext routingContext) {
		final Whisky whisky = Json.decodeValue(routingContext.getBodyAsString(), Whisky.class);
		products.put(whisky.getId(), whisky);

		routingContext.response().setStatusCode(201).putHeader("content-type", "application/json; charset=utf-8")
				.end(Json.encodePrettily(whisky));
	}

	@Override
	public void getOne(RoutingContext routingContext) {
		final String id = routingContext.request().getParam("id");
		if (Objects.isNull(id)) {
			routingContext.response().setStatusCode(400).end();
		} else {
			final Integer idAsInteger = Integer.valueOf(id);
			Whisky whisky = products.get(idAsInteger);
			if (Objects.isNull(whisky)) {
				routingContext.response().setStatusCode(404).end();
			} else {
				routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
						.end(Json.encodePrettily(whisky));
			}
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
			Whisky whisky = products.get(idAsInteger);
			if (Objects.isNull(whisky)) {
				routingContext.response().setStatusCode(404).end();
			} else {
				whisky.setName(json.getString("name"));
				whisky.setOrigin(json.getString("origin"));
				routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
						.end(Json.encodePrettily(whisky));
			}
		}
	}

	/* (non-Javadoc)
	 * @see io.vertx.blog.first.WhiskyService#deleteOne(io.vertx.ext.web.RoutingContext)
	 */
	@Override
	public void deleteOne(RoutingContext routingContext) {
		String id = routingContext.request().getParam("id");
		if (Objects.isNull(id)) {
			routingContext.response().setStatusCode(400).end();
		} else {
			Integer idAsInteger = Integer.valueOf(id);
			products.remove(idAsInteger);
		}
		routingContext.response().setStatusCode(204).end();
	}

	/* (non-Javadoc)
	 * @see io.vertx.blog.first.WhiskyService#getAll(io.vertx.ext.web.RoutingContext)
	 */
	@Override
	public void getAll(RoutingContext routingContext) {
		routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
				.end(Json.encodePrettily(products.values()));
	}

}
