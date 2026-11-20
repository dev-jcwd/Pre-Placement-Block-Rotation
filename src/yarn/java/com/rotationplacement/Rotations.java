package com.rotationplacement;

import net.minecraft.block.BlockState;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.block.enums.ChestType;
import net.minecraft.block.enums.SlabType;
import net.minecraft.state.property.Property;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;

/** The "brain": turns the block state vanilla would place into the state the player asked for. */
public final class Rotations {
    private Rotations() {}

    /** Returns the rotated state, or the same instance when nothing changes. */
    public static BlockState apply(BlockState state) {
        if (state == null) return null;

        // Read once: the values can change on the client thread while the server thread is placing a block.
        final int x = RotationState.rotationX();
        final int y = RotationState.rotationY();
        if (x == 0 && y == 0) return state;

        BlockState result = state;

        if (state.contains(Properties.FACING)) {
            // 1. All-directional blocks (observers, pistons, dispensers)
            Direction dir = state.get(Properties.FACING);

            // Break out of the up/down lock when the player wants to spin sideways
            if (dir.getAxis() == Direction.Axis.Y && y != 0) dir = Direction.NORTH;

            for (int i = 0; i < y / 90; i++) dir = dir.rotateYClockwise();

            if (x == 90) dir = Direction.UP;
            else if (x == 270) dir = Direction.DOWN;
            else if (x == 180) dir = dir.getOpposite();

            result = with(result, Properties.FACING, dir);
        } else if (state.contains(Properties.HORIZONTAL_FACING)) {
            // 2. Horizontal blocks (furnaces, chests, stairs, trapdoors)
            Direction dir = state.get(Properties.HORIZONTAL_FACING);
            for (int i = 0; i < y / 90; i++) dir = dir.rotateYClockwise();
            if (x == 180) dir = dir.getOpposite();

            result = with(result, Properties.HORIZONTAL_FACING, dir);

            // A chest decided its left/right half from the vanilla facing, so after turning it, place it as a single chest.
            if (result != state && result.contains(Properties.CHEST_TYPE)) {
                result = with(result, Properties.CHEST_TYPE, ChestType.SINGLE);
            }
        } else if (state.contains(Properties.AXIS)) {
            // 3. Logs and pillars
            Direction.Axis axis = state.get(Properties.AXIS);
            if (x == 90 || x == 270) axis = (axis == Direction.Axis.Y) ? Direction.Axis.X : Direction.Axis.Y;
            if (y == 90 || y == 270) axis = (axis == Direction.Axis.X) ? Direction.Axis.Z : (axis == Direction.Axis.Z ? Direction.Axis.X : axis);

            result = with(result, Properties.AXIS, axis);
        }

        // 4. Upside-down flipping: stairs and trapdoors use BLOCK_HALF, slabs use SLAB_TYPE
        if (x == 90 || x == 270) {
            if (result.contains(Properties.BLOCK_HALF)) {
                BlockHalf half = result.get(Properties.BLOCK_HALF);
                result = with(result, Properties.BLOCK_HALF, half == BlockHalf.TOP ? BlockHalf.BOTTOM : BlockHalf.TOP);
            }
            if (result.contains(Properties.SLAB_TYPE)) {
                SlabType type = result.get(Properties.SLAB_TYPE);
                if (type == SlabType.TOP) result = with(result, Properties.SLAB_TYPE, SlabType.BOTTOM);
                else if (type == SlabType.BOTTOM) result = with(result, Properties.SLAB_TYPE, SlabType.TOP);
            }
        }

        return result;
    }

    /** state.with() throws if a block only allows some of a property's values, so check first. */
    private static <T extends Comparable<T>> BlockState with(BlockState state, Property<T> property, T value) {
        return property.getValues().contains(value) ? state.with(property, value) : state;
    }
}
