package com.rotationplacement;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class RotationPlacementClient implements ClientModInitializer {

    public static KeyBinding toggleKey, rotateXKey, rotateYKey;

    @Override
    public void onInitializeClient() {
        toggleKey = KeyBindingHelper.registerKeyBinding(Compat.createKeyBinding("key.rotationplacement.toggle", GLFW.GLFW_KEY_R));
        rotateXKey = KeyBindingHelper.registerKeyBinding(Compat.createKeyBinding("key.rotationplacement.rotate_x", GLFW.GLFW_KEY_UP));
        rotateYKey = KeyBindingHelper.registerKeyBinding(Compat.createKeyBinding("key.rotationplacement.rotate_y", GLFW.GLFW_KEY_RIGHT));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            RotationState.setPlacer(client.player == null ? null : client.player.getUuid());

            while (toggleKey.wasPressed()) {
                RotationState.setEnabled(!RotationState.isEnabled());
                if (client.player != null) {
                    String text = RotationState.isEnabled() ? "§aPlacement Rotation Active!" : "§cPlacement Rotation Disabled!";
                    if (RotationState.isEnabled() && !client.isInSingleplayer()) {
                        text += " §e(singleplayer only, a server decides placement itself)";
                    }
                    client.player.sendMessage(Text.literal(text), true);
                }
            }

            // Always drain the rotate keys. Otherwise presses made while the mod is off would pile up and fire at once
            // the next time it is switched on.
            while (rotateXKey.wasPressed()) {
                if (RotationState.isEnabled()) RotationState.rotateX();
            }
            while (rotateYKey.wasPressed()) {
                if (RotationState.isEnabled()) RotationState.rotateY();
            }

            // Work out the preview once per tick instead of once per rendered frame.
            GhostPreview.update(client);
        });

        Compat.registerRenderer();
    }
}
