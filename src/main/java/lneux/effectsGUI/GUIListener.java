package lneux.effectsGUI;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;

public class GUIListener implements Listener {

    private final EffectsGUI plugin;
    private final Map<String, PotionEffectType> effects;

    public GUIListener(EffectsGUI plugin, Map<String, PotionEffectType> effects) {
        this.plugin = plugin;
        this.effects = effects;
    }

    @EventHandler
    public void aoClicarInventario(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        if (!"Menu de Efeitos".equals(event.getView().getTitle())) return;
        if (event.getClickedInventory() == null || !event.getClickedInventory().equals(event.getView().getTopInventory())) return;

        event.setCancelled(true); // Impede mover itens

        ItemStack clicado = event.getCurrentItem();
        if (clicado == null || !clicado.hasItemMeta()) return;

        ItemMeta meta = clicado.getItemMeta();
        if (meta.getDisplayName().endsWith("(Bloqueado)")) return;

        String nomeEfeito = meta.getDisplayName().replace("§e", "").replace("§c", "").replace(" (Bloqueado)", "");
        PotionEffectType tipo = effects.get(nomeEfeito);
        if (tipo == null) return;

        if (player.hasPotionEffect(tipo)) {
            player.removePotionEffect(tipo);
            player.sendMessage("§aEfeito §e" + nomeEfeito + " §adesativado!");
        } else {
            player.addPotionEffect(new PotionEffect(tipo, Integer.MAX_VALUE, 0, false, false, true));
            player.sendMessage("§aEfeito §e" + nomeEfeito + " §aativado!");
        }

        player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1.0f, 1.0f);

        // Atualiza a interface para mostrar o brilho corretamente
        Bukkit.getScheduler().runTask(plugin, () -> plugin.abrirGUI(player));
    }
}
