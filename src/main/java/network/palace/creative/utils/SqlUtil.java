package network.palace.creative.utils;

import com.comphenix.protocol.wrappers.EnumWrappers;
import network.palace.core.Core;
import network.palace.creative.handlers.PlayerData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

/**
 * Created by Marc on 11/16/15
 */
public class SqlUtil {

    public PlayerData login(UUID uuid) {
        try (Connection connection = Core.getSqlUtil().getConnection()) {
            PreparedStatement sql = connection.prepareStatement("SELECT * FROM creative WHERE uuid=?");
            sql.setString(1, uuid.toString());
            ResultSet result = sql.executeQuery();
            if (!result.next()) {
                result.close();
                sql.close();
                return createData(uuid);
            }
            PlayerData data = new PlayerData(uuid, EnumWrappers.Particle.getByName(result.getString("particle")),
                    result.getInt("rptag") == 1, result.getInt("showcreator") == 1,
                    result.getInt("rplimit"), result.getInt("creator") == 1,
                    result.getInt("creatortag") == 1, result.getString("resourcepack"));
            result.close();
            sql.close();
            return data;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private PlayerData createData(UUID uuid) {
        try (Connection connection = Core.getSqlUtil().getConnection()) {
            PreparedStatement sql = connection.prepareStatement("INSERT INTO creative (id,uuid) VALUES (0,?)");
            sql.setString(1, uuid.toString());
            sql.execute();
            sql.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new PlayerData(uuid, null, false, false, 5, false, false, "none");
    }

    public UUID getUniqueId(String username) {
        try (Connection connection = Core.getSqlUtil().getConnection()) {
            PreparedStatement sql = connection.prepareStatement("SELECT uuid FROM player_data WHERE username=?");
            sql.setString(1, username);
            ResultSet result = sql.executeQuery();
            if (!result.next()) {
                return null;
            }
            UUID uuid = UUID.fromString(result.getString("uuid"));
            result.close();
            sql.close();
            return uuid;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setResourcePack(UUID uuid, String pack) {
        try (Connection connection = Core.getSqlUtil().getConnection()) {
            PreparedStatement sql = connection.prepareStatement("UPDATE creative SET resourcepack=? WHERE uuid=?");
            sql.setString(1, pack);
            sql.setString(2, uuid.toString());
            sql.execute();
            sql.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}