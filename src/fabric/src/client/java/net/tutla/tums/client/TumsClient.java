package net.tutla.tums.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.tutla.tums.TumsAPI;
import net.tutla.tums.tusan.MinecraftEnvironment;
import net.tutla.tums.tusan.Variable;
import net.tutla.tums.tums.objects.TumsEntity;
import net.tutla.tums.tums.objects.TumsPlayer;
import java.util.HashMap;

public class TumsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

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