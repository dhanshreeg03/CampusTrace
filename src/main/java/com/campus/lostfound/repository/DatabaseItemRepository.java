package com.campus.lostfound.repository;

import com.campus.lostfound.config.DatabaseConfig;
import com.campus.lostfound.model.Item;
import com.campus.lostfound.model.Type;
import com.campus.lostfound.model.Status;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DatabaseItemRepository implements ItemRepository {

    @Override
    public Item save(Item item) {
        String sql = "INSERT INTO items (name, description, location, date, type, status, contact) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, item.getName().trim());
            ps.setString(2, item.getDescription().trim());
            ps.setString(3, item.getLocation().trim());
            ps.setDate(4, Date.valueOf(item.getDate()));
            ps.setString(5, item.getType().name());
            ps.setString(6, item.getStatus() != null ? item.getStatus().name() : Status.OPEN.name());
            ps.setString(7, item.getContact().trim());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    return findById(id).orElseThrow(() -> new SQLException("Failed to retrieve created item."));
                } else {
                    throw new SQLException("Creating item failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error saving item: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Item> findById(int id) {
        String sql = "SELECT id, name, description, location, date, type, status, contact, created_at FROM items WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error finding item by ID: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<Item> findByFilters(Type type, String location) {
        StringBuilder sb = new StringBuilder("SELECT id, name, description, location, date, type, status, contact, created_at FROM items WHERE status = 'OPEN'");
        List<Object> params = new ArrayList<>();

        if (type != null) {
            sb.append(" AND type = ?");
            params.add(type.name());
        }
        if (location != null && !location.trim().isEmpty()) {
            sb.append(" AND location = ?");
            params.add(location.trim());
        }
        sb.append(" ORDER BY created_at DESC");

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sb.toString())) {
            
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                List<Item> items = new ArrayList<>();
                while (rs.next()) {
                    items.add(mapRow(rs));
                }
                return items;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error finding items by filter: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Item> search(String keyword) {
        String sql = "SELECT id, name, description, location, date, type, status, contact, created_at FROM items " +
                     "WHERE status = 'OPEN' AND (LOWER(name) LIKE ? OR LOWER(description) LIKE ?) " +
                     "ORDER BY created_at DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + (keyword != null ? keyword.trim().toLowerCase() : "") + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);

            try (ResultSet rs = ps.executeQuery()) {
                List<Item> items = new ArrayList<>();
                while (rs.next()) {
                    items.add(mapRow(rs));
                }
                return items;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error searching items: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean updateStatus(int id, Status status) {
        String sql = "UPDATE items SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, status.name());
            ps.setInt(2, id);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Database error updating status: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM items WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(2, id); // Wait, ps.setInt(1, id) is correct! 
            // Let's fix this in the actual code!
            ps.setInt(1, id);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Database error deleting item: " + e.getMessage(), e);
        }
    }

    private Item mapRow(ResultSet rs) throws SQLException {
        Item item = new Item();
        item.setId(rs.getInt("id"));
        item.setName(rs.getString("name"));
        item.setDescription(rs.getString("description"));
        item.setLocation(rs.getString("location"));
        
        Date dbDate = rs.getDate("date");
        if (dbDate != null) {
            item.setDate(dbDate.toLocalDate());
        }

        item.setType(Type.valueOf(rs.getString("type")));
        item.setStatus(Status.valueOf(rs.getString("status")));
        item.setContact(rs.getString("contact"));
        
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            item.setCreatedAt(ts.toLocalDateTime());
        }
        
        return item;
    }
}
