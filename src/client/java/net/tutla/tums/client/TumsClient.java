package net.tutla.tums.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.tutla.tums.Tums;
import net.tutla.tums.TumsAPI;
import net.tutla.tums.tusan.MinecraftEnvironment;
import net.tutla.tums.tusan.Variable;
import net.tutla.tums.tusan.interpreter.Interpreter;
import net.tutla.tums.tums.objects.TumsEntity;
import net.tutla.tums.tums.objects.TumsPlayer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;

import java.util.HashMap;
import java.util.List;

public class TumsClient implements ClientModInitializer {
    private boolean mouseCallbackSet = false;

    @Override
    public void onInitializeClient() {

        ClientTickEvents.END_CLIENT_TICK.register(client -> { // minecraft why did u make me take 2 hours to figure ts out
            if (!mouseCallbackSet) {
                long handle = client.getWindow().handle();

                GLFWMouseButtonCallbackI oldC = GLFW.glfwSetMouseButtonCallback(handle, null);

                GLFW.glfwSetMouseButtonCallback(handle, (window, button, action, mods) -> {
                    if (oldC != null) {
                        oldC.invoke(window, button, action, mods);
                    }
                    if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                        if (action == GLFW.GLFW_PRESS){
                            TumsAPI.executeEvent("LEFT_CLICK", null, MinecraftEnvironment.CLIENT);
                        } else if (action == GLFW.GLFW_RELEASE){
                            TumsAPI.executeEvent("LEFT_RELEASE", null, MinecraftEnvironment.CLIENT);
                        }

                    }
                });

                mouseCallbackSet = true;
            }
        });

        AttackEntityCallback.EVENT.register((player, world, hand, target, hitResult) -> {
            if (world.isClientSide()) {
                HashMap<String, Variable> variables = new HashMap<>();
                if (player instanceof Player otherPlayer) {
                    variables.put("event_player", new TumsPlayer("event_player", otherPlayer));
                    variables.put("event_entity", new TumsEntity("event_entity", target));
                } else {
                    variables.put("event_player", new TumsPlayer("event_player", null));
                    variables.put("event_entity", new TumsEntity("event_entity", target));
                }
                TumsAPI.executeEvent("attack", variables, MinecraftEnvironment.CLIENT);
            }
            return InteractionResult.PASS;
        });
    }
}