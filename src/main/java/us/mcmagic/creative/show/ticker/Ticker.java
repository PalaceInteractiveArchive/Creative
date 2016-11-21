package us.mcmagic.creative.show.ticker;

import org.bukkit.Bukkit;

public class Ticker implements Runnable {

    @Override
    public void run() {
        Bukkit.getServer().getPluginManager().callEvent(new TickEvent());
    }
}
