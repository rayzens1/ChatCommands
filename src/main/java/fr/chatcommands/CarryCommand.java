package fr.chatcommands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CarryCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Seul un joueur peut utiliser cette commande.");
            return true;
        }

        Player player = (Player) sender;

        // Vérifie si le joueur porte déjà quelqu'un
        if (player.getPassengers().size() > 0) {
            player.removePassenger(player.getPassengers().get(0));
            return true;
        }

        Player nearestPlayer = getNearestPlayer(player, 2);

        if (nearestPlayer == null) {
            player.sendMessage(ChatColor.YELLOW + "Aucun joueur à proximité à moins de 2 blocs.");
            return true;
        }

        // Positionne le joueur ciblé sur la tête du joueur exécutant la commande
        player.addPassenger(nearestPlayer);
        player.sendMessage(ChatColor.GREEN + "Vous portez maintenant " + nearestPlayer.getName() + ".");
        nearestPlayer.sendMessage(ChatColor.YELLOW + player.getName() + " vous porte maintenant.");

        return true;
    }

    /**
     * Récupère le joueur le plus proche dans un rayon donné autour d'un joueur.
     *
     * @param player Le joueur de référence.
     * @param radius Le rayon en blocs.
     * @return Le joueur le plus proche ou null si aucun joueur n'est trouvé.
     */
    private Player getNearestPlayer(Player player, double radius) {
        Location playerLocation = player.getLocation();
        List<Player> nearbyPlayers = player.getWorld().getPlayers();

        Player closestPlayer = null;
        double closestDistance = radius;

        for (Player target : nearbyPlayers) {
            if (target.equals(player)) continue;

            double distance = playerLocation.distance(target.getLocation());
            if (distance <= closestDistance) {
                closestPlayer = target;
                closestDistance = distance;
            }
        }
        return closestPlayer;
    }
}
