package net.brainified;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

@RunWith(MockitoJUnitRunner.class)
public class IdParameterHandlerImplTest {

  @Mock
  private RoutingContext context;

  @Mock
  private HttpServerRequest request;

  @Mock
  private HttpServerResponse response;

  private IdParameterHandlerImpl handlerSUT;

  @Before
  public void setUp() {
    when(context.request()).thenReturn(request);
    when(context.response()).thenReturn(response);
    when(response.setStatusCode(400)).thenReturn(response);
    handlerSUT = new IdParameterHandlerImpl();
  }

  @Test
  public void testValidId() {
    when(request.getParam("id")).thenReturn("42");
    handlerSUT.handleIdParameter(context);
    verify(context).put("id", Integer.valueOf(42));
    verify(context).next();
  }

  @Test
  public void testInvalidId() {
    when(request.getParam("id")).thenReturn("42xx");
    handlerSUT.handleIdParameter(context);
    verify(context, never()).put("id", Integer.valueOf(42));
    verify(context, never()).next();
    verify(context).response();
    verify(response).setStatusCode(400);
    verify(response).end("Invalid id");
  }

}
