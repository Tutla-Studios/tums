package net.tutla.tums.tums.objects;


import net.minecraft.world.entity.Entity;
import net.tutla.tums.tusan.Variable;

import java.util.Objects;

/// This is the Tums variation of the Minecraft Entity. Here are all of it's *unique* properties:
///
/// <!-- properties -->
///
/// @tums.title TumsEntity
/// @tums.summary The TumsEntity type documentation
public class TumsEntity extends Variable {
    public Entity main;
    public TumsEntity(String name, Entity entity) {
        super(name, entity, null);
        this.main = entity;
        setProps();
    }

    public void setProps(){
        /// The name of the entity
        updateProperty("name", Objects.requireNonNull(main.getDisplayName()).getString());
        /// The nametag name of the entity
        updateProperty("nametag", main.getCustomName());
        /// Entity's UUID
        updateProperty("uuid", main.getUUID());

        // Health & Inventory

        // Position
        /// The X coordinate of the entity
        updateProperty("x", main.getX());
        /// The Y coordinate of the entity
        updateProperty("y", main.getY());
        /// The Z coordinate of the entity
        updateProperty("z", main.getZ());
        /// The yaw of the entity
        updateProperty("yaw", main.getYRot());
        /// The pitch of the entity
        updateProperty("pitch", main.getXRot());

        /// The Minecraft entity itself, for addons working in Java
        updateProperty("javaclass", main);
    }
}
