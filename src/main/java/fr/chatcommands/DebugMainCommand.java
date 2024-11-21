package fr.chatcommands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DebugMainCommand implements CommandExecutor {

    // Méthode pour gérer la commande /debugmain
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Vérifier si l'expéditeur est un joueur
        if (!(sender instanceof Player)) {
            sender.sendMessage("Cette commande ne peut être exécutée que par un joueur.");
            return false;
        }

        Player player = (Player) sender;
        // Vérifier si le joueur a un item dans sa main secondaire
        ItemStack offHandItem = player.getInventory().getItemInOffHand();

        if (offHandItem != null && offHandItem.getType() != Material.AIR) {
            // Déposer l'item à ses pieds
            player.getWorld().dropItem(player.getLocation(), offHandItem);
            // Retirer l'item de la main secondaire
            player.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
            player.sendMessage(ChatColor.GREEN+"L'item de ta main secondaire a été déposé à tes pieds.");
        } else {
            player.sendMessage(ChatColor.RED+"Tu n'as pas d'item dans ta main secondaire.");
        }

        return true;
    }
}