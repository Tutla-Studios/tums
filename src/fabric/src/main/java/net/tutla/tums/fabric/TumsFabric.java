package net.tutla.tums.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.tutla.tums.common.TumsCommon;

public class TumsFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        TumsCommon.init(FabricLoader.getInstance().getGameDir());
    }
}
