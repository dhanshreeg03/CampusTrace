package com.campus.lostfound.repository;

import com.campus.lostfound.model.Item;
import com.campus.lostfound.model.Type;
import com.campus.lostfound.model.Status;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    Item save(Item item);
    Optional<Item> findById(int id);
    List<Item> findByFilters(Type type, String location);
    List<Item> search(String keyword);
    boolean updateStatus(int id, Status status);
    boolean delete(int id);
}
