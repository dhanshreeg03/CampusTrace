package com.campus.lostfound.service;

import com.campus.lostfound.model.Item;
import com.campus.lostfound.model.Status;
import com.campus.lostfound.model.Type;
import com.campus.lostfound.repository.ItemRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class FakeItemRepository implements ItemRepository {
    private final Map<Integer, Item> database = new HashMap<>();
    private int sequence = 1;

    @Override
    public Item save(Item item) {
        if (item.getId() == null) {
            item.setId(sequence++);
        }
        if (item.getCreatedAt() == null) {
            item.setCreatedAt(LocalDateTime.now());
        }
        database.put(item.getId(), item);
        return item;
    }

    @Override
    public Optional<Item> findById(int id) {
        return Optional.ofNullable(database.get(id));
    }

    @Override
    public List<Item> findByFilters(Type type, String location) {
        return database.values().stream()
                .filter(item -> item.getStatus() == Status.OPEN)
                .filter(item -> type == null || item.getType() == type)
                .filter(item -> location == null || location.trim().isEmpty() || item.getLocation().equalsIgnoreCase(location.trim()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findByFilters(null, null);
        }
        String lower = keyword.trim().toLowerCase();
        return database.values().stream()
                .filter(item -> item.getStatus() == Status.OPEN)
                .filter(item -> item.getName().toLowerCase().contains(lower) || item.getDescription().toLowerCase().contains(lower))
                .collect(Collectors.toList());
    }

    @Override
    public boolean updateStatus(int id, Status status) {
        Item item = database.get(id);
        if (item != null) {
            item.setStatus(status);
            return true;
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
        return database.remove(id) != null;
    }
}
