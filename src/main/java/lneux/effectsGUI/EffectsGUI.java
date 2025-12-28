package lneux.effectsGUI;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class EffectsGUI extends JavaPlugin {

    private final Map<String, PotionEffectType> effects = new HashMap<>();
    private final Map<String, Material> icons = new HashMap<>();

    @Override
    public void onEnable() {
        // Map efeitos para PotionEffectType
        effects.put("Salto", PotionEffectType.JUMP_BOOST);
        effects.put("Queda Lenta", PotionEffectType.SLOW_FALLING);
        effects.put("Velocidade", PotionEffectType.SPEED);
        effects.put("Mineração Rápida", PotionEffectType.HASTE);
        effects.put("Respiração Aquática", PotionEffectType.WATER_BREATHING);
        effects.put("Visão Noturna", PotionEffectType.NIGHT_VISION);
        effects.put("Resistência ao Fogo", PotionEffectType.FIRE_RESISTANCE);

        // Ícones personalizados
        icons.put("Salto", Material.RABBIT_FOOT);
        icons.put("Queda Lenta", Material.FEATHER);
        icons.put("Velocidade", Material.SUGAR);
        icons.put("Mineração Rápida", Material.GOLDEN_PICKAXE);
        icons.put("Respiração Aquática", Material.HEART_OF_THE_SEA);
        icons.put("Visão Noturna", Material.ENDER_EYE);
        icons.put("Resistência ao Fogo", Material.BLAZE_POWDER);

        // Registrar listener
        getServer().getPluginManager().registerEvents(new GUIListener(this, effects), this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cApenas jogadores podem usar este comando.");
            return true;
        }
        abrirGUI(player);
        return true;
    }

    public void abrirGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 9, "Menu de Efeitos");

        for (Map.Entry<String, PotionEffectType> entry : effects.entrySet()) {
            String nomeEfeito = entry.getKey();
            PotionEffectType tipo = entry.getValue();

            String perm = "effectsgui.effect." + nomeEfeito.toLowerCase().replace(" ", "").replace("ã", "a").replace("ç", "c");
            boolean permitido = player.hasPermission("effectsgui.effect.*") || player.hasPermission(perm);

            ItemStack item;
            if (permitido) {
                Material icon = icons.getOrDefault(nomeEfeito, Material.POTION);
                item = new ItemStack(icon);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName("§e" + nomeEfeito);
                meta.setLore(Arrays.asList("§7Clique para ativar este efeito."));

                // Adiciona brilho se o jogador já tiver o efeito
                if (player.hasPotionEffect(tipo)) {
                    meta.addEnchant(Enchantment.INFINITY, 1, true);
                    meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
                }

                item.setItemMeta(meta);
            } else {
                item = new ItemStack(Material.BARRIER);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName("§c" + nomeEfeito + " (Bloqueado)");
                meta.setLore(Arrays.asList("§7Você não tem permissão para este efeito."));
                item.setItemMeta(meta);
            }

            gui.addItem(item);
        }

        player.openInventory(gui);
    }
}
