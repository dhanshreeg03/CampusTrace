package com.campus.lostfound;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.campus.lostfound.config.DatabaseConfig;
import com.campus.lostfound.controller.ItemController;
import com.campus.lostfound.exception.GlobalExceptionHandler;
import com.campus.lostfound.repository.DatabaseItemRepository;
import com.campus.lostfound.repository.ItemRepository;
import com.campus.lostfound.service.ItemService;
import io.javalin.Javalin;
import io.javalin.json.JavalinJackson;

public class Main {
    public static void main(String[] args) {
        // Configure Jackson ObjectMapper for Date/Time support
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        Javalin app = Javalin.create(config -> {
            // Configure JSON Mapper
            config.jsonMapper(new JavalinJackson(objectMapper, false));
            
            // Serve static files from the classpath (/public directory)
            config.staticFiles.add(staticFilesConfig -> {
                staticFilesConfig.directory = "/public";
                staticFilesConfig.location = io.javalin.http.staticfiles.Location.CLASSPATH;
            });

            // Serve uploaded images from local external uploads directory
            java.io.File uploadsFolder = new java.io.File("uploads");
            if (!uploadsFolder.exists()) {
                uploadsFolder.mkdirs();
            }
            config.staticFiles.add(staticFilesConfig -> {
                staticFilesConfig.hostedPath = "/uploads";
                staticFilesConfig.directory = "uploads";
                staticFilesConfig.location = io.javalin.http.staticfiles.Location.EXTERNAL;
            });

            // Enable CORS for frontend API calls from other ports/domains during development
            config.bundledPlugins.enableCors(cors -> cors.addRule(rule -> rule.anyHost()));
        });

        // Initialize Layers
        ItemRepository repository = new DatabaseItemRepository();
        ItemService service = new ItemService(repository);
        ItemController controller = new ItemController(service);

        // Register Exception Handling
        GlobalExceptionHandler.register(app);

        // API Endpoints
        app.get("/api/health", ctx -> ctx.json("{\"status\":\"UP\"}"));
        
        // Items API
        app.post("/api/items", controller::createItem);
        app.get("/api/items", controller::getItems);
        app.get("/api/items/search", controller::searchItems);
        app.get("/api/items/{id}", controller::getItemById);
        app.put("/api/items/{id}/resolve", controller::resolveItem);
        app.delete("/api/items/{id}", controller::deleteItem);

        // Start Server
        int port = 7070;
        String portEnv = System.getenv("PORT");
        if (portEnv != null && !portEnv.trim().isEmpty()) {
            try {
                port = Integer.parseInt(portEnv.trim());
            } catch (NumberFormatException ignored) {}
        }
        
        app.start(port);
        System.out.println("Campus Lost & Found Server started on port " + port);
    }
}
