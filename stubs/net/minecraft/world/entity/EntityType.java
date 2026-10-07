package net.minecraft.world.entity;
import net.minecraft.tags.TagKey;
public class EntityType<T extends Entity> { public boolean is(TagKey<EntityType<?>> tag) { return false; } }
