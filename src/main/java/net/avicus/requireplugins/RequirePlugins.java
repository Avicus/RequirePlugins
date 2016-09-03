package net.avicus.requireplugins;

import com.google.common.base.Joiner;
import com.google.common.util.concurrent.AbstractScheduledService.Scheduler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.stream.Collectors;

public class RequirePlugins extends JavaPlugin {
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
            List<String> missing = required.stream()
                    .filter(name -> !hasPlugin(name))
                    .collect(Collectors.toList());

            if (missing.isEmpty()) {
                log(ChatColor.GREEN + "All plugin requirements are met!");
            }
            else {
                String missingString = Joiner.on(", ").join(missing);
                log(ChatColor.RED + String.format("Missing %d plugins: %s", missing.size(), missingString));
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
