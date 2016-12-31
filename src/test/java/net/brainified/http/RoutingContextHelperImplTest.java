package net.brainified.http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Range;

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
    when(routingContext.request()).thenReturn(request);
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
  public void testGetParamAsEnum() {
    when(request.getParam("sortorder")).thenReturn("asc");
    Optional<SortOrder> sortOrderOptional = routingContextHelper.getParamAsEnum(routingContext, "sortorder", SortOrder.class);
    assertEquals(SortOrder.ASC, sortOrderOptional.get());
  }

  @Test
  public void testGetParamAsEnum_Empty() {
    Optional<SortOrder> sortOrderOptional = routingContextHelper.getParamAsEnum(routingContext, "sortorder", SortOrder.class);
    assertFalse(sortOrderOptional.isPresent());
  }

  @Test(expected = HandlerException.class)
  public void testGetParamAsEnum_Exception() {
    when(request.getParam("sortorder")).thenReturn("some value");
    routingContextHelper.getParamAsEnum(routingContext, "sortorder", SortOrder.class);
  }

  @Test
  public void testGetParamAsInteger() {
    when(request.getParam("page")).thenReturn("10");
    final Optional<Integer> pageOptional = routingContextHelper.getParamAsInteger(routingContext, "page", Range.closed(1, 20));
    assertEquals(Integer.valueOf(10), pageOptional.get());
  }

  @Test
  public void testGetParamAsInteger_Empty() {
    final Optional<Integer> pageOptional = routingContextHelper.getParamAsInteger(routingContext, "page", Range.closed(1, 20));
    assertFalse(pageOptional.isPresent());
  }

  @Test(expected = HandlerException.class)
  public void testGetParamAsInteger_NotAnInteger() {
    when(request.getParam("page")).thenReturn("some value");
    routingContextHelper.getParamAsInteger(routingContext, "page", Range.closed(1, 20));
  }

  @Test(expected = HandlerException.class)
  public void testGetParamAsInteger_NotInRange() {
    when(request.getParam("page")).thenReturn("30");
    routingContextHelper.getParamAsInteger(routingContext, "page", Range.closed(1, 20));
  }

}
