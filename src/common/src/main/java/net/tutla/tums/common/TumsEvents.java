package net.tutla.tums.common;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.tutla.tums.TumsAPI;
import net.tutla.tums.tums.objects.TumsEntity;
import net.tutla.tums.tums.objects.TumsPlayer;
import net.tutla.tums.tusan.MinecraftEnvironment;
import net.tutla.tums.tusan.Variable;

import java.util.HashMap;

/**
 * Fires Tums events from the Fabric and NeoForge hooks, so both loaders build the same event variables.
 * Only takes plain values and common (not client-only) Minecraft classes, so a dedicated server never
 * loads a client class through it.
 */
public final class TumsEvents {
    private TumsEvents() {}

    // the GLFW values for the three buttons and for press/release, as InputConstants has them
    private static final int LEFT = 0, RIGHT = 1, MIDDLE = 2;
    private static final int RELEASE = 0, PRESS = 1;

    public static void serverStarted() {
        TumsAPI.executeEvent("SERVER_START", null, MinecraftEnvironment.SERVER);
    }

    public static void entityLoaded(Entity entity) {
        HashMap<String, Variable> variables = new HashMap<>();
        variables.put("event_entity", new TumsEntity("event_entity", entity));
        TumsAPI.executeEvent("SERVER_ENTITY_LOAD", variables, MinecraftEnvironment.SERVER);
    }

    public static void attack(Player player, Entity target) {
        HashMap<String, Variable> variables = new HashMap<>();
        variables.put("event_player", new TumsPlayer("event_player", player));
        variables.put("event_entity", new TumsEntity("event_entity", target));
        TumsAPI.executeEvent("ATTACK", variables, MinecraftEnvironment.CLIENT);
    }

    public static void mouseButton(int button, int action) {
        String name = switch (button) {
            case LEFT -> "LEFT";
            case RIGHT -> "RIGHT";
            case MIDDLE -> "MIDDLE";
            default -> null; // side buttons have no event
        };
        if (name == null) return;
        if (action == PRESS) {
            TumsAPI.executeEvent(name + "_CLICK", null, MinecraftEnvironment.CLIENT);
        } else if (action == RELEASE) {
            TumsAPI.executeEvent(name + "_RELEASE", null, MinecraftEnvironment.CLIENT);
        }
    }

    public static void scroll(double amount) {
        if (amount > 0) {
            TumsAPI.executeEvent("SCROLL_UP", null, MinecraftEnvironment.CLIENT);
        } else if (amount < 0) {
            TumsAPI.executeEvent("SCROLL_DOWN", null, MinecraftEnvironment.CLIENT);
        }
    }

    /** @param keyName the key as InputConstants names it, like key.keyboard.a */
    public static void keyPress(String keyName) {
        HashMap<String, Variable> variables = new HashMap<>();
        String key = keyName.startsWith("key.keyboard.") ? keyName.substring("key.keyboard.".length()) : keyName;
        variables.put("event_key", new Variable("event_key", key, null));
        TumsAPI.executeEvent("KEY_PRESS", variables, MinecraftEnvironment.CLIENT);
    }
}
