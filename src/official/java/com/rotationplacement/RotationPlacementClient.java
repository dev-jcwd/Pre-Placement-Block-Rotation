package com.rotationplacement;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;

public class RotationPlacementClient implements ClientModInitializer {

    public static KeyMapping toggleKey, rotateXKey, rotateYKey;

    @Override
    public void onInitializeClient() {
        toggleKey = KeyMappingHelper.registerKeyMapping(Compat.createKeyMapping("key.rotationplacement.toggle", "key.keyboard.r"));
        rotateXKey = KeyMappingHelper.registerKeyMapping(Compat.createKeyMapping("key.rotationplacement.rotate_x", "key.keyboard.up"));
        rotateYKey = KeyMappingHelper.registerKeyMapping(Compat.createKeyMapping("key.rotationplacement.rotate_y", "key.keyboard.right"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            RotationState.setPlacer(client.player == null ? null : client.player.getUUID());

            while (toggleKey.consumeClick()) {
                RotationState.setEnabled(!RotationState.isEnabled());
                if (client.player != null) {
                    String text = RotationState.isEnabled() ? "§aPlacement Rotation Active!" : "§cPlacement Rotation Disabled!";
                    if (RotationState.isEnabled() && !client.hasSingleplayerServer()) {
                        text += " §e(singleplayer only, a server decides placement itself)";
                    }
                    client.player.sendOverlayMessage(Component.literal(text));
                }
            }

            // Always drain the rotate keys. Otherwise presses made while the mod is off would pile up and fire at once
            // the next time it is switched on.
            while (rotateXKey.consumeClick()) {
                if (RotationState.isEnabled()) RotationState.rotateX();
            }
            while (rotateYKey.consumeClick()) {
                if (RotationState.isEnabled()) RotationState.rotateY();
            }

            // Work out the preview once per tick instead of once per rendered frame.
            GhostPreview.update(client);
        });

        Compat.registerRenderer();
    }
}
