package fr.chatcommands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GiveXPCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Vérification si l'expéditeur est un joueur
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Seul un joueur peut exécuter cette commande.");
            return true;
        }

        Player player = (Player) sender;

        // Vérification des arguments
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage : /givexp <montant> d:<range>");
            return true;
        }

        // Parse le montant d'expérience
        int amount;
        try {
            amount = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Le montant doit être un nombre valide.");
            return true;
        }

        // Parse le rayon
        String rangeArg = args[1];
        if (!rangeArg.startsWith("d:")) {
            player.sendMessage(ChatColor.RED + "Le rayon doit être spécifié avec d:<range>.");
            return true;
        }

        int range;
        try {
            range = Integer.parseInt(rangeArg.substring(2));
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Le rayon doit être un nombre valide.");
            return true;
        }

        // Récupère tous les joueurs dans le rayon
        Location playerLocation = player.getLocation();
        int playersFound = 0;

        for (Player target : Bukkit.getOnlinePlayers()) {
            if (target.getWorld().equals(player.getWorld()) && target.getLocation().distance(playerLocation) <= range) {
                // Exécute la commande pour chaque joueur trouvé
                String mmocoreCommand = String.format("mmocore admin exp give %s main %d", target.getName(), amount);
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), mmocoreCommand);
                playersFound++;
            }
        }

        // Message de confirmation
        if (playersFound > 0) {
            player.sendMessage(ChatColor.GREEN + "Vous avez donné " + amount + " d'XP à " + playersFound + " joueur(s) dans un rayon de " + range + ".");
        } else {
            player.sendMessage(ChatColor.RED + "Aucun joueur trouvé dans un rayon de " + range + ".");
        }

        return true;
    }
}
