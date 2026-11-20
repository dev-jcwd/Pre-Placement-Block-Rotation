package com.rotationplacement.mixin;

import com.rotationplacement.GhostPreview;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueueImpl;
import net.minecraft.client.render.state.WorldRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Draws the ghost block on Minecraft 1.21.9, which has no Fabric world render event.
 * It piggybacks on renderBlockEntities, which already hands us the camera-relative matrices and the command queue.
 */
@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {
    @Inject(method = "renderBlockEntities", at = @At("RETURN"))
    private void rotationplacement$renderGhost(MatrixStack matrices, WorldRenderState renderStates, OrderedRenderCommandQueueImpl queue, CallbackInfo ci) {
        BlockState state = GhostPreview.state;
        BlockPos pos = GhostPreview.pos;
        if (state == null || pos == null) return;

        Vec3d camera = MinecraftClient.getInstance().gameRenderer.getCamera().getCameraPos();
        matrices.push();
        matrices.translate(pos.getX() - camera.x, pos.getY() - camera.y, pos.getZ() - camera.z);
        queue.submitBlock(matrices, state, 15728880, OverlayTexture.DEFAULT_UV, 0);
        matrices.pop();
    }
}
