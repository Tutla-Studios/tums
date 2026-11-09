package net.tutla.tums.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.world.InteractionResult;
import net.tutla.tums.common.TumsEvents;

public class TumsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // mouse buttons, scrolling and keys are fired from the mixins in client.mixins
        AttackEntityCallback.EVENT.register((player, world, hand, target, hitResult) -> {
            if (world.isClientSide()) {
                TumsEvents.attack(player, target);
            }
            return InteractionResult.PASS;
        });
    }
}
