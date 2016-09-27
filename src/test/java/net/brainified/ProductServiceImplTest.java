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
    assertEquals(sort, options.getSort());
  }

  @Test
  public void testGetProduct() {
    final JsonObject query = new JsonObject();
    query.put("_id", "1");

    final JsonObject fields = new JsonObject();

    final JsonObject product = new JsonObject();
    product.put("_id", "1");
    product.put("name", "myProduct");

    when(client.findOneObservable("products", query, fields)).thenReturn(Observable.just(product));

    serviceSUT.getProduct("1").subscribe(result -> assertEquals(product, result));

    verify(client).findOneObservable("products", query, fields);
  }

  @Test
  public void testAddProduct() {
    final JsonObject product = new JsonObject();
    when(client.insertObservable("products", product)).thenReturn(Observable.just("id"));

    serviceSUT.addProduct(product).subscribe(id -> assertEquals("id", id));

    verify(client).insertObservable("products", product);
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
    final JsonObject query = new JsonObject();
    query.put("_id", "1");

    final MongoClientDeleteResult result = Mockito.mock(MongoClientDeleteResult.class);
    when(result.getRemovedCount()).thenReturn(1L);

    when(client.removeDocumentObservable("products", query)).thenReturn(Observable.just(result));

    serviceSUT.deleteProduct("1").subscribe(numDeleted -> assertEquals(Long.valueOf(1L), numDeleted));

    verify(client).removeDocumentObservable("products", query);
  }

}
