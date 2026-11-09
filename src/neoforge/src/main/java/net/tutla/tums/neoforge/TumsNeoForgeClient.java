package net.tutla.tums.neoforge;

import com.mojang.blaze3d.platform.InputConstants;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.tutla.tums.common.TumsEvents;

/** Mouse and keyboard events, which only exist on a game client. */
final class TumsNeoForgeClient {
    private TumsNeoForgeClient() {}

    static void register() {
        NeoForge.EVENT_BUS.addListener((InputEvent.MouseButton.Pre event) ->
                TumsEvents.mouseButton(event.getButton(), event.getAction()));
        NeoForge.EVENT_BUS.addListener((InputEvent.MouseScrollingEvent event) ->
                TumsEvents.scroll(event.getScrollDeltaY()));
        NeoForge.EVENT_BUS.addListener((InputEvent.Key event) -> {
            if (event.getAction() == InputConstants.PRESS) {
                TumsEvents.keyPress(InputConstants.getKey(event.getKeyEvent()).getName());
            }
        });
    }
}
