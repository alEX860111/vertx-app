package net.brainified;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.*;

import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.mongo.MongoClientDeleteResult;
import io.vertx.ext.mongo.MongoClientUpdateResult;
import io.vertx.rxjava.ext.mongo.MongoClient;
import rx.Observable;

@RunWith(MockitoJUnitRunner.class)
public class ProductDaoImplTest {

  @Mock
  private MongoClient client;

  @InjectMocks
  private ProductDaoImpl serviceSUT;

  @Test
  public void testGetProductCount() {
    final JsonObject query = new JsonObject();
    when(client.countObservable("products", query)).thenReturn(Observable.just(42L));

    serviceSUT.getProductCount().subscribe(count -> assertEquals(Long.valueOf(42L), count));

    verify(client).countObservable("products", query);
  }

  @Test
  public void testGetProductList() {
    final JsonObject query = new JsonObject();

    final ArgumentCaptor<FindOptions> optionsCaptor = ArgumentCaptor.forClass(FindOptions.class);

    when(client.findWithOptionsObservable(eq("products"), eq(query), optionsCaptor.capture())).thenReturn(Observable.just(Collections.emptyList()));

    serviceSUT.getProductList(1, 10).subscribe(products -> assertTrue(products.isEmpty()));

    verify(client).findWithOptionsObservable(eq("products"), eq(query), optionsCaptor.capture());

    final FindOptions options = optionsCaptor.getValue();
    assertEquals(10, options.getLimit());
    assertEquals(0, options.getSkip());
    final JsonObject sort = new JsonObject();
    sort.put("createdAt", -1);
    assertEquals(sort, options.getSort());
  }

  @Test
  public void testGetProduct() {
    final JsonObject query = new JsonObject().put("_id", "1");

    final JsonObject fields = new JsonObject();

    final JsonObject product = new JsonObject().put("_id", "1").put("name", "myProduct");

    when(client.findOneObservable("products", query, fields)).thenReturn(Observable.just(product));

    serviceSUT.getProduct("1").subscribe(result -> assertEquals(product, result.get()));

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
    final JsonObject data = new JsonObject();

    final JsonObject query = new JsonObject().put("_id", "1");

    final JsonObject product = new JsonObject().put("data", data);
    final JsonObject update = new JsonObject().put("$set", product);

    final MongoClientUpdateResult result = Mockito.mock(MongoClientUpdateResult.class);
    when(result.getDocModified()).thenReturn(1L);

    when(client.updateCollectionObservable("products", query, update)).thenReturn(Observable.just(result));

    serviceSUT.updateProduct("1", data).subscribe(numModified -> assertEquals(Long.valueOf(1L), numModified));

    verify(client).updateCollectionObservable("products", query, update);
  }

  @Test
  public void testDeleteProduct() {
    final JsonObject query = new JsonObject().put("_id", "1");

    final MongoClientDeleteResult result = Mockito.mock(MongoClientDeleteResult.class);
    when(result.getRemovedCount()).thenReturn(1L);

    when(client.removeDocumentObservable("products", query)).thenReturn(Observable.just(result));

    serviceSUT.deleteProduct("1").subscribe(numDeleted -> assertEquals(Long.valueOf(1L), numDeleted));

    verify(client).removeDocumentObservable("products", query);
  }

}
