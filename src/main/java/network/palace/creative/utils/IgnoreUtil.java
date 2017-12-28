package network.palace.creative.utils;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class IgnoreUtil {
    private HashMap<UUID, List<UUID>> ignoreData = new HashMap<>();

    public void addData(UUID player, List<UUID> ignored) {
        ignoreData.put(player, ignored);
    }

    public void logout(UUID uuid) {
        ignoreData.remove(uuid);
    }

    /**
     * Check if player ignores target
     *
     * @param player the player with the ignore list
     * @param target the target we're checking
     * @return if target is on player's ignore list
     */
    public boolean isIgnored(UUID player, UUID target) {
        List<UUID> list = ignoreData.get(player);
        return list != null && list.contains(target);
    }
}
