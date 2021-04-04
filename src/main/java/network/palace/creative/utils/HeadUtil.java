package network.palace.creative.utils;

import com.comphenix.protocol.wrappers.nbt.NbtBase;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import lombok.Getter;
import network.palace.core.Core;
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

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class HeadUtil {

    //public String url = "https://spreadsheets.google.com/feeds/cells/1_zKmWoZYj7rUkL5qeAmJIfyJlxndl9NcRHxgw3h2wn4/od6/public/basic?alt=json";
    public String url = "https://spreadsheets.google.com/feeds/cells/1msHPnWju6nSYXcZUwq-F2tU71LoQHYyhJXbG0xiJ2AA/od6/public/basic?alt=json";
    private final HashMap<String, List<ItemStack>> map = new HashMap<>();
    @Getter private final Set<String> hashes = new HashSet<>();

    public HeadUtil() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(Creative.getInstance(), this::update, 0L, 36000L);
    }

    public void update() {
        JsonObject obj = readJsonFromUrl(url);
        if (obj == null) {
            return;
        }
        JsonArray array = (obj.getAsJsonObject("feed")).getAsJsonArray("entry");
        map.clear();
        hashes.clear();
        String lastCategory = "";
        String lastName = "";
        for (JsonElement anArray : array) {
            JsonObject ob = anArray.getAsJsonObject();
            JsonObject code = ob.getAsJsonObject("content");
            JsonObject name = ob.getAsJsonObject("title");
            String column = name.get("$t").getAsString();
            // Column A
            if (column.substring(0, 1).equalsIgnoreCase("a")) {
                // Category
                if (code.get("$t").getAsString().startsWith("-")) {
                    String cat = code.get("$t").getAsString().replace("-", "");
                    map.put(cat, new ArrayList<>());
                    lastCategory = cat;
                    continue;
                }
                // Name
                List<ItemStack> list = map.get(lastCategory);
                String headName = code.get("$t").getAsString();
                list.add(ItemUtil.create(Material.APPLE, headName));
                map.put(lastCategory, list);
                lastName = headName;
                continue;
            }
            // Column B
            List<ItemStack> list = map.get(lastCategory);
            int i2 = 0;
            for (int i = 0; i < list.size(); i++) {
                ItemStack it = list.get(i);
                if (it.getItemMeta().getDisplayName().equals(lastName)) {
                    String hash = code.get("$t").getAsString();
                    ItemStack head = network.palace.core.utils.HeadUtil.getPlayerHead(hash, ChatColor.GREEN + lastName);
                    try {
                        NbtCompound compound = (NbtCompound) NbtFactory.fromItemTag(head);
                        NbtCompound skullOwner = (NbtCompound) ((NbtBase<?>) compound.getValue("SkullOwner"));
                        JsonReader reader = new JsonReader(new StringReader(skullOwner.getValue("Properties").toString()));
                        reader.setLenient(true);
                        JsonObject object = new JsonParser().parse(reader).getAsJsonObject();
                        JsonObject textures = object.getAsJsonObject("textures");
                        JsonArray value = textures.getAsJsonArray("value");
                        JsonObject entry = value.get(0).getAsJsonObject();
                        String texture = entry.get("Value").getAsString();
                        hashes.add(texture);
                    } catch (Exception e) {
                        Core.logMessage("HeadShop", "Error loading head '" + lastName + "'!");
                        e.printStackTrace();
                        continue;
                    }
                    list.set(i, head);
                }
                i2++;
            }
//            map.put(lastCategory, clone);
        }
    }

    public HashMap<String, List<ItemStack>> getCategories() {
        return new HashMap<>(map);
    }

    private static JsonObject readJsonFromUrl(String url) {
        try (InputStream is = new URL(url).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String jsonText = readAll(rd);
            JsonParser parser = new JsonParser();
            return parser.parse(jsonText).getAsJsonObject();
        } catch (Exception e) {
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
            } catch (IndexOutOfBoundsException ignored) {
            }
        }

        if (page - 1 > 0) {
            buttons.add(new MenuButton(s - 9, Creative.getInstance().getMenuUtil().last, ImmutableMap.of(ClickType.LEFT, p -> openCategory(p, name, page - 1))));
        }

        buttons.add(new MenuButton(s - 5, Creative.getInstance().getMenuUtil().back, ImmutableMap.of(ClickType.LEFT, Creative.getInstance().getMenuUtil()::openHeadShop)));
        if (page + 1 <= (int) Math.ceil(map.get(name).size() / 45D)) {
            buttons.add(new MenuButton(s - 1, Creative.getInstance().getMenuUtil().next, ImmutableMap.of(ClickType.LEFT, p -> openCategory(p, name, page + 1))));
        }

        new Menu(s, ChatColor.BLUE + "Heads - " + name + " - " + page, player, buttons).open();
    }
}
