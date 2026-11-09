package net.tutla.tums.paper;

import net.tutla.tums.TumsAPI;
import net.tutla.tums.tusan.MinecraftEnvironment;
import net.tutla.tums.tusan.Variable;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.HashMap;

/**
 * The Tums events a server can see. Clicks here are a player's, seen by the server, so there are no
 * releases, middle clicks, scrolling or keys. Only Bukkit API is used, so this works on Spigot too.
 */
final class TumsPaperEvents implements Listener {

    // the same properties TumsEntity and TumsPlayer give a script on Fabric and NeoForge
    static Variable entity(String name, Entity entity) {
        Variable v = new Variable(name, entity, null);
        v.updateProperty("name", entity.getName());
        v.updateProperty("nametag", entity.getCustomName());
        v.updateProperty("uuid", entity.getUniqueId());
        Location at = entity.getLocation();
        v.updateProperty("x", at.getX());
        v.updateProperty("y", at.getY());
        v.updateProperty("z", at.getZ());
        v.updateProperty("yaw", at.getYaw());
        v.updateProperty("pitch", at.getPitch());
        v.updateProperty("javaclass", entity);
        if (entity instanceof Player player) {
            v.updateProperty("health", player.getHealth());
        }
        return v;
    }

    private static void fire(String event, HashMap<String, Variable> variables) {
        TumsAPI.executeEvent(event, variables, MinecraftEnvironment.SERVER);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return; // fired once per hand: count the click once
        String name = switch (event.getAction()) {
            case LEFT_CLICK_AIR, LEFT_CLICK_BLOCK -> "LEFT_CLICK";
            case RIGHT_CLICK_AIR, RIGHT_CLICK_BLOCK -> "RIGHT_CLICK";
            default -> null; // stepping on a pressure plate
        };
        if (name == null) return;
        HashMap<String, Variable> variables = new HashMap<>();
        variables.put("event_player", entity("event_player", event.getPlayer()));
        fire(name, variables);
    }

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;
        HashMap<String, Variable> variables = new HashMap<>();
        variables.put("event_player", entity("event_player", player));
        variables.put("event_entity", entity("event_entity", event.getEntity()));
        fire("ATTACK", variables);
    }
}
