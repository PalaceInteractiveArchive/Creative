package network.palace.creative.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

public class HeadUtil {

    public String url = "https://spreadsheets.google.com/feeds/cells/1_zKmWoZYj7rUkL5qeAmJIfyJlxndl9NcRHxgw3h2wn4/od6/public/basic?alt=json";
    //public String url = "https://spreadsheets.google.com/feeds/cells/1msHPnWju6nSYXcZUwq-F2tU71LoQHYyhJXbG0xiJ2AA/od6/public/basic?alt=json";
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
        Pattern pattern = Pattern.compile("Heads - (?<category>[A-Za-z0-9]*) - (?<page>[0-9]*)");
        Matcher matcher = pattern.matcher(invname);
        if (!matcher.matches()) {
            player.closeInventory();
            player.sendMessage(ChatColor.RED + "An error has occurred! Please contact a developer.");
            return;
        }

        String category = matcher.group("category");
        int page = Integer.valueOf(matcher.group("page"));
        boolean isBack = item.getType().equals(Material.ARROW) && name.equalsIgnoreCase("Back");
        event.setCancelled(true);
        int maxPages = new Double(Math.ceil(map.get(category).size() / 45D)).intValue();
        if (isBack) {
            Creative.getInstance().getMenuUtil().openMenu(player, CreativeInventoryType.HEADSHOP);
            return;
        }
        else if (name.equals("Last Page")) {
            if (page - 1 > 0) {
                openCategory(player, category, page - 1);
            }

            return;
        }
        else if (name.equals("Next Page")) {
            if (page + 1 <= maxPages) {
                openCategory(player, category, page + 1);
            }

            return;
        }

        player.getInventory().addItem(item);
        player.playSound(player.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1, 1);
    }

    public void openCategory(Player player, String name, int page) {
        List<ItemStack> heads = map.get(name);
        int size = heads.size();
        int s = size > 18 ? (size > 27 ? (size > 36 ? 54 : 45) : 36) : 27;
        int itemsSize = s - 9;
        Inventory inv = Bukkit.createInventory(player, s, ChatColor.BLUE + "Heads - " + name + " - " + page);
        for (int x = 0; x < itemsSize; x++) {
            try {
                inv.setItem(x, heads.get(x + (page - 1) * itemsSize));
            }
            catch (IndexOutOfBoundsException ignored) {

            }
        }

        inv.setItem(s - 9, Creative.getInstance().getMenuUtil().last);
        inv.setItem(s - 5, Creative.getInstance().getMenuUtil().back);
        inv.setItem(s - 1, Creative.getInstance().getMenuUtil().next);
        player.openInventory(inv);
    }
}
