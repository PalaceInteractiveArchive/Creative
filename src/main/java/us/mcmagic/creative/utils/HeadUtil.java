package us.mcmagic.creative.utils;

import net.minecraft.server.v1_8_R3.Item;
import net.minecraft.server.v1_8_R3.MojangsonParseException;
import net.minecraft.server.v1_8_R3.MojangsonParser;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.JSONArray;
import org.json.JSONObject;
import us.mcmagic.creative.Creative;
import us.mcmagic.creative.handlers.CreativeInventoryType;
import us.mcmagic.mcmagiccore.itemcreator.ItemCreator;
import us.mcmagic.mcmagiccore.player.User;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

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
        JSONArray array = obj.getJSONObject("feed").getJSONArray("entry");
        map.clear();
        String lastCategory = "";
        String lastName = "";
        for (int i = 0; i < array.length(); i++) {
            JSONObject ob = array.getJSONObject(i);
            JSONObject code = ob.getJSONObject("content");
            JSONObject name = ob.getJSONObject("title");
            String column = name.getString("$t");
            Integer row = Integer.parseInt(column.substring(1, 2));
            //Column A
            if (column.substring(0, 1).equalsIgnoreCase("a")) {
                //Category
                if (code.getString("$t").startsWith("-")) {
                    String cat = code.getString("$t").replace("-", "");
                    map.put(cat, new ArrayList<>());
                    lastCategory = cat;
                    continue;
                }
                //Name
                List<ItemStack> list = map.get(lastCategory);
                String headName = code.getString("$t");
                list.add(new ItemCreator(Material.APPLE, headName));
                map.put(lastCategory, list);
                lastName = headName;
                continue;
            }
            //Column B
            List<ItemStack> list = map.get(lastCategory);
            int i2 = 0;
            for (ItemStack it : list) {
                if (it.getItemMeta().getDisplayName().equals(lastName)) {
                    list.remove(i2);
                    String hash = code.getString("$t");
                    try {
                        list.add(getPlayerHead(hash, ChatColor.GREEN + lastName));
                    } catch (MojangsonParseException e) {
                        Bukkit.getLogger().severe("Error parsing head ID for cell " + name.getString("$t"));
                    }
                }
                i2++;
            }
        }
    }

    public HashMap<String, List<ItemStack>> getCategories() {
        return new HashMap<>(map);
    }

    public static ItemStack getPlayerHead(User user) throws MojangsonParseException {
        return getPlayerHead(user.getTextureHash());
    }

    public static ItemStack getPlayerHead(String hash) throws MojangsonParseException {
        return getPlayerHead(hash, "Head");
    }

    public static ItemStack getPlayerHead(String hash, String display) throws MojangsonParseException {
        net.minecraft.server.v1_8_R3.ItemStack i = new net.minecraft.server.v1_8_R3.ItemStack(Item.getById(397), 1);
        i.setData(3);
        i.setTag(MojangsonParser.parse("{display:{Name:\"" + display + ChatColor.RESET + "\"},SkullOwner:{Id:\"" +
                UUID.randomUUID() + "\",Properties:{textures:[{Value:\"" + hash + "\"}]}}}"));
        return CraftItemStack.asBukkitCopy(i);
    }

    private static JSONObject readJsonFromUrl(String url) {
        try (InputStream is = new URL(url).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } catch (IOException e) {
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
            Creative.menuUtil.openMenu(player, CreativeInventoryType.HEADSHOP);
            return;
        }
        player.getInventory().addItem(item);
        player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
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
        inv.setItem(s - 5, Creative.menuUtil.back);
        player.openInventory(inv);
    }
}