package network.palace.creative.utils;

import lombok.Getter;
import network.palace.core.player.CPlayer;
import network.palace.creative.Creative;
import network.palace.creative.handlers.PlayerData;

public enum CreativeRank {

    NEW("New", 75),
    REGULAR("Regular", 101),
    SKILLED("Skilled", 151),
    ADVANCED("Advanced", 201),
    MASTER("Master", 301),
    EXPERT("Expert", 401);

    @Getter
    private final int size;
    @Getter
    private final String name;

    CreativeRank(String name, int size) {
        this.name = name;
        this.size = size;
    }

    public static boolean isValidWorld(CPlayer player) {
        PlayerData data = Creative.getInstance().getPlayerData(player.getUniqueId());
        if (data == null) {
            return false;
        }

        return player.getWorld().getName().equals("plot" + data.getRank().size);
    }
}
