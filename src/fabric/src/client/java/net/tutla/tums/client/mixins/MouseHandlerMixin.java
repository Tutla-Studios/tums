package net.tutla.tums.client.mixins;

import net.minecraft.client.MouseHandler;
import net.minecraft.client.input.MouseButtonInfo;
import net.tutla.tums.common.TumsEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {
    // left/right/middle click and release, in menus too
    @Inject(method = "onButton", at = @At("HEAD"))
    private void tums$onButton(long window, MouseButtonInfo buttonInfo, int action, CallbackInfo ci) {
        TumsEvents.mouseButton(buttonInfo.button(), action);
    }

    @Inject(method = "onScroll", at = @At("HEAD"))
    private void tums$onScroll(long window, double xOffset, double yOffset, CallbackInfo ci) {
        TumsEvents.scroll(yOffset);
    }
}
