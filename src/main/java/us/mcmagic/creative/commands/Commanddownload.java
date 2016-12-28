package us.mcmagic.creative.commands;

import com.intellectualcrafters.jnbt.CompoundTag;
import com.intellectualcrafters.jnbt.NBTOutputStream;
import com.intellectualcrafters.plot.PS;
import com.intellectualcrafters.plot.api.PlotAPI;
import com.intellectualcrafters.plot.config.Settings;
import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.object.RunnableVal;
import com.intellectualcrafters.plot.util.TaskManager;
import com.plotsquared.bukkit.util.BukkitSchematicHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.mcmagic.creative.handlers.AbstractDelegateOutputStream;
import us.mcmagic.mcmagiccore.chat.formattedmessage.FormattedMessage;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.zip.GZIPOutputStream;

/**
 * Created by Marc on 12/27/16.
 */
public class Commanddownload implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player) sender;
        PlotAPI api = new PlotAPI();
        Plot plot = api.getPlot(player);
        if (!plot.getOwners().contains(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Only the Plot Owner can download a schematic of the plot!");
            return true;
        }
        plot.addRunning();
        player.sendMessage(ChatColor.GREEN + "Generating download link... (This could take a moment, be patient!)");
        BukkitSchematicHandler.manager.getCompoundTag(plot, new RunnableVal<CompoundTag>() {
            @Override
            public void run(CompoundTag value) {
                plot.removeRunning();
                upload(value, null, null, new RunnableVal<URL>() {
                    @Override
                    public void run(URL url) {
                        if (url == null) {
                            player.sendMessage(ChatColor.RED + "Error!");
                            return;
                        }
                        new FormattedMessage("Click here to download your Plot").color(ChatColor.YELLOW)
                                .style(ChatColor.BOLD).tooltip(ChatColor.GREEN + "Click here to download your Plot!")
                                .link(url.toString()).send(player);
                    }
                });
            }
        });
        return true;
    }

    public void upload(final CompoundTag tag, UUID uuid, String file, RunnableVal<URL> whenDone) {
        if (tag == null) {
            PS.debug("&cCannot save empty tag");
            TaskManager.runTask(whenDone);
            return;
        }
        upload2(uuid, file, "schematic", new RunnableVal<OutputStream>() {
            @Override
            public void run(OutputStream output) {
                try {
                    try (GZIPOutputStream gzip = new GZIPOutputStream(output, true)) {
                        try (NBTOutputStream nos = new NBTOutputStream(gzip)) {
                            nos.writeTag(tag);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, whenDone);
    }

    public static void upload2(UUID uuid, String file, String extension, final RunnableVal<OutputStream> writeTask, final RunnableVal<URL> whenDone) {
        if (writeTask == null) {
            PS.debug("&cWrite task cannot be null");
            TaskManager.runTask(whenDone);
            return;
        }
        final String filename;
        final String website;
        if (uuid == null) {
            uuid = UUID.randomUUID();
            website = Settings.Web.URL + "upload.php?" + uuid;
            filename = "plot." + extension;
        } else {
            website = Settings.Web.URL + "save.php?" + uuid;
            filename = file + '.' + extension;
        }
        final URL url;
        try {
            url = new URL(Settings.Web.URL + "?key=" + uuid + "&ip=" + Settings.Web.SERVER_IP + "&type=" + extension);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            whenDone.run();
            return;
        }
        TaskManager.runTaskAsync(() -> {
            try {
                String boundary = Long.toHexString(System.currentTimeMillis());
                URLConnection con = new URL(website).openConnection();
                con.setDoOutput(true);
                con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
                try (OutputStream output = con.getOutputStream();
                     PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8), true)) {
                    String CRLF = "\r\n";
                    writer.append("--").append(boundary).append(CRLF);
                    writer.append("Content-Disposition: form-data; name=\"param\"").append(CRLF);
                    writer.append("Content-Type: text/plain; charset=").append(StandardCharsets.UTF_8.displayName()).append(CRLF);
                    String param = "value";
                    writer.append(CRLF).append(param).append(CRLF).flush();
                    writer.append("--").append(boundary).append(CRLF);
                    writer.append("Content-Disposition: form-data; name=\"schematicFile\"; filename=\"").append(filename).append(String.valueOf('"')).append(CRLF);
                    writer.append("Content-Type: ").append(URLConnection.guessContentTypeFromName(filename)).append(CRLF);
                    writer.append("Content-Transfer-Encoding: binary").append(CRLF);
                    writer.append(CRLF).flush();
                    writeTask.value = new AbstractDelegateOutputStream(output) {
                        @Override
                        public void close() {
                        } // Don't close
                    };
                    writeTask.run();
                    output.flush();
                    writer.append(CRLF).flush();
                    writer.append("--").append(boundary).append("--").append(CRLF).flush();
                }
                try (Reader response = new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8)) {
                    final char[] buffer = new char[256];
                    final StringBuilder result = new StringBuilder();
                    while (true) {
                        final int r = response.read(buffer);
                        if (r < 0) {
                            break;
                        }
                        result.append(buffer, 0, r);
                    }
                    if (!result.toString().startsWith("Success")) {
                        System.out.println(result);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int responseCode = ((HttpURLConnection) con).getResponseCode();
                if (responseCode == 200) {
                    whenDone.value = url;
                }
                TaskManager.runTask(whenDone);
            } catch (IOException e) {
                e.printStackTrace();
                TaskManager.runTask(whenDone);
            }
        });
    }
}
