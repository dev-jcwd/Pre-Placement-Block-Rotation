package com.rotationplacement;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/** Minecraft 26.2 and 26.3: like 26.1, but submitMovingBlock takes an outline colour. */
public final class Compat {
    private Compat() {}

    private static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(Identifier.fromNamespaceAndPath("rotationplacement", "main"));
    private static final MovingBlockRenderState RENDER_STATE = new MovingBlockRenderState();

    /** Keys are looked up by name at runtime because the numeric key codes changed in 26.3. */
    public static KeyMapping createKeyMapping(String translationKey, String keyName) {
        return new KeyMapping(translationKey, InputConstants.getKey(keyName).getValue(), CATEGORY);
    }

    public static void registerRenderer() {
        LevelRenderEvents.COLLECT_SUBMITS.register(Compat::renderGhost);
    }

    private static void renderGhost(LevelRenderContext context) {
        BlockState state = GhostPreview.state;
        BlockPos pos = GhostPreview.pos;
        Minecraft client = Minecraft.getInstance();
        ClientLevel level = client.level;
        if (state == null || pos == null || level == null) return;

        RENDER_STATE.blockState = state;
        RENDER_STATE.blockPos = pos;
        RENDER_STATE.randomSeedPos = pos;
        RENDER_STATE.biome = level.getBiome(pos);
        RENDER_STATE.cardinalLighting = level.cardinalLighting();
        RENDER_STATE.lightEngine = level.getLightEngine();

        Vec3 camera = client.gameRenderer.mainCamera().position();
        PoseStack poseStack = context.poseStack();
        poseStack.pushPose();
        poseStack.translate(pos.getX() - camera.x, pos.getY() - camera.y, pos.getZ() - camera.z);
        context.submitNodeCollector().submitMovingBlock(poseStack, RENDER_STATE, 0);
        poseStack.popPose();
    }
}
