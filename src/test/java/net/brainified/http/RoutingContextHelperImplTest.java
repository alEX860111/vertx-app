package net.brainified.http;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

import java.util.Optional;

import io.vertx.rxjava.core.http.HttpServerRequest;
import io.vertx.rxjava.ext.web.RoutingContext;
import net.brainified.db.SortOrder;

@RunWith(MockitoJUnitRunner.class)
public class RoutingContextHelperImplTest {

  private static final class Person {

    private String name;

    public String getName() {
      return name;
    }

    @SuppressWarnings("unused")
    public void setName(String name) {
      this.name = name;
    }
  }

  @Mock
  private RoutingContext routingContext;

  @Mock
  private HttpServerRequest request;

  private RoutingContextHelperImpl routingContextHelper;

  @Before
  public void setUp() {
    routingContextHelper = new RoutingContextHelperImpl();
  }

  @Test
  public void testGetBody() {
    when(routingContext.getBodyAsString()).thenReturn("{\"name\":\"joe\"}");
    final Person person = routingContextHelper.getBody(routingContext, Person.class);
    assertEquals("joe", person.getName());
  }

  @Test(expected = HandlerException.class)
  public void testGetBody_Exception() {
    when(routingContext.getBodyAsString()).thenReturn("{\"firstName\":\"joe\"}");
    routingContextHelper.getBody(routingContext, Person.class);
  }

  @Test
  public void getParamAsEnum() {
    when(request.getParam("sortorder")).thenReturn("asc");
    when(routingContext.request()).thenReturn(request);
    Optional<SortOrder> sortOrderOptional = routingContextHelper.getParamAsEnum(routingContext, "sortorder", SortOrder.class);
    assertEquals(SortOrder.ASC, sortOrderOptional.get());
  }

  @Test
  public void getParamAsEnum_Empty() {
    when(routingContext.request()).thenReturn(request);
    Optional<SortOrder> sortOrderOptional = routingContextHelper.getParamAsEnum(routingContext, "sortorder", SortOrder.class);
    assertFalse(sortOrderOptional.isPresent());
  }

  @Test(expected = HandlerException.class)
  public void getParamAsEnum_Exception() {
    when(request.getParam("sortorder")).thenReturn("some value");
    when(routingContext.request()).thenReturn(request);
    routingContextHelper.getParamAsEnum(routingContext, "sortorder", SortOrder.class);
  }

}
