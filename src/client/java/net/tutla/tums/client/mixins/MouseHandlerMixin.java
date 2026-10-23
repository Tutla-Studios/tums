package net.tutla.tums.client.mixins;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.input.MouseButtonInfo;
import net.tutla.tums.TumsAPI;
import net.tutla.tums.tusan.MinecraftEnvironment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {
    @Inject(method = "onButton", at = @At("HEAD"))
    private void tums$onButton(long window, MouseButtonInfo buttonInfo, int action, CallbackInfo ci) {
        if (buttonInfo.button() != InputConstants.MOUSE_BUTTON_LEFT) return;


        if (action == 1) {
            TumsAPI.executeEvent("LEFT_CLICK", null, MinecraftEnvironment.CLIENT);
        } else if (action == 0) {
            TumsAPI.executeEvent("LEFT_RELEASE", null, MinecraftEnvironment.CLIENT);
        }
    }
}
