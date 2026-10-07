package dev.nimura.patches;

import java.lang.reflect.Method;
import net.neoforged.fml.ModList;

/**
 * Asks RoadWeaver whether its "Initial Generation" panel is on screen, using the same three checks its
 * LoadingGenerationOverlayRenderer.render makes (loading progress enabled, InitialGenManager active,
 * progress snapshot active). Reflection keeps Nimura Patches free of a hard dependency: without RoadWeaver,
 * or if its classes change, this always answers false and the vanilla loading screen is left alone.
 */
public final class RoadWeaverOverlay {
    private static boolean resolved;
    private static boolean broken;
    private static Method configGet, progressEnabled, isActive, getSnapshot, snapshotActive;

    private RoadWeaverOverlay() {}

    public static boolean isShowing() {
        if (!resolved) resolve();
        if (broken) return false;
        try {
            Object config = configGet.invoke(null);
            if (config != null && !(boolean) progressEnabled.invoke(config)) return false;
            if (!(boolean) isActive.invoke(null)) return false;
            Object snapshot = getSnapshot.invoke(null);
            return snapshot != null && (boolean) snapshotActive.invoke(snapshot);
        } catch (Throwable t) {
            broken = true;
            NimuraPatches.LOGGER.warn("RoadWeaver loading-screen check failed; showing the vanilla chunk map again", t);
            return false;
        }
    }

    private static void resolve() {
        resolved = true;
        if (!ModList.get().isLoaded("roadweaver")) {
            broken = true;
            return;
        }
        try {
            ClassLoader cl = RoadWeaverOverlay.class.getClassLoader();
            Class<?> configService = Class.forName("net.shiroha233.roadweaver.config.ConfigService", false, cl);
            Class<?> modConfig = Class.forName("net.shiroha233.roadweaver.config.ModConfig", false, cl);
            Class<?> manager = Class.forName("net.shiroha233.roadweaver.generation.InitialGenManager", false, cl);
            Class<?> snapshot = Class.forName("net.shiroha233.roadweaver.generation.progress.InitialGenerationProgressSnapshot", false, cl);
            configGet = configService.getMethod("get");
            progressEnabled = modConfig.getMethod("loadingProgressEnabled");
            isActive = manager.getMethod("isActive");
            getSnapshot = manager.getMethod("getProgressSnapshot");
            snapshotActive = snapshot.getMethod("active");
        } catch (Throwable t) {
            broken = true;
            NimuraPatches.LOGGER.warn("RoadWeaver is installed but its loading-screen API changed; leaving the vanilla chunk map alone", t);
        }
    }
}
