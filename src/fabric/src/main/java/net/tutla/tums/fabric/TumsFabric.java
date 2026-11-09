package net.tutla.tums.fabric;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.tutla.tums.common.TumsCommon;
import net.tutla.tums.common.TumsEvents;
import net.tutla.tums.tusan.MinecraftEnvironment;

public class TumsFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        MinecraftEnvironment environment = FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT
                ? MinecraftEnvironment.CLIENT
                : MinecraftEnvironment.SERVER;
        TumsCommon.init(FabricLoader.getInstance().getGameDir(), environment);

        // these fire for a dedicated server and for the one inside a singleplayer game alike
        ServerLifecycleEvents.SERVER_STARTED.register(server -> TumsEvents.serverStarted());
        ServerEntityEvents.ENTITY_LOAD.register((entity, level) -> TumsEvents.entityLoaded(entity));
    }
}
