//©️Lukas Pellny | 2023
package de.lukasdev.dailyreward.commands;

import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class DailyRewardManager implements CommandExecutor, Listener {

    private final Map<UUID, Long> lastClaimed;
    private final Random random;

    public DailyRewardManager() {
        this.lastClaimed = new HashMap<>();
        this.random = new Random();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Du musst ein Spieler sein, um diesen Befehl zu verwenden.");
            return true;
        }

        Player player = (Player) sender;

        if (cmd.getName().equalsIgnoreCase("daily")) {
            if (canOpenDailyGUI(player)) {
                openDailyRewardGUI(player);
            } else {
                player.sendMessage(ChatColor.RED + "Du kannst die Daily Reward GUI erst wieder in "
                        + getTimeUntilNextOpen(player) + " Stunden öffnen.");
            }
        }

        return true;
    }

    private boolean canOpenDailyGUI(Player player) {
        UUID uuid = player.getUniqueId();
        long lastOpenTime = lastClaimed.getOrDefault(uuid, 0L);
        long currentTime = System.currentTimeMillis();
        long timeSinceLastOpen = currentTime - lastOpenTime;
        long dayInMillis = 24 * 60 * 60 * 1000; // 24 Stunden in Millisekunden

        int daysSinceLastOpen = (int) (timeSinceLastOpen / dayInMillis);

        return daysSinceLastOpen >= 1;
    }

    private long getTimeUntilNextOpen(Player player) {
        UUID uuid = player.getUniqueId();
        long lastOpenTime = lastClaimed.getOrDefault(uuid, 0L);
        long currentTime = System.currentTimeMillis();
        long timeSinceLastOpen = currentTime - lastOpenTime;
        long dayInMillis = 24 * 60 * 60 * 1000; // 24 Stunden in Millisekunden

        int daysSinceLastOpen = (int) (timeSinceLastOpen / dayInMillis);

        long timeUntilNextOpen = dayInMillis - (timeSinceLastOpen % dayInMillis);
        return timeUntilNextOpen / (60 * 60 * 1000); // Umrechnung in Stunden
    }

    private void openDailyRewardGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, ChatColor.GOLD + "Daily Reward");

        ItemStack claimButton = new ItemStack(Material.CHEST);
        ItemStack closeButton = new ItemStack(Material.BARRIER);

        gui.setItem(12, claimButton);
        gui.setItem(14, closeButton);

        // Füge graue Glasscheiben in leeren Slots hinzu
        for (int i = 0; i < gui.getSize(); i++) {
            if (gui.getItem(i) == null || gui.getItem(i).getType() == Material.AIR) {
                ItemStack glassPane = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
                gui.setItem(i, glassPane);
            }
        }

        player.openInventory(gui);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
            return;
        }

        if (event.getView().getTitle().equals(ChatColor.GOLD + "Daily Reward")) {
            event.setCancelled(true);

            if (clickedItem.getType() == Material.CHEST) {
                claimDailyReward(player);
                player.closeInventory();
            } else if (clickedItem.getType() == Material.BARRIER) {
                player.closeInventory();
            }
        }
    }

    private void claimDailyReward(Player player) {
        UUID uuid = player.getUniqueId();
        long currentTime = System.currentTimeMillis();
        lastClaimed.put(uuid, currentTime);

        ItemStack reward = getDailyReward();

        if (random.nextInt(100) < 1) {
            reward = new ItemStack(Material.DIAMOND, 64); // Jackpot: Ein Stack Diamanten
            Bukkit.broadcastMessage(ChatColor.GOLD + player.getName() + " hat den Jackpot im Daily Reward gewonnen!");
        }

        player.getInventory().addItem(reward);

        player.sendMessage(ChatColor.GREEN + "Du hast deine tägliche Belohnung erhalten!");
    }

    private ItemStack getDailyReward() {
        int randomValue = random.nextInt(100); // Wert zwischen 0 und 99

        if (randomValue < 30) {
            return new ItemStack(Material.DIAMOND, 2); // 30% Chance, 2 Diamanten zu erhalten
        } else {
            return new ItemStack(Material.IRON_INGOT, 3); // 70% Chance, 3 Eisenbarren zu erhalten
        }
    }
}
