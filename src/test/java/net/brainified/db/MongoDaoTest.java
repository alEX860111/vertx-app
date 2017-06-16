package net.brainified.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
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
public class MongoDaoTest {

  private static final String CREATED_AT_DATE = "2016-12-28T22:10:10.280Z";

  private static final String ID = "58643842aa03f417ee37076f";

  private static final String COLLECTION_NAME = "objects";

  @Mock
  private MongoClient client;

  private MongoDao<MongoObject> dao;

  private JsonObject document;

  @Before
  public void setUp() {
    dao = new MongoDao<MongoObject>(client, COLLECTION_NAME, MongoObject.class);
    document = new JsonObject().put("_id", ID).put("createdAt", CREATED_AT_DATE);
  }

  @Test
  public void testGetList() {
    final JsonObject query = new JsonObject();

    final ArgumentCaptor<FindOptions> optionsCaptor = ArgumentCaptor.forClass(FindOptions.class);

    when(client.countObservable(COLLECTION_NAME, query)).thenReturn(Observable.just(42L));

    when(client.findWithOptionsObservable(eq(COLLECTION_NAME), eq(query), optionsCaptor.capture()))
        .thenReturn(Observable.just(Arrays.asList(document)));

    dao.getList(1, 10, "createdAt", SortOrder.DESC).subscribe(container -> {
      assertEquals(Long.valueOf(42L), container.getCount());

      List<MongoObject> objects = container.getItems();
      assertEquals(1, objects.size());
      final MongoObject object = objects.get(0);
      assertEquals(ID, object.get_id());
      assertEquals(CREATED_AT_DATE, object.getCreatedAt());
    });

    verify(client).findWithOptionsObservable(eq(COLLECTION_NAME), eq(query), optionsCaptor.capture());

    final FindOptions options = optionsCaptor.getValue();
    assertEquals(10, options.getLimit());
    assertEquals(0, options.getSkip());

    final JsonObject sort = new JsonObject().put("createdAt", -1);
    assertEquals(sort, options.getSort());
  }

  @Test
  public void testGetById() {
    final JsonObject query = new JsonObject().put("_id", ID);

    final JsonObject fields = new JsonObject();

    when(client.findOneObservable(COLLECTION_NAME, query, fields)).thenReturn(Observable.just(document));

    dao.getById(ID).subscribe(objectOptional -> {
      assertTrue(objectOptional.isPresent());
      final MongoObject object = objectOptional.get();
      assertEquals(ID, object.get_id());
      assertEquals(CREATED_AT_DATE, object.getCreatedAt());
    });

    verify(client).findOneObservable(COLLECTION_NAME, query, fields);
  }

  @Test
  public void testGetById_Empty() {
    final JsonObject query = new JsonObject().put("_id", ID);

    final JsonObject fields = new JsonObject();

    when(client.findOneObservable(COLLECTION_NAME, query, fields)).thenReturn(Observable.just(null));

    dao.getById(ID).subscribe(objectOptional -> {
      assertFalse(objectOptional.isPresent());
    });

    verify(client).findOneObservable(COLLECTION_NAME, query, fields);
  }

  @Test
  public void testAdd() {
    final ArgumentCaptor<JsonObject> documentCaptor = ArgumentCaptor.forClass(JsonObject.class);

    when(client.insertObservable(eq(COLLECTION_NAME), any(JsonObject.class))).thenReturn(Observable.just(ID));

    final MongoObject object = new MongoObject();
    object.set_id("some value");
    object.setCreatedAt(CREATED_AT_DATE);

    dao.add(object).subscribe(savedObject -> {
      assertSame(object, savedObject);
      assertEquals(ID, savedObject.get_id());
      assertFalse(savedObject.getCreatedAt().isEmpty());
      assertFalse(savedObject.getCreatedAt().equals(CREATED_AT_DATE));
    });

    verify(client).insertObservable(eq(COLLECTION_NAME), documentCaptor.capture());
    final JsonObject savedDocument = documentCaptor.getValue();
    assertFalse(savedDocument.containsKey("_id"));
    assertTrue(savedDocument.containsKey("createdAt"));
  }

  @Test
  public void testUpdate() {
    final JsonObject query = new JsonObject().put("_id", ID);

    final JsonObject document = new JsonObject();
    final JsonObject update = new JsonObject().put("$set", document);

    final MongoClientUpdateResult result = Mockito.mock(MongoClientUpdateResult.class);
    when(result.getDocMatched()).thenReturn(1L);

    when(client.updateCollectionObservable(COLLECTION_NAME, query, update)).thenReturn(Observable.just(result));

    final MongoObject object = new MongoObject();
    object.set_id(ID);
    object.setCreatedAt(CREATED_AT_DATE);
    dao.update(object).subscribe(updated -> assertTrue(updated));

    verify(client).updateCollectionObservable(COLLECTION_NAME, query, update);
  }

  @Test
  public void testDeleteProduct() {
    final JsonObject query = new JsonObject().put("_id", ID);

    final MongoClientDeleteResult result = Mockito.mock(MongoClientDeleteResult.class);
    when(result.getRemovedCount()).thenReturn(1L);

    when(client.removeDocumentObservable(COLLECTION_NAME, query)).thenReturn(Observable.just(result));

    dao.delete(ID).subscribe(deleted -> assertTrue(deleted));

    verify(client).removeDocumentObservable(COLLECTION_NAME, query);
  }

}
