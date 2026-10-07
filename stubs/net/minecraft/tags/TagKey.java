package net.minecraft.tags;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
public record TagKey<T>(ResourceKey<?> registry, ResourceLocation location) {
    public static <T> TagKey<T> create(ResourceKey<?> registry, ResourceLocation location) { return null; }
}
