package net.tutla.tums.neoforge;

import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.tutla.tums.common.TumsCommon;
import net.tutla.tums.common.TumsEvents;
import net.tutla.tums.tusan.MinecraftEnvironment;

@Mod(TumsCommon.MOD_ID)
public class TumsNeoForge {
    public TumsNeoForge() {
        boolean client = FMLEnvironment.getDist().isClient();
        TumsCommon.init(FMLPaths.GAMEDIR.get(), client ? MinecraftEnvironment.CLIENT : MinecraftEnvironment.SERVER);

        NeoForge.EVENT_BUS.addListener((ServerStartedEvent event) -> TumsEvents.serverStarted());
        // fires on both sides; attack is a client event, as on Fabric
        NeoForge.EVENT_BUS.addListener((AttackEntityEvent event) -> {
            if (event.getEntity().level().isClientSide()) TumsEvents.attack(event.getEntity(), event.getTarget());
        });

        if (client) {
            TumsNeoForgeClient.register(); // its own class, so a dedicated server never loads client classes
        }
    }
}
