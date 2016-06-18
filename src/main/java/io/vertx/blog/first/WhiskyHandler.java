package io.vertx.blog.first;

import io.vertx.ext.web.RoutingContext;

interface WhiskyHandler {

	void addOne(RoutingContext routingContext);

	void getOne(RoutingContext routingContext);

	void updateOne(RoutingContext routingContext);

	void deleteOne(RoutingContext routingContext);

	void getAll(RoutingContext routingContext);

}