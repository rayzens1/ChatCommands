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

        this.getCommand("itemedit").setExecutor(new ItemEditCommand());
        this.getCommand("debugmain").setExecutor(new DebugMainCommand());
        this.getCommand("carry").setExecutor(new CarryCommand());

        this.getCommand("givexp").setExecutor(new GiveXPCommand());

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
                // Vérification de base
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Usage: /me <message> d:<distance>");
                    return false;
                }

                // Récupérer l'argument de distance
                String lastArg = args[args.length - 1];
                if (!lastArg.startsWith("d:")) {
                    sender.sendMessage(ChatColor.RED + "Le dernier argument doit spécifier la distance sous la forme d:<distance>.");
                    return false;
                }

                // Extraire la distance
                int distance;
                try {
                    distance = Integer.parseInt(lastArg.substring(2));
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "La distance spécifiée est invalide.");
                    return false;
                }

                // Construire le message
                StringBuilder messageBuilder = new StringBuilder();
                for (int i = 0; i < args.length - 1; i++) { // Exclure l'argument "d:<distance>"
                    messageBuilder.append(args[i]).append(" ");
                }
                String message = messageBuilder.toString().trim();

                // Vérifier si la distance maximale est inférieure ou égale à celle définie dans la configuration
                int configDistance = getConfig().getInt("settings.meMessageDistance", 15);
                if (distance > configDistance) {
                    distance = configDistance;
                    player.sendMessage(ChatColor.YELLOW + "La distance maximale est de (" + configDistance + " blocs).");
                    return false;
                }

                String meMessageTemplate = getConfig().getString("messages.meMessage");

                // Remplacement des variables et conversion des couleurs
                String meMessage = ChatColor.translateAlternateColorCodes('&', meMessageTemplate
                        .replace("%player%", player.getName())
                        .replace("%message%", message));

                envoyerMessageProximite(player, message, distance);


            } else if (label.equalsIgnoreCase("roll")) {
                if (args.length < 1) {
                    player.sendMessage(ChatColor.RED + "Usage: /roll <distance_max>");
                    return false;
                }

                if(args.length > 0) {

                    // Récupérer la distance depuis l'argument
                    int maxDistance;
                    try {
                        maxDistance = Integer.parseInt(args[0]);
                    } catch (NumberFormatException e) {
                        player.sendMessage(ChatColor.RED + "Veuillez entrer une distance valide.");
                        return false;
                    }

                    // Vérifier si la distance maximale est inférieure ou égale à celle définie dans la configuration
                    int configDistance = getConfig().getInt("settings.meMessageDistance", 15);
                    if (maxDistance > configDistance) {
                        maxDistance = configDistance;
                        player.sendMessage(ChatColor.YELLOW + "La distance maximale est de (" + configDistance + " blocs).");
                        return false;
                    }

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

                    envoyerMessageProximiteRoll(player, rollMessage, maxDistance);


                } else {
                    player.sendMessage(ChatColor.RED + "Usage: /roll <distance_max>");
                    return false;
                }
            } else if (command.getName().equalsIgnoreCase("narration")) {
                // Vérifier qu'il y a au moins deux arguments (message + range)
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Usage: /narration <message> d:<range>");
                    return false;
                }

                // Récupérer l'argument range
                String lastArg = args[args.length - 1];
                if (!lastArg.startsWith("d:")) {
                    sender.sendMessage(ChatColor.RED + "Le dernier argument doit spécifier la portée sous la forme d:<distance>.");
                    return false;
                }

                // Extraire la distance
                int range;
                try {
                    range = Integer.parseInt(lastArg.substring(2));
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "La portée spécifiée est invalide.");
                    return false;
                }

                // Construire le message
                StringBuilder messageBuilder = new StringBuilder();
                for (int i = 0; i < args.length - 1; i++) { // Exclure le dernier argument "d:<distance>"
                    messageBuilder.append(args[i]).append(" ");
                }
                String message = messageBuilder.toString().trim();

                // Format du message
                String formattedMessage = ChatColor.GOLD + "\n"
                        + ChatColor.GOLD + "=====================================================\n"
                        + ChatColor.GOLD + "\n"
                        + ChatColor.YELLOW + ChatColor.BOLD+"                       Narration\n"
                        + ChatColor.WHITE + "\n " + message + "\n"
                        + ChatColor.GOLD + "\n"
                        + ChatColor.GOLD + "====================================================="
                        + ChatColor.GOLD + "\n";

                // Envoyer le message à proximité
                if (sender instanceof Player) {
                    player = (Player) sender;

                    // Diffuser le message aux joueurs dans le rayon spécifié
                    Player finalPlayer = player;
                    player.getWorld().getPlayers().stream()
                            .filter(p -> p.getLocation().distance(finalPlayer.getLocation()) <= range)
                            .forEach(p -> p.sendMessage(formattedMessage));

                    sender.sendMessage(ChatColor.GREEN + "Narration envoyée aux joueurs dans un rayon de " + range + " blocs.");
                } else {
                    sender.sendMessage(ChatColor.RED + "Seuls les joueurs peuvent utiliser cette commande.");
                }
                return true;
            }
            return true;
        }
        return false;
    }

    private void envoyerMessageProximite(Player player, String message, int distance) {
        Location playerLocation = player.getLocation();
        String meMessageTemplate = getConfig().getString("messages.meMessage");

        // Remplacement des variables et conversion des couleurs
        String meMessage = ChatColor.translateAlternateColorCodes('&', meMessageTemplate
                .replace("%player%", player.getName())
                .replace("%message%", message));

        // Envoi du message aux joueurs à proximité
        player.getWorld().getNearbyEntities(playerLocation, distance, distance, distance).forEach(entity -> {
            if (entity instanceof Player) {
                ((Player) entity).sendMessage(ChatColor.GRAY + meMessage);
            }
        });

        getLogger().info(player.getName() + " a utilisé la commande /me avec le message : " + meMessage);
    }

    private void envoyerMessageProximiteRoll(Player player, String message, int distance) {
        Location playerLocation = player.getLocation();
        String meMessageTemplate = getConfig().getString("messages.meMessage");

        // Remplacement des variables et conversion des couleurs
        String meMessage = ChatColor.translateAlternateColorCodes('&', meMessageTemplate
                .replace("%player%", player.getName())
                .replace("%message%", message));

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
