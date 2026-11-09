package net.tutla.tums.tums.objects;


import net.minecraft.world.entity.player.Player;
import net.tutla.tums.tusan.Variable;

import java.util.HashMap;

/// This is the Tums variation of the Minecraft Player, it inherits all the properties of [Entity](https://wiki.tutla.net/tums/scripting/types/entity). Here are all of it's *unique* properties:
///
/// <!-- properties -->
///
/// @tums.title TumsPlayer
/// @tums.summary The TumsPlayer type documentation
public class TumsPlayer extends TumsEntity {
    public Player main;
    public TumsPlayer(String name, Player player) {
        super(name, player);
        main = player;
        setProps();
    }

    public void setProps(){
        if (main != null){
            // Health & Inventory
            /// The player's health
            updateProperty("health", main.getHealth());
        }

    }
}
