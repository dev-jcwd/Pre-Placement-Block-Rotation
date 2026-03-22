package com.rotationplacement.mixin;

import com.rotationplacement.RotationPlacementClient;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin {
    @Inject(method = "getPlacementState", at = @At("RETURN"), cancellable = true)
    private void forceRotationOverrides(ItemPlacementContext context, CallbackInfoReturnable<BlockState> cir) {
        if (RotationPlacementClient.isModEnabled) {
            BlockState vanillaState = cir.getReturnValue();

            // Send the state to the central brain to be rotated, then return it!
            BlockState rotatedState = RotationPlacementClient.applyCustomRotations(vanillaState);

            if (rotatedState != null) {
                cir.setReturnValue(rotatedState);
            }
        }
    }
}