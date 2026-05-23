package net.tutla.tums.tums.objects;


import net.minecraft.world.entity.player.Player;
import net.tutla.tums.tusan.Variable;

import java.util.HashMap;

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
            updateProperty("health", main.getHealth());
        }

    }
}
