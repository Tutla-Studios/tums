package net.tutla.tums.neoforge;

import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;
import net.tutla.tums.common.TumsCommon;

@Mod(TumsCommon.MOD_ID)
public class TumsNeoForge {
    public TumsNeoForge() {
        TumsCommon.init(FMLPaths.GAMEDIR.get());
    }
}
