package network.palace.creative.utils;

import network.palace.core.utils.ItemUtil;
import network.palace.creative.Creative;
import network.palace.creative.handlers.CreativeInventoryType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HeadUtil {

    public String url = "https://spreadsheets.google.com/feeds/cells/1msHPnWju6nSYXcZUwq-F2tU71LoQHYyhJXbG0xiJ2AA/od6/public/basic?alt=json";
    private HashMap<String, List<ItemStack>> map = new HashMap<>();

    public HeadUtil() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(Creative.getInstance(), this::update, 0L, 36000L);
    }

    public void update() {
        JSONObject obj = readJsonFromUrl(url);
        if (obj == null) {
            return;
        }
        JSONArray array = (JSONArray) ((JSONObject) obj.get("feed")).get("entry");
        map.clear();
        String lastCategory = "";
        String lastName = "";
        for (Object anArray : array) {
            JSONObject ob = (JSONObject) anArray;
            JSONObject code = (JSONObject) ob.get("content");
            JSONObject name = (JSONObject) ob.get("title");
            String column = (String) name.get("$t");
            // Column A
            if (column.substring(0, 1).equalsIgnoreCase("a")) {
                // Category
                if (((String) code.get("$t")).startsWith("-")) {
                    String cat = ((String) code.get("$t")).replace("-", "");
                    map.put(cat, new ArrayList<>());
                    lastCategory = cat;
                    continue;
                }
                // Name
                List<ItemStack> list = map.get(lastCategory);
                String headName = (String) code.get("$t");
                list.add(ItemUtil.create(Material.APPLE, headName));
                map.put(lastCategory, list);
                lastName = headName;
                continue;
            }
            // Column B
            List<ItemStack> list = map.get(lastCategory);
            int i2 = 0;
            for (ItemStack it : list) {
                if (it.getItemMeta().getDisplayName().equals(lastName)) {
                    list.remove(i2);
                    String hash = (String) code.get("$t");
                    list.add(network.palace.core.utils.HeadUtil.getPlayerHead(hash, ChatColor.GREEN + lastName));
                }
                i2++;
            }
        }
    }

    public HashMap<String, List<ItemStack>> getCategories() {
        return new HashMap<>(map);
    }

    private static JSONObject readJsonFromUrl(String url) {
        try (InputStream is = new URL(url).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONParser parser = new JSONParser();
            return (JSONObject) parser.parse(jsonText);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public void handleClick(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();
        if (item == null) {
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null || meta.getDisplayName() == null) {
            return;
        }
        String invname = ChatColor.stripColor(event.getInventory().getName());
        String name = ChatColor.stripColor(meta.getDisplayName());
        boolean isBack = item.getType().equals(Material.ARROW);
        event.setCancelled(true);
        if (isBack) {
            Creative.getInstance().getMenuUtil().openMenu(player, CreativeInventoryType.HEADSHOP);
            return;
        }
        player.getInventory().addItem(item);
        player.playSound(player.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1, 1);
    }

    public void openCategory(Player player, String name) {
        List<ItemStack> heads = map.get(name);
        int size = heads.size();
        int s = size > 18 ? (size > 27 ? (size > 36 ? 54 : 45) : 36) : 27;
        Inventory inv = Bukkit.createInventory(player, s, ChatColor.BLUE + "Heads - " + name);
        int place = 0;
        for (ItemStack head : heads) {
            if (place > s - 10) {
                break;
            }
            inv.setItem(place, head);
            place++;
        }
        inv.setItem(s - 5, Creative.getInstance().getMenuUtil().back);
        player.openInventory(inv);
    }
}