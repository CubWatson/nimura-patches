package dev.nimura.patches.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.mehvahdjukaar.moonlight.api.resources.pack.DynamicResourcesProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/**
 * Moonlight 1.21.1-3.7.1: DynamicResourcesInternals#registerProvider iterates a plain HashMultimap
 * (PROVIDERS) to check for duplicate names and then puts into it, with no locking. NeoForge constructs
 * mods in parallel, and Supplementaries and Supplementaries Squared both register providers from their
 * constructors, so one thread can modify the map while the other is iterating it. Result: an intermittent
 * ConcurrentModificationException at boot ("Supplementaries Squared has failed to load correctly"), seen
 * once in about 9 launches on 2026-10-09; the same race can also silently lose a provider.
 *
 * Fix: run the whole check-then-put under one static lock. WrapMethod is needed because the lock must
 * cover the entire method body; HEAD/RETURN injections can't hold a monitor across it. Registration only
 * happens a handful of times during mod construction, so the lock costs nothing.
 */
@Mixin(targets = "net.mehvahdjukaar.moonlight.core.pack.DynamicResourcesInternals")
public abstract class MoonlightProviderRaceMixin {
    @Unique
    private static final Object nimurapatches$LOCK = new Object();

    @WrapMethod(method = "registerProvider", require = 1)
    private static void nimurapatches$lockRegister(DynamicResourcesProvider provider, Operation<Void> original) {
        synchronized (nimurapatches$LOCK) {
            original.call(provider);
        }
    }
}
