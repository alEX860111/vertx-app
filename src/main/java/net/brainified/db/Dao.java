package net.brainified.db;

import java.util.Optional;

import rx.Single;

public interface Dao<T> {

  Single<ItemContainer<T>> getList(Integer page, Integer perpage, String sortKey, SortOrder sortOrder);

  Single<Optional<T>> getById(String id);

  Single<Optional<T>> getByKey(String key, String value);

  Single<T> add(T object);

  Single<Boolean> update(T object);

  Single<Boolean> delete(String id);

}