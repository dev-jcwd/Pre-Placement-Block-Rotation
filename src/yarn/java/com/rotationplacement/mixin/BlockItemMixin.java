package com.rotationplacement.mixin;

import com.rotationplacement.RotationState;
import com.rotationplacement.Rotations;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin {
    @Shadow
    protected abstract boolean canPlace(ItemPlacementContext context, BlockState state);

    @Inject(method = "getPlacementState", at = @At("RETURN"), cancellable = true)
    private void rotationplacement$applyRotation(ItemPlacementContext context, CallbackInfoReturnable<BlockState> cir) {
        // Only the local player's own placements are rotated, not dispensers or other players.
        PlayerEntity player = context.getPlayer();
        if (player == null || !RotationState.appliesTo(player.getUuid())) return;

        BlockState vanilla = cir.getReturnValue();
        BlockState rotated = Rotations.apply(vanilla);

        // Vanilla already checked that the original state can be placed; check the rotated one too, otherwise
        // wall-mounted blocks (torches, ladders, buttons...) would be placed facing a wall they are not attached to.
        if (rotated != vanilla && this.canPlace(context, rotated)) {
            cir.setReturnValue(rotated);
        }
    }
}
