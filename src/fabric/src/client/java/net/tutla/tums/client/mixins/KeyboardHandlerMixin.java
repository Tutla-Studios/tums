package net.tutla.tums.client.mixins;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.input.KeyEvent;
import net.tutla.tums.common.TumsEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin {
    // a key going down, not held-down repeats or releases
    @Inject(method = "keyPress", at = @At("HEAD"))
    private void tums$keyPress(long window, int action, KeyEvent event, CallbackInfo ci) {
        if (action == InputConstants.PRESS) {
            TumsEvents.keyPress(InputConstants.getKey(event).getName());
        }
    }
}
