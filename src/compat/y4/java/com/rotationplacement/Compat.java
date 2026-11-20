package com.rotationplacement;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;

/**
 * Minecraft 1.21.9 only. Fabric API had no world render events in this version, so the ghost is drawn by
 * WorldRendererMixin instead and there is nothing to register here.
 */
public final class Compat {
    private Compat() {}

    private static final KeyBinding.Category CATEGORY = KeyBinding.Category.create(Identifier.of("rotationplacement", "main"));

    public static KeyBinding createKeyBinding(String translationKey, int keyCode) {
        return new KeyBinding(translationKey, InputUtil.Type.KEYSYM, keyCode, CATEGORY);
    }

    public static void registerRenderer() {
        // drawn by WorldRendererMixin
    }
}
