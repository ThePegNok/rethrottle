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

package net.lavabucket.throttle;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.ModLoadingContext;

/**
 * Mod configuration class.
 */
public final class ThrottleConfig {

    private static final Builder BUILDER = new Builder();
    /** The mod configuration value holder. */
    public static final ThrottleConfig CONFIG = new ThrottleConfig(BUILDER);
    /** The mod configuration spec. */
    public static final ForgeConfigSpec SPEC = BUILDER.build();

    /** The minecart max speed. */
    public final DoubleValue maxSpeed;

    /**
     * Constructs an instance of a server configuration.
     * @param builder  a Forge config builder instance
     */
    private ThrottleConfig(final ForgeConfigSpec.Builder builder) {
        maxSpeed = builder
            .comment("The minecart speed cap in m/s. In vanilla value is 8.0 m/s.")
            .defineInRange("maxSpeed", 16D, 0D, Double.MAX_VALUE);
    }

    /**
     * Register this class's configs with the mod loading context.
     * @param event  the event, provided by the mod event bus
     */
    @SubscribeEvent
    public static void onConstructModEvent(final FMLConstructModEvent event) {
        final ModLoadingContext context = ModLoadingContext.get();
        context.registerConfig(ModConfig.Type.SERVER, SPEC);
    }

}
