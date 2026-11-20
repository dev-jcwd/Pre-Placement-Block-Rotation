package com.rotationplacement;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.SlabType;

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

        if (state.hasProperty(BlockStateProperties.FACING)) {
            // 1. All-directional blocks (observers, pistons, dispensers)
            Direction dir = state.getValue(BlockStateProperties.FACING);

            // Break out of the up/down lock when the player wants to spin sideways
            if (dir.getAxis() == Direction.Axis.Y && y != 0) dir = Direction.NORTH;

            for (int i = 0; i < y / 90; i++) dir = dir.getClockWise();

            if (x == 90) dir = Direction.UP;
            else if (x == 270) dir = Direction.DOWN;
            else if (x == 180) dir = dir.getOpposite();

            result = with(result, BlockStateProperties.FACING, dir);
        } else if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            // 2. Horizontal blocks (furnaces, chests, stairs, trapdoors)
            Direction dir = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            for (int i = 0; i < y / 90; i++) dir = dir.getClockWise();
            if (x == 180) dir = dir.getOpposite();

            result = with(result, BlockStateProperties.HORIZONTAL_FACING, dir);

            // A chest decided its left/right half from the vanilla facing, so after turning it, place it as a single chest.
            if (result != state && result.hasProperty(BlockStateProperties.CHEST_TYPE)) {
                result = with(result, BlockStateProperties.CHEST_TYPE, ChestType.SINGLE);
            }
        } else if (state.hasProperty(BlockStateProperties.AXIS)) {
            // 3. Logs and pillars
            Direction.Axis axis = state.getValue(BlockStateProperties.AXIS);
            if (x == 90 || x == 270) axis = (axis == Direction.Axis.Y) ? Direction.Axis.X : Direction.Axis.Y;
            if (y == 90 || y == 270) axis = (axis == Direction.Axis.X) ? Direction.Axis.Z : (axis == Direction.Axis.Z ? Direction.Axis.X : axis);

            result = with(result, BlockStateProperties.AXIS, axis);
        }

        // 4. Upside-down flipping: stairs and trapdoors use HALF, slabs use SLAB_TYPE
        if (x == 90 || x == 270) {
            if (result.hasProperty(BlockStateProperties.HALF)) {
                Half half = result.getValue(BlockStateProperties.HALF);
                result = with(result, BlockStateProperties.HALF, half == Half.TOP ? Half.BOTTOM : Half.TOP);
            }
            if (result.hasProperty(BlockStateProperties.SLAB_TYPE)) {
                SlabType type = result.getValue(BlockStateProperties.SLAB_TYPE);
                if (type == SlabType.TOP) result = with(result, BlockStateProperties.SLAB_TYPE, SlabType.BOTTOM);
                else if (type == SlabType.BOTTOM) result = with(result, BlockStateProperties.SLAB_TYPE, SlabType.TOP);
            }
        }

        return result;
    }

    /** setValue() throws if a block only allows some of a property's values, so check first. */
    private static <T extends Comparable<T>> BlockState with(BlockState state, Property<T> property, T value) {
        return property.getPossibleValues().contains(value) ? state.setValue(property, value) : state;
    }
}
