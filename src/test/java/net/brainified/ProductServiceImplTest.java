package net.brainified;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.mongo.MongoClientDeleteResult;
import io.vertx.ext.mongo.MongoClientUpdateResult;
import io.vertx.rxjava.ext.mongo.MongoClient;
import rx.Observable;

@RunWith(MockitoJUnitRunner.class)
public class ProductServiceImplTest {

  @Mock
  private MongoClient client;

  @InjectMocks
  private ProductServiceImpl serviceSUT;

  @Test
  public void testGetProductCount() {
    @SuppressWarnings("unchecked")
    final Handler<AsyncResult<Long>> handler = Mockito.mock(Handler.class);

    serviceSUT.getProductCount(handler);

    verify(client).count("products", new JsonObject(), handler);
  }

  @Test
  public void testGetProductList() {
    @SuppressWarnings("unchecked")
    final Handler<AsyncResult<List<JsonObject>>> handler = Mockito.mock(Handler.class);

    final ArgumentCaptor<FindOptions> optionsCaptor = ArgumentCaptor.forClass(FindOptions.class);

    serviceSUT.getProductList(1, 10, handler);

    verify(client).findWithOptions(eq("products"), eq(new JsonObject()), optionsCaptor.capture(), eq(handler));
    final FindOptions options = optionsCaptor.getValue();
    assertEquals(10, options.getLimit());
    assertEquals(0, options.getSkip());
    final JsonObject sort = new JsonObject();
    sort.put("createdAt", -1);
    assertEquals(sort , options.getSort());
  }

  @Test
  public void testGetProduct() {
    final JsonObject query = new JsonObject();
    query.put("_id", "1");
    when(client.findOneObservable(eq("products"), eq(query), eq(new JsonObject()))).thenReturn(Observable.just(new JsonObject()));

    serviceSUT.getProduct("1");

    verify(client).findOneObservable(eq("products"), eq(query), eq(new JsonObject()));
  }

  @Test
  public void testAddProduct() {
    @SuppressWarnings("unchecked")
    final Handler<AsyncResult<String>> handler = Mockito.mock(Handler.class);

    final JsonObject product = new JsonObject();

    serviceSUT.addProduct(product, handler);

    verify(client).insert(eq("products"), eq(product), eq(handler));
  }

  @Test
  public void testUpdateProduct() {
    @SuppressWarnings("unchecked")
    final Handler<AsyncResult<MongoClientUpdateResult>> handler = Mockito.mock(Handler.class);

    final JsonObject data = new JsonObject();

    serviceSUT.updateProduct("1", data, handler);

    final JsonObject query = new JsonObject();
    query.put("_id", "1");

    final JsonObject product = new JsonObject();
    product.put("data", data);

    final JsonObject update = new JsonObject();
    update.put("$set", product);
    verify(client).updateCollection(eq("products"), eq(query), eq(update), eq(handler));
  }

  @Test
  public void testDeleteProduct() {
    @SuppressWarnings("unchecked")
    final Handler<AsyncResult<MongoClientDeleteResult>> handler = Mockito.mock(Handler.class);

    serviceSUT.deleteProduct("1", handler);

    final JsonObject query = new JsonObject();
    query.put("_id", "1");
    verify(client).removeDocument(eq("products"), eq(query), eq(handler));
  }

}
