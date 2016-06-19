package io.vertx.blog.first;

import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

@RunWith(VertxUnitRunner.class)
public class HttpServerVerticleTest {

	private Vertx vertx;

	private WhiskyService serviceMock;

	@Before
	public void setUp(TestContext context) {
		serviceMock = Mockito.mock(WhiskyService.class);

		vertx = Vertx.vertx();
		final HttpServerVerticle verticle = new HttpServerVerticle(new WhiskyHandlerImpl(serviceMock));
		vertx.deployVerticle(verticle, context.asyncAssertSuccess());
	}

	@After
	public void tearDown(TestContext context) {
		vertx.close(context.asyncAssertSuccess());
	}

	@Test
	public void testGetWhiskies_200(TestContext context) {
		final Async async = context.async();

		vertx.createHttpClient().getNow(8080, "localhost", "/api/whiskies", response -> {
			context.assertEquals(200, response.statusCode());
			response.handler(body -> {
				context.assertTrue(body.toJsonArray().getList().isEmpty());
				verify(serviceMock).getAll();
				async.complete();
			});
		});
	}

	@Test
	public void testGetWhisky_200(TestContext context) {
		when(serviceMock.getOne(1)).thenReturn(Optional.of(new Whisky("name", "origin")));

		final Async async = context.async();

		vertx.createHttpClient().getNow(8080, "localhost", "/api/whiskies/1", response -> {
			response.handler(body -> {
				context.assertEquals(200, response.statusCode());
				final Whisky whisky = Json.decodeValue(body.toString(), Whisky.class);
				context.assertEquals("name", whisky.getName());
				context.assertEquals("origin", whisky.getOrigin());
				verify(serviceMock).getOne(1);
				async.complete();
			});
		});
	}

	@Test
	public void testGetWhisky_400(TestContext context) {
		final Async async = context.async();

		vertx.createHttpClient().getNow(8080, "localhost", "/api/whiskies/x", response -> {
			context.assertEquals(400, response.statusCode());
			verifyZeroInteractions(serviceMock);
			async.complete();
		});
	}

	@Test
	public void testGetWhisky_404(TestContext context) {
		when(serviceMock.getOne(1)).thenReturn(Optional.ofNullable(null));

		final Async async = context.async();

		vertx.createHttpClient().getNow(8080, "localhost", "/api/whiskies/1", response -> {
			context.assertEquals(404, response.statusCode());
			verify(serviceMock).getOne(1);
			async.complete();
		});
	}
}