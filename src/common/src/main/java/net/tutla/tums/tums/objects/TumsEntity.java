package net.tutla.tums.tums.objects;


import net.minecraft.world.entity.Entity;
import net.tutla.tums.tusan.Variable;

import java.util.Objects;

public class TumsEntity extends Variable {
    public Entity main;
    public TumsEntity(String name, Entity entity) {
        super(name, entity, null);
        this.main = entity;
        setProps();
    }

    public void setProps(){
        updateProperty("name", Objects.requireNonNull(main.getDisplayName()).getString());
        updateProperty("nametag", main.getCustomName());
        updateProperty("uuid", main.getUUID());

        // Health & Inventory

        // Position
        updateProperty("x", main.getX());
        updateProperty("y", main.getY());
        updateProperty("z", main.getY());
        updateProperty("yaw", main.getYRot());
        updateProperty("pitch", main.getXRot());

        updateProperty("javaclass", main);
    }
}
