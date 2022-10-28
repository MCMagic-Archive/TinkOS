package us.mcmagic.tinkos.utils;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;
import net.md_5.bungee.config.Configuration;
import us.mcmagic.tinkos.TinkOS;
import us.mcmagic.tinkos.handlers.AddressBan;
import us.mcmagic.tinkos.handlers.Ban;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by Marc on 7/14/16
 */
public class SqlUtil {
    BoneCP connectionPool = null;

    public SqlUtil() {
        BoneCPConfig config = new BoneCPConfig();
        Configuration cfg = TinkOS.getConfig();
        String address = cfg.getString("sql.address");
        String database = cfg.getString("sql.database");
        String username = cfg.getString("sql.username");
        String password = cfg.getString("sql.password");
        config.setJdbcUrl("jdbc:mysql://" + address + ":3306/" + database);
        config.setUsername(username);
        config.setPassword(password);
        config.setMinConnectionsPerPartition(30);
        config.setMaxConnectionsPerPartition(100);
        config.setPartitionCount(3);
        config.setIdleConnectionTestPeriod(600, TimeUnit.SECONDS);
        try {
            connectionPool = new BoneCP(config);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() throws SQLException {
        return connectionPool.getConnection();
    }

    public void stop() {
        connectionPool.shutdown();
    }

    /**
     * Ban methods
     */

    public boolean isBanned(UUID uuid) {
        try (Connection connection = getConnection()) {
            PreparedStatement sql = connection.prepareStatement("SELECT active FROM banned_players WHERE uuid=?;");
            sql.setString(1, uuid.toString());
            ResultSet result = sql.executeQuery();
            boolean banned = false;
            while (result.next()) {
                if (result.getInt("active") == 1) {
                    banned = true;
                    break;
                }
            }
            result.close();
            sql.close();
            return banned;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public AddressBan getAddressBan(String address) {
        try (Connection connection = getConnection()) {
            PreparedStatement sql = connection.prepareStatement("SELECT reason,source FROM banned_ips WHERE ipAddress=? AND active=1;");
            sql.setString(1, address);
            ResultSet result = sql.executeQuery();
            AddressBan ban = null;
            if (!result.next()) {
                return null;
            }
            ban = new AddressBan(address, result.getString("reason"), result.getString("source"));
            result.close();
            sql.close();
            return ban;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Ban getBan(UUID uuid, String name) {
        try (Connection connection = getConnection()) {
            PreparedStatement sql = connection.prepareStatement("SELECT permanent,`release`,reason,source FROM banned_players WHERE uuid=? AND active=1;");
            sql.setString(1, uuid.toString());
            ResultSet result = sql.executeQuery();
            Ban ban = null;
            if (!result.next()) {
                return null;
            }
            ban = new Ban(uuid, name, result.getInt("permanent") == 1, result.getTimestamp("release").getTime(),
                    result.getString("reason"), result.getString("source"));
            result.close();
            sql.close();
            return ban;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void unbanPlayer(UUID uuid) {
        try (Connection connection = getConnection()) {
            PreparedStatement sql = connection.prepareStatement("UPDATE banned_players SET active=0 WHERE uuid=?");
            sql.setString(1, uuid.toString());
            sql.execute();
            sql.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}