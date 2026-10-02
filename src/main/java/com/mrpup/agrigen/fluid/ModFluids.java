package com.mrpup.agrigen.fluid;

import com.mrpup.clumapi.fluids.FluidsHolder;
import com.mrpup.clumapi.fluids.RegFluids;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidType;

public class ModFluids {
    public static final FluidsHolder LIQUID_DNA = RegFluids.reg(
            "liquid_dna",
            () -> FluidType.Properties.create()
                    .descriptionId("fluid.agrigen.liquid_dna")
                    .canConvertToSource(false)
                    .supportsBoating(false)
                    .canSwim(true)
                    .canPushEntity(true)
                    .density(1200)
                    .viscosity(1500)
                    .lightLevel(0),
            new IClientFluidTypeExtensions() {},
            0xFF4B0082
    );

    public static void register() {

    }
}
