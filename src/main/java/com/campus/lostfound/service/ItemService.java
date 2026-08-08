package com.campus.lostfound.service;

import com.campus.lostfound.dto.CreateItemRequest;
import com.campus.lostfound.exception.ItemAlreadyResolvedException;
import com.campus.lostfound.exception.ItemNotFoundException;
import com.campus.lostfound.model.Item;
import com.campus.lostfound.model.Status;
import com.campus.lostfound.model.Type;
import com.campus.lostfound.repository.ItemRepository;

import java.time.LocalDate;
import java.util.List;

public class ItemService {
    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public Item createItem(CreateItemRequest request) {
        validateCreateRequest(request);

        Item item = new Item();
        item.setName(request.getName().trim());
        item.setDescription(request.getDescription().trim());
        item.setLocation(request.getLocation().trim());
        item.setDate(LocalDate.parse(request.getDate().trim()));
        item.setType(Type.valueOf(request.getType().toUpperCase().trim()));
        item.setStatus(Status.OPEN);
        item.setContact(request.getContact().trim());

        return itemRepository.save(item);
    }

    public Item getItem(int id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Item not found"));
    }

    public List<Item> getItems(String typeStr, String location) {
        Type type = null;
        if (typeStr != null && !typeStr.trim().isEmpty()) {
            try {
                type = Type.valueOf(typeStr.toUpperCase().trim());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Type must be LOST or FOUND");
            }
        }
        return itemRepository.findByFilters(type, location);
    }

    public List<Item> searchItems(String keyword) {
        return itemRepository.search(keyword);
    }

    public void resolveItem(int id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Item not found"));
        
        if (item.getStatus() == Status.RESOLVED) {
            throw new ItemAlreadyResolvedException("Item already resolved");
        }

        itemRepository.updateStatus(id, Status.RESOLVED);
    }

    public void deleteItem(int id) {
        itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Item not found"));
        
        itemRepository.delete(id);
    }

    private void validateCreateRequest(CreateItemRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be blank");
        }
        if (request.getDescription() == null || request.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be blank");
        }
        if (request.getLocation() == null || request.getLocation().trim().isEmpty()) {
            throw new IllegalArgumentException("Location cannot be blank");
        }
        if (request.getContact() == null || request.getContact().trim().isEmpty()) {
            throw new IllegalArgumentException("Contact cannot be blank");
        }
        if (request.getType() == null || request.getType().trim().isEmpty()) {
            throw new IllegalArgumentException("Type must be LOST or FOUND");
        }
        try {
            Type.valueOf(request.getType().toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Type must be LOST or FOUND");
        }

        if (request.getDate() == null || request.getDate().trim().isEmpty()) {
            throw new IllegalArgumentException("Date must be valid");
        }
        try {
            LocalDate.parse(request.getDate().trim());
        } catch (Exception e) {
            throw new IllegalArgumentException("Date must be valid");
        }
    }
}
