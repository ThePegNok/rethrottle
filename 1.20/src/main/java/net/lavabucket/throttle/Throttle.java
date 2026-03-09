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

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/** This class contains the mod entry point. */
@Mod(Throttle.MOD_ID)
public final class Throttle {

    /** Mod identifier. The value here should match an entry in the META-INF/mods.toml file. */
    public static final String MOD_ID = "throttle";

    /** Mod entry point. */
    public Throttle() {
        final IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.register(ThrottleConfig.class);
    }

}
