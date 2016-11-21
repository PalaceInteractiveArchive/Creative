package us.mcmagic.creative.utils;

import us.mcmagic.creative.handlers.PlayerData;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.particles.ParticleEffect;

import java.sql.*;
import java.util.UUID;

/**
 * Created by Marc on 11/16/15
 */
public class SqlUtil {

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(MCMagicCore.getMCMagicConfig().sqlConnectionUrl,
                MCMagicCore.getMCMagicConfig().sqlUser, MCMagicCore.getMCMagicConfig().sqlPassword);
    }

    public PlayerData login(UUID uuid) {
        try (Connection connection = getConnection()) {
            PreparedStatement sql = connection.prepareStatement("SELECT * FROM creative WHERE uuid=?");
            sql.setString(1, uuid.toString());
            ResultSet result = sql.executeQuery();
            if (!result.next()) {
                result.close();
                sql.close();
                return createData(uuid);
            }
            PlayerData data = new PlayerData(uuid, ParticleEffect.fromString(result.getString("particle")),
                    result.getInt("rptag") == 1, result.getInt("showcreator") == 1, result.getInt("rplimit"),
                    result.getInt("creator") == 1, result.getInt("creatortag") == 1);
            result.close();
            sql.close();
            return data;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private PlayerData createData(UUID uuid) {
        try (Connection connection = getConnection()) {
            PreparedStatement sql = connection.prepareStatement("INSERT INTO creative (id,uuid) VALUES (0,?)");
            sql.setString(1, uuid.toString());
            sql.execute();
            sql.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new PlayerData(uuid, null, false, false, 5, false, false);
    }
}