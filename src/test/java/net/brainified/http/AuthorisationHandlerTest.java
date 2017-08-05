package net.brainified.http;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import static org.mockito.Mockito.*;

import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.http.HttpServerResponse;
import io.vertx.rxjava.ext.auth.User;
import io.vertx.rxjava.ext.web.RoutingContext;
import net.brainified.db.Role;

@RunWith(MockitoJUnitRunner.class)
public class AuthorisationHandlerTest {

  @Mock
  private RoutingContext routingContext;

  private AuthorisationHandler authorisationHandler;

  private HttpServerResponse response;

  @Before
  public void setUp() {
    final User user = Mockito.mock(User.class);
    final JsonObject principal = new JsonObject().put("role", Role.USER.name());
    when(user.principal()).thenReturn(principal);
    when(routingContext.user()).thenReturn(user);

    response = Mockito.mock(HttpServerResponse.class);
    when(response.setStatusCode(anyInt())).thenReturn(response);
    when(routingContext.response()).thenReturn(response);

  }

  @Test
  public void handle() {
    authorisationHandler = new AuthorisationHandler(new Role[] { Role.USER });

    authorisationHandler.handle(routingContext);

    verify(routingContext).next();
    verifyZeroInteractions(response);
  }

  @Test
  public void handle_() {
    authorisationHandler = new AuthorisationHandler(new Role[] { Role.ADMIN });

    authorisationHandler.handle(routingContext);

    verify(routingContext, never()).next();
    verify(response).setStatusCode(403);
    verify(response).end();
  }

}
