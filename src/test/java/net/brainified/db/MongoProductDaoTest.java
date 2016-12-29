package net.brainified.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
public class MongoProductDaoTest {

  @Mock
  private MongoClient client;

  @InjectMocks
  private MongoProductDao serviceSUT;

  @Test
  public void testGetProductCount() {
    final JsonObject query = new JsonObject();
    when(client.countObservable("products", query)).thenReturn(Observable.just(42L));

    serviceSUT.getCount().subscribe(count -> assertEquals(Long.valueOf(42L), count));

    verify(client).countObservable("products", query);
  }

  @Test
  public void testGetProductList() {
    final JsonObject query = new JsonObject();

    final ArgumentCaptor<FindOptions> optionsCaptor = ArgumentCaptor.forClass(FindOptions.class);

    when(client.findWithOptionsObservable(eq("products"), eq(query), optionsCaptor.capture())).thenReturn(Observable.just(Collections.emptyList()));

    serviceSUT.getList(1, 10).subscribe(products -> assertTrue(products.isEmpty()));

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

    final JsonObject document = new JsonObject().put("_id", "1");

    when(client.findOneObservable("products", query, fields)).thenReturn(Observable.just(document));

    serviceSUT.get("1").subscribe(product -> {
      assertTrue(product.isPresent());
      assertEquals("1", product.get().get_id());
    });

    verify(client).findOneObservable("products", query, fields);
  }

  @Test
  public void testAddProduct() {
    when(client.insertObservable(eq("products"), any(JsonObject.class))).thenReturn(Observable.just("id"));

    final Product product = new Product();
    serviceSUT.add(product).subscribe(savedProduct -> {
      assertEquals("id", savedProduct.get_id());
      assertFalse(savedProduct.getCreatedAt().isEmpty());
    });

    verify(client).insertObservable(eq("products"), any(JsonObject.class));
  }

  @Test
  public void testUpdateProduct() {
    final JsonObject query = new JsonObject().put("_id", "1");

    final JsonObject document = new JsonObject().put("name", "abc").put("price", 299.99d);
    final JsonObject update = new JsonObject().put("$set", document);

    final MongoClientUpdateResult result = Mockito.mock(MongoClientUpdateResult.class);
    when(result.getDocModified()).thenReturn(1L);

    when(client.updateCollectionObservable("products", query, update)).thenReturn(Observable.just(result));

    final Product product = new Product();
    product.setName("abc");
    product.setPrice(299.99d);
    serviceSUT.update("1", product).subscribe(numModified -> assertEquals(Long.valueOf(1L), numModified));

    verify(client).updateCollectionObservable("products", query, update);
  }

  @Test
  public void testDeleteProduct() {
    final JsonObject query = new JsonObject().put("_id", "1");

    final MongoClientDeleteResult result = Mockito.mock(MongoClientDeleteResult.class);
    when(result.getRemovedCount()).thenReturn(1L);

    when(client.removeDocumentObservable("products", query)).thenReturn(Observable.just(result));

    serviceSUT.delete("1").subscribe(numDeleted -> assertEquals(Long.valueOf(1L), numDeleted));

    verify(client).removeDocumentObservable("products", query);
  }

}
