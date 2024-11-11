package fr.chatcommands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public final class ChatCommands extends JavaPlugin implements CommandExecutor {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getCommand("me").setExecutor(this);
        getCommand("roll").setExecutor(this);

        saveDefaultConfig();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (label.equalsIgnoreCase("me")) {
                if (args.length == 0) {
                    player.sendMessage(ChatColor.RED + "Usage: /me <message>");
                    return false;
                }

                String message = String.join(" ", args);

                String meMessageTemplate = getConfig().getString("messages.meMessage");

                // Remplacement des variables et conversion des couleurs
                String meMessage = ChatColor.translateAlternateColorCodes('&', meMessageTemplate
                        .replace("%player%", player.getName())
                        .replace("%message%", message));

                envoyerMessageProximite(player, message);
                creerHologrammeTemporaire(player, meMessage);

            } else if (label.equalsIgnoreCase("roll")) {
                Random random = new Random();
                int roll = random.nextInt(100) + 1;
                String rollString;
                if(roll>=50) {
                    rollString = ChatColor.GREEN+String.valueOf(roll);
                } else {
                    rollString = ChatColor.RED+String.valueOf(roll);
                }

                String rollMessageTemplate = getConfig().getString("messages.rollMessage");
                String rollMessage = ChatColor.translateAlternateColorCodes('&', rollMessageTemplate
                        .replace("%player%", player.getName())
                        .replace("%roll%", rollString));

                envoyerMessageProximiteRoll(player, rollMessage);
                creerHologrammeTemporaire(player, rollMessage);
            }
            return true;
        }
        return false;
    }

    private void envoyerMessageProximite(Player player, String message) {
        Location playerLocation = player.getLocation();
        String meMessageTemplate = getConfig().getString("messages.meMessage");

        // Remplacement des variables et conversion des couleurs
        String meMessage = ChatColor.translateAlternateColorCodes('&', meMessageTemplate
                .replace("%player%", player.getName())
                .replace("%message%", message));

        // Récupère la distance depuis la configuration
        int distance = getConfig().getInt("settings.meMessageDistance", 15);  // Valeur par défaut de 15 si non défini

        // Envoi du message aux joueurs à proximité
        player.getWorld().getNearbyEntities(playerLocation, distance, distance, distance).forEach(entity -> {
            if (entity instanceof Player) {
                ((Player) entity).sendMessage(ChatColor.GRAY + meMessage);
            }
        });

        getLogger().info(player.getName() + " a utilisé la commande /me avec le message : " + meMessage);
    }

    private void envoyerMessageProximiteRoll(Player player, String message) {
        Location playerLocation = player.getLocation();
        String meMessageTemplate = getConfig().getString("messages.meMessage");

        // Remplacement des variables et conversion des couleurs
        String meMessage = ChatColor.translateAlternateColorCodes('&', meMessageTemplate
                .replace("%player%", player.getName())
                .replace("%message%", message));

        // Récupère la distance depuis la configuration
        int distance = getConfig().getInt("settings.rollMessageDistance", 15);  // Valeur par défaut de 15 si non défini

        // Envoi du message aux joueurs à proximité
        player.getWorld().getNearbyEntities(playerLocation, distance, distance, distance).forEach(entity -> {
            if (entity instanceof Player) {
                ((Player) entity).sendMessage(ChatColor.GRAY + meMessage);
            }
        });

        getLogger().info(player.getName() + " a utilisé la commande /roll : " + "\"" +meMessage+ "\"");
    }

    private void creerHologrammeTemporaire(Player player, String message) {
        Location hologramLocation = player.getLocation().add(0, 2, 0);
        ArmorStand hologram = (ArmorStand) player.getWorld().spawnEntity(hologramLocation, EntityType.ARMOR_STAND);

        // Conversion des couleurs pour l'hologramme
        hologram.setCustomName(ChatColor.translateAlternateColorCodes('&', message));
        hologram.setCustomNameVisible(true);
        hologram.setInvisible(true);
        hologram.setMarker(true); // Empêche les collisions

        // Tâche pour faire suivre l'hologramme à la tête du joueur et le supprimer après 5 secondes
        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks >= 5 * 20) { // 5 secondes en ticks
                    hologram.remove();
                    this.cancel();
                    return;
                }

                // Met à jour la position de l'hologramme pour qu'il suive la tête du joueur
                hologram.teleport(player.getLocation().add(0, 2, 0));

                ticks++;
            }
        }.runTaskTimer(this, 0L, 1L); // Exécute toutes les 1 tick (0.05 seconde)
    }

}
