package net.avicus.requireplugins;

import com.google.common.base.Joiner;
import com.google.common.util.concurrent.AbstractScheduledService.Scheduler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RequirePlugins extends JavaPlugin {
    private static final Joiner COMMAS = Joiner.on(", ");

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults(true);
        saveConfig();

        List<String> required = getConfig().getStringList("required");

        if (required.isEmpty()) {
            log(ChatColor.YELLOW + "No plugin requirements defined.");
            return;
        }

        getServer().getScheduler().runTask(this, () -> {
            List<String> met = required.stream()
                    .filter(this::hasPlugin)
                    .collect(Collectors.toList());

            String metString = COMMAS.join(met);
            log(ChatColor.GREEN + String.format("Requirements met (%d/%d): %s", met.size(), required.size(), metString));

            if (met.size() == required.size()) {
                log(ChatColor.GREEN + "All requirements met!");
            }
            else {
                List<String> missing = new ArrayList<>(required);
                missing.removeAll(met);

                String missingString = COMMAS.join(missing);

                log(ChatColor.RED + String.format("Missing plugins (%d): %s", missing.size(), missingString));
                shutdown();
            }
        });
    }

    private void log(String message) {
        message = String.format(ChatColor.GRAY + "[RequirePlugins] %s", message);
        Bukkit.getConsoleSender().sendMessage(message);
    }

    private void shutdown() {
        log(ChatColor.RED + "Shutting down!");
        Bukkit.shutdown();
    }

    private boolean hasPlugin(String name) {
        Plugin plugin = getServer().getPluginManager().getPlugin(name);
        return plugin != null && plugin.isEnabled();
    }
}
