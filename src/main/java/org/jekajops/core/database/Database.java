package org.jekajops.core.database;

import org.jekajops.core.context.Context;
import org.jekajops.core.database.wrappers.DatabaseCollectionWrapper;
import org.jekajops.core.database.wrappers.DatabaseObjectWrapper;
import org.jekajops.core.database.wrappers.DatabaseVoidWrapper;
import org.jekajops.core.entities.*;
import org.eclipse.jetty.util.BlockingArrayQueue;
import org.jekajops.core.utils.time.TimeUtil;

import java.sql.*;
import java.util.*;

import static org.jekajops.core.entities.User.Role.USER;

public class Database {
    private final Connection connection;

    public Database() throws SQLException {
        connection = DatabaseConnectionManager.getConnection();
    }

    public void insertPrank(String category, String subcategory, String text, String vkAudioId, String audioUrl) {
        ((DatabaseVoidWrapper) () -> {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO pranks (category, subcategory, text_msg, vkaudio_id, audio_url) VALUES (?,?,?,?,?)");
            ps.setString(1, category);
            ps.setString(2, subcategory);
            ps.setString(3, text);
            ps.setString(4, vkAudioId);
            ps.setString(5, audioUrl);
            ps.executeUpdate();
        }).execute();
    }

    public void updatePrankRating(int id) {
        ((DatabaseVoidWrapper) () -> {
            PreparedStatement ps = connection.prepareStatement("UPDATE pranks SET rating = rating + 1 WHERE id = ?");
            ps.setInt(1, id);
            ps.executeUpdate();
        }).execute();
    }

    public void updatePrankAudioUrl(String vkAudioId, String url) {
        ((DatabaseVoidWrapper) () -> {
            PreparedStatement ps = connection.prepareStatement("UPDATE pranks SET audio_url = ? WHERE vkaudio_id = ?");
            ps.setString(1, url);
            ps.setString(2, vkAudioId);
            ps.executeUpdate();
        }).execute();
    }

    public List<String> getAudios() {
        return (List<String>) ((DatabaseCollectionWrapper<String>) () -> {
            List<String> audios = new ArrayList<>();
            PreparedStatement ps = connection.prepareStatement("SELECT text_msg FROM pranks");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                audios.add(rs.getString("text_msg"));
            }
            return audios;
        }).execute();
    }

    public void deletePrank(int dbId) {
        ((DatabaseVoidWrapper) () -> {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM pranks WHERE id = ?");
            ps.setInt(1, dbId);
            ps.executeUpdate();
        }).execute();
    }

    public void deletePrank(String text) {
        ((DatabaseVoidWrapper) () -> {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM pranks WHERE text_msg = ?");
            ps.setString(1, text);
            ps.executeUpdate();
        }).execute();
    }

    public User insertUser(String userName, int userId) {
        return ((DatabaseObjectWrapper<User>) () -> {
            User user = new User(0, userName, userId, 0, 0);
            PreparedStatement ps = connection.prepareStatement("INSERT INTO users (user_name, user_id, roles) VALUES (?, ?, ?)");
            ps.setString(1, userName);
            ps.setInt(2, userId);
            ps.setString(3, USER.name());
            ps.executeUpdate();
            ResultSet iRs = connection.prepareStatement("SELECT id FROM users WHERE user_id = " + userId).executeQuery();
            if (iRs.next()) {
                user.setId(iRs.getInt(1));
            }
            return user;
        }).execute();
    }

    public void updateUserBalance(int userId, double balance) {
        ((DatabaseVoidWrapper) () -> {
            PreparedStatement ps = connection.prepareStatement("UPDATE users SET pranks_available = ?, balance = ? WHERE user_id = ?");
            ps.setInt(1, (int) (balance / Context.SETTINGS.PRANK_COST.getDATA()));
            ps.setDouble(2, balance);
            ps.setInt(3, userId);
            ps.executeUpdate();
        }).execute();
    }

    public void updateUserRoles(int userId, Set<User.Role> roles) {
        ((DatabaseVoidWrapper) () -> {
            PreparedStatement ps = connection.prepareStatement("UPDATE users SET roles = ? WHERE user_id = ?");
            String array = getRolesSqlArray(roles);
            ps.setString(1, array);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }).execute();
    }

    public void addUserRole(int userId, User.Role role) {
        ((DatabaseVoidWrapper) () -> {
            PreparedStatement ps = connection.prepareStatement("UPDATE users SET roles = ? WHERE user_id = ?");
            Set<User.Role> roles = getUserRoles(userId);
            roles.add(role);
            String array = getRolesSqlArray(roles);
            ps.setString(1, array);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }).execute();
    }

    public void removeUserRole(int userId, User.Role role) {
        ((DatabaseVoidWrapper) () -> {
            PreparedStatement ps = connection.prepareStatement("UPDATE users SET roles = ? WHERE user_id = ?");
            Set<User.Role> roles = getUserRoles(userId);
            roles.remove(role);
            String array = getRolesSqlArray(roles);
            ps.setString(1, array);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }).execute();
    }

    public List<User> getUsers() {
        return (List<User>) ((DatabaseCollectionWrapper<User>) () -> {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM a0468909_trollbot.users");
            ResultSet rs = ps.executeQuery();
            return constructUsers(rs);
        }).execute();
    }

    public Set<User.Role> getUserRoles(int userId) {
        return (Set<User.Role>) ((DatabaseCollectionWrapper<User.Role>) () -> {
            PreparedStatement ps = connection.prepareStatement("SELECT roles FROM a0468909_trollbot.users WHERE user_id = ?");
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                var strArray = rs.getString("roles");
                return parseRolesFromArray(strArray);
            }
            return Collections.emptySet();
        }).execute();
    }

    private String getRolesSqlArray(Set<User.Role> roles) {
        StringBuilder sb = new StringBuilder();
        roles.forEach(role -> sb.append(role.name()).append(","));
        sb.deleteCharAt(sb.lastIndexOf(","));
        return sb.toString();
    }

    private Set<User.Role> parseRolesFromArray(String array) {
        Set<User.Role> roles = new HashSet<>();
        for (String string : array.split(",")) {
            roles.add(User.Role.valueOf(string));
        }
        return roles;
    }

    private User constructUser(ResultSet rs) throws SQLException {
        if (rs.next()) {
            return new User(
                    rs.getInt("id"),
                    rs.getString("user_name"),
                    rs.getInt("user_id"),
                    rs.getInt("pranks_available"),
                    rs.getInt("balance"),
                    parseRolesFromArray(rs.getString("roles"))
            );
        }
        return null;
    }

    private List<User> constructUsers(ResultSet rs) throws SQLException {
        List<User> users = new ArrayList<>();
        while (rs.next()) {
            users.add(
                    new User(
                            rs.getInt("id"),
                            rs.getString("user_name"),
                            rs.getInt("user_id"),
                            rs.getInt("pranks_available"),
                            rs.getInt("balance"),
                            parseRolesFromArray(rs.getString("roles")))
            );
        }
        return users;
    }

    private User getUser(PreparedStatement ps, int id) throws SQLException {
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        return constructUser(rs);
    }

    public User getUserByUserId(int userId) {
        return ((DatabaseObjectWrapper<User>) () -> {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM users WHERE user_id = ?");
            return getUser(ps, userId);
        }).execute();
    }

    public User getUserByDbId(int dbId) {
        return ((DatabaseObjectWrapper<User>) () -> {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM users WHERE id = ?");
            return getUser(ps, dbId);
        }).execute();
    }

    public void deleteUser(int userId) {
        ((DatabaseVoidWrapper) () -> {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM users WHERE user_id = ?");
            ps.setInt(1, userId);
            ps.executeUpdate();
        }).execute();
    }

    public void insertOrder(int prankId, int userId, String phone) {
        ((DatabaseVoidWrapper) () -> {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO orders (prank_id, user_id, phone, start_time) VALUES (?,?,?,NOW())");
            ps.setInt(1, prankId);
            ps.setInt(2, userId);
            ps.setString(3, phone);
            ps.executeUpdate();
        }).execute();
    }

    public void updateOrderCallId(int id, String callId) {
        ((DatabaseVoidWrapper) () -> {
            PreparedStatement ps = connection.prepareStatement("UPDATE orders SET call_id = ? WHERE id = ?");
            ps.setString(1, callId);
            ps.setInt(2, id);
            ps.executeUpdate();
        }).execute();
    }

    synchronized private Queue<Order> constructOrders(PreparedStatement statement) throws SQLException {
        ResultSet rs = statement.executeQuery();
        Queue<Order> orders = new BlockingArrayQueue<>();
        while (rs.next()) {

            long time = Objects.requireNonNullElse(rs.getTimestamp("start_time"), new Timestamp(0)).getTime() - 3 * TimeUtil.HOUR;
            orders.add(new Order(
                    rs.getInt("id"),
                    rs.getInt("prank_id"),
                    rs.getInt("user_id"),
                    rs.getString("phone"),
                    rs.getString("call_Id"),
                    time)
            );
        }
        return orders;
    }

    synchronized public Queue<Order> getOrdersQueueInProcess() {
        return (Queue<Order>) ((DatabaseCollectionWrapper<Order>) () -> {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM orders WHERE call_id IS NOT NULL and record IS NULL");
            return constructOrders(ps);
        }).execute();
    }

    synchronized public Queue<Order> getOrdersQueueNeedCall() {
        return (Queue<Order>) ((DatabaseCollectionWrapper<Order>) () -> {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM orders WHERE call_id IS NULL");
            return constructOrders(ps);
        }).execute();
    }

    synchronized public Queue<Order> getOrders() {
        return (Queue<Order>) ((DatabaseCollectionWrapper<Order>) () -> {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM orders");
            return constructOrders(ps);
        }).execute();
    }

    public void deleteOrder(int id) {
        ((DatabaseVoidWrapper) () -> {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM orders WHERE id = ?");
            ps.setInt(1, id);
            ps.executeUpdate();
        }).execute();
    }

    public void insertSetting(String key, String value) {
        ((DatabaseVoidWrapper) () -> {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO settings (_key, setting) VALUES (?,?)");
            ps.setString(1, key);
            ps.setString(2, value);
            ps.executeUpdate();
        }).execute();
    }

    public String getSetting(String key) {
        return ((DatabaseObjectWrapper<String>) () -> {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM settings WHERE _key = ?");
            ps.setString(1, key);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String setting = rs.getString("setting");
                if (setting == null) setting = rs.getString("blob_setting");
                return setting;
            }
            return null;
        }).execute();
    }

    public void updateSetting(String key, String value) {
        ((DatabaseVoidWrapper) () -> {
            PreparedStatement ps = connection.prepareStatement("UPDATE settings SET setting = ? WHERE _key = ?");
            ps.setString(1, value);
            ps.setString(2, key);
            ps.executeUpdate();
        }).execute();
    }

    public void updateBlobSetting(String key, String value) {
        ((DatabaseVoidWrapper) () -> {
            PreparedStatement ps = connection.prepareStatement("UPDATE settings SET blob_setting = ? WHERE _key = ?");
            ps.setString(1, value);
            ps.setString(2, key);
            ps.executeUpdate();
        }).execute();
    }

    public void insertCategory(String category, String subcategory) {
        ((DatabaseVoidWrapper) () -> {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO categories (category, subcategory) VALUES (?,?)");
            ps.setString(1, category);
            ps.setString(2, subcategory);
            ps.executeUpdate();
        }).execute();
    }

    public Categories getCategories() {
        return ((DatabaseObjectWrapper<Categories>) () -> {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM categories");
            ResultSet rs = ps.executeQuery();
            Categories categories = new Categories();
            while (rs.next()) {
                String category = rs.getString("category");
                String subcategory = rs.getString("subcategory");
                categories.add(category, subcategory);
            }
            return categories;
        }).execute();
    }

    public void deleteCategory(String category, String subcategory) {
        ((DatabaseVoidWrapper) () -> {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM categories WHERE (category,subcategory) = (?,?) ");
            ps.setString(1, category);
            ps.setString(2, subcategory);
            ps.executeUpdate();
        }).execute();
    }

}
