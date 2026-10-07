package net.minecraft.world.level;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
public interface LevelAccessor { <T extends Entity> List<T> getEntitiesOfClass(Class<T> type, AABB box, Predicate<? super T> filter); }
