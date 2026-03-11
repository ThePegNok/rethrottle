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

import net.minecraft.world.entity.vehicle.MinecartFurnace;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecartFurnace.class)
public class MinecartFurnaceMixin {

    @Inject(method = "getMaxCartSpeedOnRail", at = @At("HEAD"), cancellable = true, remap = false)
    private void getMaxCartSpeedOnRail(CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(1.2F);
    }
}
