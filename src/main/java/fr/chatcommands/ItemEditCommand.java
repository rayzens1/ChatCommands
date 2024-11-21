package fr.chatcommands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class ItemEditCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Vérifier si l'expéditeur est un joueur
        if (!(sender instanceof Player)) {
            sender.sendMessage("Cette commande ne peut être exécutée que par un joueur.");
            return false;
        }

        Player player = (Player) sender;
        ItemStack item = player.getInventory().getItemInMainHand();

        // Vérifier si le joueur tient un objet
        if (item == null || item.getType() == Material.AIR) {
            player.sendMessage("Tu n'as pas d'objet dans ta main.");
            return false;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("info")) {
            // Récupérer l'item en main
            ItemStack itemInHand = player.getInventory().getItemInMainHand();

            if (itemInHand != null && itemInHand.getType() != Material.AIR) {
                // Récupérer les informations de l'item
                ItemMeta meta = itemInHand.getItemMeta();
                String itemName = meta != null ? meta.getDisplayName() : "Aucun nom";
                Material itemType = itemInHand.getType();
                int customModelData = 0; // Par défaut, CustomModelData est 0

                // Vérifier si l'item a un CustomModelData défini
                if (meta != null && meta.hasCustomModelData()) {
                    customModelData = meta.getCustomModelData();
                }

                // Afficher les informations dans le chat
                player.sendMessage(ChatColor.GREEN + "Informations de l'item :");
                player.sendMessage(ChatColor.AQUA + "Nom : " + itemName);
                player.sendMessage(ChatColor.AQUA + "ID de l'item : " + itemType.name());
                player.sendMessage(ChatColor.AQUA + "CustomModelData : " + customModelData);
            }
        }

        // Vérifier si les arguments sont valides
        if (args.length < 1) {
            player.sendMessage("Usage : /itemedit name|custommodeldata|lore <valeur d'édition>");
            return false;
        }

        String editType = args[0].toLowerCase();
        String editValue = args[1];

        // Récupérer les métadonnées de l'item
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            player.sendMessage("Cet objet ne peut pas être modifié.");
            return false;
        }

        // Appliquer les modifications selon le type d'édition
        switch (editType) {
            case "name":
                String name = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
                break;

            case "custommodeldata":
                try {
                    int customModelData = Integer.parseInt(editValue);
                    meta.setCustomModelData(customModelData);
                } catch (NumberFormatException e) {
                    player.sendMessage("La valeur de CustomModelData doit être un nombre entier.");
                    return false;
                }
                break;
            case "lore":
                List<String> lore = meta.getLore();
                String value = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                if (lore != null) {
                    lore.add(ChatColor.translateAlternateColorCodes('&', value));
                    meta.setLore(lore);
                } else {
                    meta.setLore(Arrays.asList(ChatColor.translateAlternateColorCodes('&', value)));
                }
                break;

            default:
                player.sendMessage("Type d'édition inconnu. Utilisez : name, custommodeldata, attackdamage, attackspeed, lore.");
                return false;
        }

        // Appliquer les modifications à l'objet
        item.setItemMeta(meta);
        player.sendMessage("L'objet a été modifié avec succès.");

        return true;
    }
}
