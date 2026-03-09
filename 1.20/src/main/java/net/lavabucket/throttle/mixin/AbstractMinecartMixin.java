/*
 * Copyright (C) 2021 Nick Iacullo
 *
 * This file is part of Throttle.
 *
 * Throttle is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Throttle is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Throttle.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.lavabucket.throttle.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.lavabucket.throttle.ThrottleConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.extensions.IForgeAbstractMinecart;

/**
 * The mixin for modifying minecart behavior.
 */
@Mixin(AbstractMinecart.class)
public abstract class AbstractMinecartMixin extends Entity implements IForgeAbstractMinecart {

    @Shadow protected abstract void moveAlongTrack(BlockPos blockPos, BlockState blockState);
    @Shadow protected abstract void comeOffTrack();

    /**
     * The remaining distance this minecart has to move during this tick. Only non-zero while
     * {@link #onMoveAlongTrack(CallbackInfo)} is running.
     */
    protected double remainingTickDistance = 0;

    /**
     * Fake constructor to allow compilation.
     *
     * @param type  the entity type of this entity
     * @param level  the level in which this entity exists
     */
	protected AbstractMinecartMixin(EntityType<?> type, Level level) {
        super(type, level);
    }

    /**
     * Executes multiple iterations of {@link #moveAlongTrack(BlockPos, BlockState)} to achieve
     * speeds greater than 8 m/s when necessary.
     *
     * @param info  the {@link CallbackInfo} for this injected method
     */
    @Inject(method = "moveAlongTrack", at = @At("TAIL"))
    protected void onMoveAlongTrack(CallbackInfo info) {
        if (getCurrentCartSpeedCapOnRail() <= 0.4 || remainingTickDistance != 0 || super.level.isClientSide()) {
            return;
        }

        double maxSpeed = ThrottleConfig.CONFIG.maxSpeed.get() / 20;
        Vec3 velocity = this.getDeltaMovement();
        double xSpeed = Math.abs(velocity.x());
        double zSpeed = Math.abs(velocity.z());
        double largestSpeed = Math.max(xSpeed, zSpeed);
        remainingTickDistance = Math.min(maxSpeed, largestSpeed);
        // Subtract 0.4 since moveAlongTrack has already run once.
        remainingTickDistance -= 0.4;

        while (remainingTickDistance > 0.0001) {
            Tuple<BlockPos, BlockState> rail = getRail();
            if (rail == null) {
                this.comeOffTrack();
                break;
            }

            this.moveAlongTrack(rail.getA(), rail.getB());
            remainingTickDistance -= 0.4;
        }
        remainingTickDistance = 0;
    }

    /** {@return the rail that should be used for movement calculations, or null if none exists} */
    protected Tuple<BlockPos, BlockState> getRail() {
        if (!this.canUseRail()) {
            return null;
        }

        int x = (int) Math.floor(this.getX());
        int y = (int) Math.floor(this.getY());
        int z = (int) Math.floor(this.getZ());

        BlockPos pos = new BlockPos(x, y - 1, z);
        BlockState state = this.level.getBlockState(pos);
        if (BaseRailBlock.isRail(state)) {
            return new Tuple<>(pos, state);
        }

        pos = new BlockPos(x, y, z);
        state = this.level.getBlockState(pos);
        if (BaseRailBlock.isRail(state)) {
            return new Tuple<>(pos, state);
        }

        return null;
    }

    /**
     * Works with {@link #onMoveAlongTrack(CallbackInfo)} to iterate speeds over 8 m/s. Accounts
     * for water drag, top speeds below 8 m/s, and speeds of uneven multiples of 8 m/s.
     *
     * @return the top speed to move during this tick/interval
     */
    @Override
    public double getMaxSpeedWithRail() {
        double maxSpeed = Math.min(0.4, ThrottleConfig.CONFIG.maxSpeed.get() / 20);
        if (remainingTickDistance > 0) {
            maxSpeed = Math.min(0.4, remainingTickDistance);
        }
        if (this.isInWater()) {
            maxSpeed /= 2;
        }
        return maxSpeed;
    }

    /**
     * Modifies vanilla's momentum cap to match the new max speed.
     * <p>
     * Vanilla prevents minecart from going above [5 x top speed]. Since this mod changes top the
     * top speed, this value has to be adjusted too, or momentum would remain capped at 40 m/s.
     *
     * @param oldValue  the original capped momentum
     * @return the new capped momentum
     */
	@ModifyVariable(method = "moveAlongTrack", at = @At("STORE"), ordinal = 8)
	protected double injectedMoveAlongTrack(double oldValue) {
		double speed = this.getDeltaMovement().horizontalDistance();
        double maxSpeed = ThrottleConfig.CONFIG.maxSpeed.get();
        return Math.min(speed, maxSpeed / 4);
	}

}
