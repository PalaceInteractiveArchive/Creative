package network.palace.creative.utils;

import lombok.Getter;
import network.palace.core.player.CPlayer;
import network.palace.creative.Creative;
import network.palace.creative.handlers.PlayerData;

public enum CreativeRank {

    NEW("New", 75, 50, 0),
    REGULAR("Regular", 101, 100, 0),
    SKILLED("Skilled", 151, 150, 100),
    ADVANCED("Advanced", 201, 200, 200),
    MASTER("Master", 301, 300, 300),
    EXPERT("Expert", 401, 400, 400);

    @Getter
    private final int honor;
    @Getter
    private final int size;
    @Getter
    private final int worldEditLimit;
    @Getter
    private final String name;

    CreativeRank(String name, int size, int honor, int worldEditLimit) {
        this.name = name;
        this.size = size;
        this.honor = honor;
        this.worldEditLimit = worldEditLimit;
    }

    public static boolean isValidWorld(CPlayer player) {
        PlayerData data = Creative.getInstance().getPlayerData(player.getUniqueId());
        if (data == null) {
            return false;
        }

        return player.getWorld().getName().equals("plot" + data.getRank().size);
    }
}
