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
        CreateItemRequest request;
        String contentType = ctx.contentType();

        if (contentType != null && contentType.toLowerCase().contains("multipart/form-data")) {
            request = new CreateItemRequest();
            request.setName(ctx.formParam("name"));
            request.setDescription(ctx.formParam("description"));
            request.setLocation(ctx.formParam("location"));
            request.setDate(ctx.formParam("date"));
            request.setType(ctx.formParam("type"));
            request.setContact(ctx.formParam("contact"));

            io.javalin.http.UploadedFile uploadedFile = ctx.uploadedFile("photo");
            if (uploadedFile != null && uploadedFile.size() > 0) {
                // Validate size (5 MB max)
                if (uploadedFile.size() > 5 * 1024 * 1024) {
                    ctx.status(HttpStatus.BAD_REQUEST);
                    ctx.json(java.util.Map.of("success", false, "message", "Image size must be less than 5 MB"));
                    return;
                }

                // Validate file type
                String fileContentType = uploadedFile.contentType();
                String filename = uploadedFile.filename() != null ? uploadedFile.filename().toLowerCase() : "";
                boolean isValidType = (fileContentType != null && (
                        fileContentType.equalsIgnoreCase("image/jpeg") ||
                        fileContentType.equalsIgnoreCase("image/jpg") ||
                        fileContentType.equalsIgnoreCase("image/png") ||
                        fileContentType.equalsIgnoreCase("image/webp")
                )) || (
                        filename.endsWith(".jpg") || filename.endsWith(".jpeg") ||
                        filename.endsWith(".png") || filename.endsWith(".webp")
                );

                if (!isValidType) {
                    ctx.status(HttpStatus.BAD_REQUEST);
                    ctx.json(java.util.Map.of("success", false, "message", "Only JPG, PNG and WEBP images are allowed"));
                    return;
                }

                String ext = ".jpg";
                if (filename.endsWith(".png")) ext = ".png";
                else if (filename.endsWith(".webp")) ext = ".webp";
                else if (filename.endsWith(".jpeg")) ext = ".jpeg";

                String uniqueFilename = "item_" + java.util.UUID.randomUUID().toString().substring(0, 8) + ext;
                java.io.File uploadDir = new java.io.File("uploads");
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }

                java.io.File destFile = new java.io.File(uploadDir, uniqueFilename);
                try (var is = uploadedFile.content(); var os = new java.io.FileOutputStream(destFile)) {
                    is.transferTo(os);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to save uploaded file: " + e.getMessage(), e);
                }

                request.setPhotoPath("/uploads/" + uniqueFilename);
            }
        } else {
            request = ctx.bodyAsClass(CreateItemRequest.class);
        }

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
