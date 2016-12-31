package net.brainified.http;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import io.vertx.rxjava.core.http.HttpServerResponse;
import io.vertx.rxjava.ext.web.RoutingContext;

@RunWith(MockitoJUnitRunner.class)
public class FailureHandlerTest {

  @Mock
  private RoutingContext routingContext;

  @Mock
  private HttpServerResponse response;

  private FailureHandler failureHandler;

  @Before
  public void setUp() {
    failureHandler = new FailureHandler();
  }

  @Test
  public void testHandle_RuntimeException() {
    when(routingContext.failure()).thenReturn(new RuntimeException());
    failureHandler.handle(routingContext);
    verify(routingContext, never()).response();
    verify(routingContext).next();
  }

  @Test
  public void testHandle_HandlerException() {
    when(routingContext.failure()).thenReturn(new HandlerException("msg", 400));
    when(routingContext.response()).thenReturn(response);
    when(response.putHeader("Content-Type", "text/plain; charset=utf-8")).thenReturn(response);
    when(response.setStatusCode(400)).thenReturn(response);
    failureHandler.handle(routingContext);
    verify(routingContext).response();
    verify(response).putHeader("Content-Type", "text/plain; charset=utf-8");
    verify(response).setStatusCode(400);
    verify(response).end("msg");
    verify(routingContext, never()).next();
  }

}
