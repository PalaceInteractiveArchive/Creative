package network.palace.creative.utils;

import com.google.common.collect.ImmutableMap;
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
import network.palace.core.menu.Menu;
import network.palace.core.menu.MenuButton;
import network.palace.core.player.CPlayer;
import network.palace.core.utils.ItemUtil;
import network.palace.creative.Creative;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class HeadUtil {

    //public String url = "https://spreadsheets.google.com/feeds/cells/1_zKmWoZYj7rUkL5qeAmJIfyJlxndl9NcRHxgw3h2wn4/od6/public/basic?alt=json";
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

    public void openCategory(CPlayer player, String name, int page) {
        List<MenuButton> buttons = new ArrayList<>();
        List<ItemStack> heads = map.get(name);
        int size = heads.size();
        int s = size > 18 ? (size > 27 ? (size > 36 ? 54 : 45) : 36) : 27;
        int itemsSize = s - 9;
        for (int x = 0; x < itemsSize; x++) {
            try {
                ItemStack head = heads.get(x + (page - 1) * itemsSize);
                buttons.add(new MenuButton(x, head, ImmutableMap.of(ClickType.LEFT, p -> {
                    player.getInventory().addItem(head);
                    player.playSound(player.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1, 1);
                })));
            }
            catch (IndexOutOfBoundsException ignored) {

            }
        }

        if (page - 1 > 0) {
            buttons.add(new MenuButton(s - 9, Creative.getInstance().getMenuUtil().last, ImmutableMap.of(ClickType.LEFT, p -> openCategory(p, name, page - 1))));
        }

        buttons.add(new MenuButton(s - 5, Creative.getInstance().getMenuUtil().back, ImmutableMap.of(ClickType.LEFT, Creative.getInstance().getMenuUtil()::openHeadShop)));
        if (page + 1 <= new Double(Math.ceil(map.get(name).size() / 45D)).intValue()) {
            buttons.add(new MenuButton(s - 1, Creative.getInstance().getMenuUtil().next, ImmutableMap.of(ClickType.LEFT, p -> openCategory(p, name, page + 1))));
        }

        new Menu(s, ChatColor.BLUE + "Heads - " + name + " - " + page, player, buttons).open();
    }
}
