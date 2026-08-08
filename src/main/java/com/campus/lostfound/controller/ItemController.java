package com.campus.lostfound.controller;

import com.campus.lostfound.dto.CreateItemRequest;
import com.campus.lostfound.dto.ItemResponse;
import com.campus.lostfound.model.Item;
import com.campus.lostfound.service.ItemService;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

import java.util.List;
import java.util.stream.Collectors;

public class ItemController {
    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    public void createItem(Context ctx) {
        CreateItemRequest request = ctx.bodyAsClass(CreateItemRequest.class);
        Item created = itemService.createItem(request);
        ctx.status(HttpStatus.CREATED);
        ctx.json(new ItemResponse(created));
    }

    public void getItemById(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class).get();
        Item item = itemService.getItem(id);
        ctx.json(new ItemResponse(item));
    }

    public void getItems(Context ctx) {
        String type = ctx.queryParam("type");
        String location = ctx.queryParam("location");
        List<Item> items = itemService.getItems(type, location);
        List<ItemResponse> response = items.stream()
                .map(ItemResponse::new)
                .collect(Collectors.toList());
        ctx.json(response);
    }

    public void searchItems(Context ctx) {
        String keyword = ctx.queryParam("keyword");
        List<Item> items = itemService.searchItems(keyword);
        List<ItemResponse> response = items.stream()
                .map(ItemResponse::new)
                .collect(Collectors.toList());
        ctx.json(response);
    }

    public void resolveItem(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class).get();
        itemService.resolveItem(id);
        ctx.json("{\"message\":\"Item marked as resolved.\"}");
    }

    public void deleteItem(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class).get();
        itemService.deleteItem(id);
        ctx.status(HttpStatus.NO_CONTENT);
    }
}
