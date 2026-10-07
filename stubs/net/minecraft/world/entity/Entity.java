package net.minecraft.world.entity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
public abstract class Entity {
    public BlockPos blockPosition() { return null; }
    public void discard() {}
    public EntityType<?> getType() { return null; }
    public CompoundTag getPersistentData() { return null; } // NeoForge extension
}
