package network.palace.creative.show;

import com.intellectualcrafters.plot.api.PlotAPI;
import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.object.PlotId;
import lombok.Getter;
import lombok.Setter;
import network.palace.audio.Audio;
import network.palace.audio.handlers.AudioArea;
import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.creative.Creative;
import network.palace.creative.handlers.ShowColor;
import network.palace.creative.handlers.ShowFireworkData;
import network.palace.creative.show.actions.FireworkAction;
import network.palace.creative.show.actions.ParticleAction;
import network.palace.creative.show.actions.ShowAction;
import network.palace.creative.show.actions.TextAction;
import network.palace.creative.show.handlers.AudioTrack;
import network.palace.creative.utils.ParticleUtil;
import org.bukkit.*;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Show {
    private final UUID owner;
    private String name;
    private final PlotId plotId;
    private final World world;
    private final long startTime;
    public List<ShowAction> actions;
    private final HashMap<String, String> invalidLines;
    private long lastPlayerListUpdate = System.currentTimeMillis();
    private List<UUID> nearbyPlayers = new ArrayList<>();
    @Setter private String audioTrack = "none";
    public long musicTime = 0;
    @Getter @Setter private boolean saving = false;

    public Show(File file, CPlayer player, Plot plot) {
        owner = player.getUniqueId();
        world = player.getLocation().getWorld();
        invalidLines = new HashMap<>();
        actions = new ArrayList<>();
        if (file != null) {
            loadActions(file);
        }
        if (name == null) {
            if (file == null) {
                name = "New Show";
            } else {
                name = file.getName().replace(".show", "");
            }
        }
        startTime = System.currentTimeMillis();
        this.plotId = plot.getId();
        for (Player tp : Bukkit.getOnlinePlayers()) {
            Plot p = new PlotAPI().getPlot(tp.getLocation());
            if (p != null && p.getId().equals(plot.getId())) {
                nearbyPlayers.add(tp.getUniqueId());
            }
        }
    }

    private Location strToLoc(String string) {
        if (string.length() == 0) {
            return null;
        }
        String[] tokens = string.split(",");
        try {
            for (World cur : Bukkit.getWorlds()) {
                if (cur.getName().equalsIgnoreCase(tokens[0])) {
                    return new Location(cur, Double.parseDouble(tokens[1]),
                            Double.parseDouble(tokens[2]), Double.parseDouble(tokens[3]));
                }
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    private void loadActions(File file) {
        List<ShowAction> actions = new ArrayList<>();
        String strLine = "";
        try {
            FileInputStream fstream = new FileInputStream(file);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            // Parse Lines
            while ((strLine = br.readLine()) != null) {
                if (strLine.length() == 0) {
                    continue;
                }
                String[] tokens = strLine.split(" ");
                if (tokens[0].equals("Name")) {
                    StringBuilder name = new StringBuilder();
                    for (int i = 1; i < tokens.length; i++) {
                        String part = tokens[i];
                        name.append(part);
                        if ((i - 2) < (tokens.length)) {
                            name.append(" ");
                        }
                    }
                    this.name = name.toString();
                    continue;
                }
                if (tokens[0].equals("Audio")) {
                    musicTime = System.currentTimeMillis();
                    AudioTrack track = null;
                    for (Map.Entry<String, AudioTrack> entry : Creative.getInstance().getShowManager().getAudioTracks().entrySet()) {
                        AudioTrack t = entry.getValue();
                        if (entry.getKey().equalsIgnoreCase(tokens[1])) {
                            track = t;
                            break;
                        }
                    }
                    if (track == null) {
                        continue;
                    }
                    setAudioTrack(track.getAudioPath());
                    continue;
                }
                // Get time
                String[] timeToks = tokens[0].split("_");
                long time = 0;
                for (String timeStr : timeToks) {
                    time += (long) (Double.parseDouble(timeStr) * 1000);
                }
                Integer id = actions.size() + 1;
                // Text
                if (tokens[1].contains("Text")) {
                    StringBuilder text = new StringBuilder();
                    for (int i = 2; i < tokens.length; i++) {
                        text.append(tokens[i]).append(" ");
                    }
                    if (text.length() > 1) {
                        text = new StringBuilder(text.substring(0, text.length() - 1));
                    }
                    actions.add(new TextAction(this, time, text.toString()));
                    continue;
                }
                if (tokens[1].startsWith("Firework")) {
                    // Location
                    Location loc = strToLoc(world.getName() + "," + tokens[2]);
                    if (loc == null) {
                        invalidLines.put(strLine, "Invalid Location");
                        continue;
                    }
                    // Power
                    int power;
                    try {
                        power = Integer.parseInt(tokens[3]);
                        if (power < 0 || power > 5) {
                            invalidLines.put(strLine, "Power too High/Low");
                            continue;
                        }
                    } catch (Exception e) {
                        invalidLines.put(strLine, "Invalid Power");
                        continue;
                    }
                    FireworkEffect.Type type = Creative.getInstance().getShowManager().getType(tokens[4]);
                    List<ShowColor> colors = Stream.of(tokens[5].split(",")).map(ShowColor::fromString).collect(Collectors.toList());
                    List<ShowColor> fade = Stream.of(tokens[6].split(",")).map(ShowColor::fromString).collect(Collectors.toList());
                    boolean flicker = tokens[7].equalsIgnoreCase("true");
                    boolean trail = tokens[8].equalsIgnoreCase("true");
                    ShowFireworkData data = new ShowFireworkData(type, colors, fade, flicker, trail);
                    actions.add(new FireworkAction(this, time, loc, data, power));
                    continue;
                }
                if (tokens[1].contains("Particle")) {
                    // 0 Particle type x,y,z oX oY oZ speed amount
                    Particle effect = ParticleUtil.getParticle(tokens[2]);
                    Location location = strToLoc(world.getName() + "," + tokens[3]);
                    double offsetX = Float.parseFloat(tokens[4]);
                    double offsetY = Float.parseFloat(tokens[5]);
                    double offsetZ = Float.parseFloat(tokens[6]);
                    float speed = Float.parseFloat(tokens[7]);
                    int amount = Integer.parseInt(tokens[8]);
                    actions.add(new ParticleAction(this, time, effect, location, offsetX, offsetY, offsetZ,
                            speed, amount));
                }
            }
            in.close();
            this.actions = actions;
        } catch (Exception e) {
            System.out.println("Error on Line [" + strLine + "]");
            e.printStackTrace();
        }
    }

    private int getInt(String s) {
        return Integer.parseInt(s);
    }

    public List<UUID> getNearPlayers() {
        if (System.currentTimeMillis() - lastPlayerListUpdate < 10000) {
            return new ArrayList<>(nearbyPlayers);
        }
        List<UUID> list = new ArrayList<>();
        for (Player tp : Bukkit.getOnlinePlayers()) {
            Plot p = new PlotAPI().getPlot(tp.getLocation());
            if (p != null && p.getId().equals(plotId)) {
                list.add(tp.getUniqueId());
            }
        }
        lastPlayerListUpdate = System.currentTimeMillis();
        nearbyPlayers = list;
        return list;
    }

    public boolean isInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    public boolean update() {
        if (isSaving()) {
            return false;
        }
        if (!invalidLines.isEmpty()) {
            return true;
        }
        for (ShowAction action : new ArrayList<>(actions)) {
            try {
                if (System.currentTimeMillis() - startTime <= action.time) {
                    continue;
                }
                action.play();
                actions.remove(action);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return actions.isEmpty();
    }

    public void displayText(String text) {
        for (UUID uuid : getNearPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) {
                continue;
            }
            player.sendMessage(ChatColor.AQUA + ChatColor.translateAlternateColorCodes('&', text));
        }
    }

    @SuppressWarnings("deprecation")
    public void playMusic(int record) {
        for (UUID uuid : getNearPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) {
                continue;
            }
            player.playEffect(player.getLocation(), Effect.RECORD_PLAY, record);
        }
    }

    public FireworkEffect parseEffect(String effect) {
        String[] tokens = effect.split(",");

        // Shape
        Type shape;
        try {
            shape = Type.valueOf(tokens[0]);
        } catch (Exception e) {
            invalidLines.put(effect, "Invalid type [" + tokens[0] + "]");
            return null;
        }

        // Color
        List<Color> colors = new ArrayList<>();
        for (String color : tokens[1].split("&")) {
            if (color.equalsIgnoreCase("AQUA")) {
                colors.add(Color.AQUA);
            } else if (color.equalsIgnoreCase("BLACK")) {
                colors.add(Color.BLACK);
            } else if (color.equalsIgnoreCase("BLUE")) {
                colors.add(Color.BLUE);
            } else if (color.equalsIgnoreCase("FUCHSIA")) {
                colors.add(Color.FUCHSIA);
            } else if (color.equalsIgnoreCase("GRAY")) {
                colors.add(Color.GRAY);
            } else if (color.equalsIgnoreCase("GREEN")) {
                colors.add(Color.GREEN);
            } else if (color.equalsIgnoreCase("LIME")) {
                colors.add(Color.LIME);
            } else if (color.equalsIgnoreCase("MAROON")) {
                colors.add(Color.MAROON);
            } else if (color.equalsIgnoreCase("NAVY")) {
                colors.add(Color.NAVY);
            } else if (color.equalsIgnoreCase("OLIVE")) {
                colors.add(Color.OLIVE);
            } else if (color.equalsIgnoreCase("ORANGE")) {
                colors.add(Color.ORANGE);
            } else if (color.equalsIgnoreCase("PURPLE")) {
                colors.add(Color.PURPLE);
            } else if (color.equalsIgnoreCase("RED")) {
                colors.add(Color.RED);
            } else if (color.equalsIgnoreCase("SILVER")) {
                colors.add(Color.SILVER);
            } else if (color.equalsIgnoreCase("TEAL")) {
                colors.add(Color.TEAL);
            } else if (color.equalsIgnoreCase("WHITE")) {
                colors.add(Color.WHITE);
            } else if (color.equalsIgnoreCase("YELLOW")) {
                colors.add(Color.YELLOW);
            } else if (color.contains(";")) {
                String[] list = color.split(";");
                colors.add(Color.fromRGB(getInt(list[0]), getInt(list[1]), getInt(list[2])));
            } else {
                invalidLines.put(effect, "Invalid Color [" + color + "]");
                return null;
            }
        }
        if (colors.isEmpty()) {
            invalidLines.put(effect, "No Valid Colors");
            return null;
        }
        // Fade
        List<Color> fades = new ArrayList<>();
        if (tokens.length > 2) {
            for (String color : tokens[2].split("&")) {
                if (color.equalsIgnoreCase("AQUA")) {
                    fades.add(Color.AQUA);
                } else if (color.equalsIgnoreCase("BLACK")) {
                    fades.add(Color.BLACK);
                } else if (color.equalsIgnoreCase("BLUE")) {
                    fades.add(Color.BLUE);
                } else if (color.equalsIgnoreCase("FUCHSIA")) {
                    fades.add(Color.FUCHSIA);
                } else if (color.equalsIgnoreCase("GRAY")) {
                    fades.add(Color.GRAY);
                } else if (color.equalsIgnoreCase("GREEN")) {
                    fades.add(Color.GREEN);
                } else if (color.equalsIgnoreCase("LIME")) {
                    fades.add(Color.LIME);
                } else if (color.equalsIgnoreCase("MAROON")) {
                    fades.add(Color.MAROON);
                } else if (color.equalsIgnoreCase("NAVY")) {
                    fades.add(Color.NAVY);
                } else if (color.equalsIgnoreCase("OLIVE")) {
                    fades.add(Color.OLIVE);
                } else if (color.equalsIgnoreCase("ORANGE")) {
                    fades.add(Color.ORANGE);
                } else if (color.equalsIgnoreCase("PURPLE")) {
                    fades.add(Color.PURPLE);
                } else if (color.equalsIgnoreCase("RED")) {
                    fades.add(Color.RED);
                } else if (color.equalsIgnoreCase("SILVER")) {
                    fades.add(Color.SILVER);
                } else if (color.equalsIgnoreCase("TEAL")) {
                    fades.add(Color.TEAL);
                } else if (color.equalsIgnoreCase("WHITE")) {
                    fades.add(Color.WHITE);
                } else if (color.equalsIgnoreCase("YELLOW")) {
                    fades.add(Color.YELLOW);
                } else if (color.contains(";")) {
                    String[] list = color.split(";");
                    colors.add(Color.fromRGB(getInt(list[0]), getInt(list[1]), getInt(list[2])));
                } else if (color.equalsIgnoreCase("FLICKER") || color.equalsIgnoreCase("TRAIL")) {
                    break;
                } else {
                    invalidLines.put(effect, "Invalid Fade Color [" + color + "]");
                    return null;
                }
            }
        }
        boolean flicker = effect.toLowerCase().contains("flicker");
        boolean trail = effect.toLowerCase().contains("trail");
        // Firework
        return FireworkEffect.builder().with(shape).withColor(colors).withFade(fades).flicker(flicker).trail(trail).build();
    }

    public UUID getOwner() {
        return owner;
    }

    public String getNameColored() {
        return ChatColor.translateAlternateColorCodes('&', name.trim());
    }

    public List<ShowAction> getActions() {
        return new ArrayList<>(actions);
    }

    public void saveFile() {
        if (isSaving()) {
            Core.getPlayerManager().getPlayer(owner).sendMessage(ChatColor.RED + "Could not save show file, already saving!");
            return;
        }
        setSaving(true);
        long t = System.currentTimeMillis();
        CPlayer cp = Core.getPlayerManager().getPlayer(owner);
        cp.getActionBar().show(ChatColor.GREEN + "Saving show file...");
        Bukkit.getScheduler().runTaskAsynchronously(Creative.getInstance(), () -> {
            try {
                String name = ChatColor.stripColor(getNameColored()).replaceAll("\\W", " ");
                char[] chars = name.toCharArray();
                if (chars[chars.length - 1] == ' ') {
                    name = name.substring(0, chars.length - 1);
                }

                while (name.endsWith(" .show")) {
                    name = name.replace(" .show", ".show");
                }

                BufferedWriter bw = new BufferedWriter(new FileWriter("plugins/Creative/shows/" + getOwner().toString() + "/"
                        + name + ".show", false));
                bw.write("Name " + name);
                bw.newLine();
                if (!audioTrack.equals("none")) {
                    String s = "";
                    for (Map.Entry<String, AudioTrack> entry : Creative.getInstance().getShowManager().getAudioTracks().entrySet()) {
                        if (entry.getValue().getAudioPath().equalsIgnoreCase(audioTrack)) {
                            s = entry.getKey();
                            break;
                        }
                    }
                    if (!s.equals("")) {
                        bw.write("Audio " + s);
                    }
                }
                bw.newLine();
                for (ShowAction act : getActions()) {
                    bw.write(act.toString());
                    bw.newLine();
                }
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            setSaving(false);
            long t2 = System.currentTimeMillis();
            long diff = t2 - t;
            System.out.println("Finished saving (" + diff + "ms) show file " + cp.getName());
            if (diff >= 500) {
                Bukkit.getLogger().warning("Took more than 500ms to save " + cp.getName() + "'s show file " +
                        diff + "ms");
                for (CPlayer cp1 : Core.getPlayerManager().getOnlinePlayers()) {
                    if (cp1 == null)
                        continue;
                    if (cp1.getRank().getRankId() < Rank.DEVELOPER.getRankId()) {
                        continue;
                    }
                    cp1.sendMessage(ChatColor.RED + "Took more than 500ms to save " + cp1.getName() +
                            "'s show file " + diff + "ms");
                }
            }
            cp.getActionBar().show(ChatColor.GREEN + "Show file saved (took " + diff + "ms)");
        });
    }

    public void syncAudioForPlayer(final CPlayer tp) {
        final AudioArea area = Audio.getInstance().getByName(Core.getPlayerManager().getPlayer(owner).getUniqueId().toString());
        if (area == null) return;
        area.triggerPlayer(tp);
        tp.sendMessage(ChatColor.GREEN + "Syncing your audio!");
        Bukkit.getScheduler().runTaskLater(Creative.getInstance(), () -> area.sync(((System.currentTimeMillis() - musicTime + 300) / 1000.0), tp), 20L);
    }

    public void setName(String name) {
        new File("plugins/Creative/shows/" + getOwner().toString() + "/" + ChatColor.stripColor(getNameColored()) + ".show").delete();
        this.name = name;
    }

    public String getAudioTrack() {
        return audioTrack;
    }
}
