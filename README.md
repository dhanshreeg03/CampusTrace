# Campus Lost & Found

A small, polished, and beginner-friendly web application to centralize lost and found reporting for college campuses.

## Problem
Students frequently lose or find items around college campuses (ID cards, wallets, books, electronics). Currently, they rely on fragmented communication like WhatsApp groups, friends, or campus security. There is no centralized, structured place to report, search, and manage lost and found items.

## Solution
This **Campus Lost & Found** application provides a centralized web platform where anyone can quickly report a lost or found item, browse active reports, search by keywords, filter by locations, and mark their item as resolved once it has been returned.

## Features
* **Report Lost Items**: Submit details of a lost item (name, description, location, date, contact).
* **Report Found Items**: Submit details of an item you found.
* **Browse Items**: View active reports in a clean, modern grid layout.
* **Search**: Find specific items quickly using a case-insensitive keyword search on the title or description.
* **Filter**: Narrow down reports by item type (LOST/FOUND) or location (Library, Canteen, Classroom, etc.).
* **Resolve Items**: Mark items as resolved to remove them from the active listings once reunited with the owner.

## Tech Stack
* **Backend**: Java 17, Javalin 6.1.3, JDBC, Maven
* **Database**: MySQL 8.x
* **Frontend**: HTML5, CSS3 (Premium Glassmorphism Design), Minimal Vanilla JavaScript
* **Testing**: JUnit 5, Mockito, Postman

## Architecture
```
Browser
  ↓ (HTML/CSS + minimal JS)
Javalin REST API
  ↓
Service Layer (ItemService)
  ↓
Repository Layer (DatabaseItemRepository using JDBC)
  ↓
MySQL Database (items table)
```

## API Documentation

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/health` | Health check endpoint |
| `POST` | `/api/items` | Create a new lost or found item report |
| `GET` | `/api/items` | Get all active (OPEN) items. Supports `?type=` and `?location=` query filters |
| `GET` | `/api/items/{id}` | Get a specific item by its ID |
| `GET` | `/api/items/search` | Search for items by title or description (e.g. `?keyword=wallet`) |
| `PUT` | `/api/items/{id}/resolve` | Mark an item as RESOLVED |
| `DELETE` | `/api/items/{id}` | Delete an item by ID |

## Database
The application relies on a single relational table.
```sql
CREATE TABLE items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    location VARCHAR(255) NOT NULL,
    date DATE NOT NULL,
    type ENUM('LOST', 'FOUND') NOT NULL,
    status ENUM('OPEN', 'RESOLVED') NOT NULL DEFAULT 'OPEN',
    contact VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_type (type),
    INDEX idx_status (status),
    INDEX idx_location (location)
);
```

## Setup Instructions

1. **Create the Database**
   Open your MySQL client and run:
   ```sql
   CREATE DATABASE campus_lostfound;
   ```
2. **Run schema.sql**
   Execute the `schema.sql` script located in the project root to create the `items` table.
3. **Configure MySQL**
   By default, the app expects MySQL at `localhost:3306` with user `root` and password `root`. 
   You can override these by setting environment variables before running:
   `DB_URL`, `DB_USER`, `DB_PASSWORD`.
4. **Run Maven**
   Compile and run the tests:
   ```bash
   mvn clean test
   ```
5. **Run the Application**
   Run the `Main.java` class directly through your IDE or execute via maven:
   ```bash
   mvn exec:java -Dexec.mainClass="com.campus.lostfound.Main"
   ```
   (Wait for the console message: `Campus Lost & Found Server started on port 7070`)
6. **Open the Application**
   Navigate to [http://localhost:7070/](http://localhost:7070/) in your browser.
7. **Test Using Postman**
   Import the `LostFound_Postman_Collection.json` into Postman to test the backend API completely independently of the frontend.

## Future Improvements
* User authentication and accounts
* Image uploads for items
* Email notifications for matches
* Admin verification roles
* Automatic lost/found matching algorithms
* Interactive location maps
* Claim requests functionality
* Detailed user profiles
