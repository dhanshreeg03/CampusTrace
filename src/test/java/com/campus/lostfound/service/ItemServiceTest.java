package com.campus.lostfound.service;

import com.campus.lostfound.dto.CreateItemRequest;
import com.campus.lostfound.exception.ItemAlreadyResolvedException;
import com.campus.lostfound.exception.ItemNotFoundException;
import com.campus.lostfound.model.Item;
import com.campus.lostfound.model.Status;
import com.campus.lostfound.model.Type;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ItemServiceTest {
    private ItemService itemService;
    private FakeItemRepository fakeRepository;

    @BeforeEach
    public void setUp() {
        fakeRepository = new FakeItemRepository();
        itemService = new ItemService(fakeRepository);
    }

    @Test
    public void testCreateLostItem() {
        CreateItemRequest request = new CreateItemRequest(
            "Black Wallet", "Leather wallet with ID", "Library", "2026-08-08", "LOST", "student@example.com"
        );
        Item item = itemService.createItem(request);
        assertNotNull(item.getId());
        assertEquals("Black Wallet", item.getName());
        assertEquals(Type.LOST, item.getType());
        assertEquals(Status.OPEN, item.getStatus());
        assertEquals(LocalDate.parse("2026-08-08"), item.getDate());
    }

    @Test
    public void testCreateFoundItem() {
        CreateItemRequest request = new CreateItemRequest(
            "Keyring", "Keys found near lawn", "Sports Ground", "2026-08-08", "FOUND", "finder@example.com"
        );
        Item item = itemService.createItem(request);
        assertNotNull(item.getId());
        assertEquals("Keyring", item.getName());
        assertEquals(Type.FOUND, item.getType());
        assertEquals(Status.OPEN, item.getStatus());
    }

    @Test
    public void testCreateItemWithBlankName() {
        CreateItemRequest request = new CreateItemRequest(
            "", "Leather wallet with ID", "Library", "2026-08-08", "LOST", "student@example.com"
        );
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            itemService.createItem(request);
        });
        assertEquals("Name cannot be blank", exception.getMessage());
    }

    @Test
    public void testCreateItemWithInvalidType() {
        CreateItemRequest request = new CreateItemRequest(
            "Wallet", "Leather wallet with ID", "Library", "2026-08-08", "NOT_A_TYPE", "student@example.com"
        );
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            itemService.createItem(request);
        });
        assertEquals("Type must be LOST or FOUND", exception.getMessage());
    }

    @Test
    public void testGetItem() {
        CreateItemRequest request = new CreateItemRequest(
            "Water Bottle", "Blue hydroflask", "Canteen", "2026-08-08", "LOST", "student@example.com"
        );
        Item created = itemService.createItem(request);
        
        Item retrieved = itemService.getItem(created.getId());
        assertNotNull(retrieved);
        assertEquals(created.getId(), retrieved.getId());
        assertEquals("Water Bottle", retrieved.getName());
    }

    @Test
    public void testGetItemNotFound() {
        Exception exception = assertThrows(ItemNotFoundException.class, () -> {
            itemService.getItem(999);
        });
        assertEquals("Item not found", exception.getMessage());
    }

    @Test
    public void testSearch() {
        itemService.createItem(new CreateItemRequest("Leather Wallet", "Black leather wallet", "Library", "2026-08-08", "LOST", "s@e.com"));
        itemService.createItem(new CreateItemRequest("Blue Water Bottle", "Metal bottle", "Canteen", "2026-08-08", "FOUND", "f@e.com"));

        List<Item> results = itemService.searchItems("Wallet");
        assertEquals(1, results.size());
        assertEquals("Leather Wallet", results.get(0).getName());
    }

    @Test
    public void testCaseInsensitiveSearch() {
        itemService.createItem(new CreateItemRequest("Leather Wallet", "Black leather wallet", "Library", "2026-08-08", "LOST", "s@e.com"));
        
        List<Item> results = itemService.searchItems("wallet");
        assertEquals(1, results.size());

        List<Item> resultsUpper = itemService.searchItems("WALLET");
        assertEquals(1, resultsUpper.size());
    }

    @Test
    public void testResolveItem() {
        Item created = itemService.createItem(new CreateItemRequest("Wallet", "Black wallet", "Library", "2026-08-08", "LOST", "s@e.com"));
        assertEquals(Status.OPEN, created.getStatus());

        itemService.resolveItem(created.getId());
        Item resolved = itemService.getItem(created.getId());
        assertEquals(Status.RESOLVED, resolved.getStatus());
    }

    @Test
    public void testResolveAlreadyResolvedItem() {
        Item created = itemService.createItem(new CreateItemRequest("Wallet", "Black wallet", "Library", "2026-08-08", "LOST", "s@e.com"));
        itemService.resolveItem(created.getId());

        Exception exception = assertThrows(ItemAlreadyResolvedException.class, () -> {
            itemService.resolveItem(created.getId());
        });
        assertEquals("Item already resolved", exception.getMessage());
    }

    @Test
    public void testDeleteItem() {
        Item created = itemService.createItem(new CreateItemRequest("Wallet", "Black wallet", "Library", "2026-08-08", "LOST", "s@e.com"));
        assertNotNull(itemService.getItem(created.getId()));

        itemService.deleteItem(created.getId());
        assertThrows(ItemNotFoundException.class, () -> {
            itemService.getItem(created.getId());
        });
    }
}
