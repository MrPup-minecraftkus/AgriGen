package com.mrpup.agrigen.config.gene;

import com.mrpup.agrigen.config.ModConfigGene;
import com.mrpup.clumapi.config.ConfigFile;
import com.mrpup.clumapi.config.ConfigVal;

@ConfigFile.Child(ModConfigGene.class)
public class GenesConfig {

    @ConfigVal(
            comment = "Maximum gene level. Default: [25]"
    )
    @ConfigVal.InRangeInt(min = 1, max = 2147483647)
    public static int growSpeed = 25;

    @ConfigVal(
            comment = "Maximum gene level. Default: [25]"
    )
    @ConfigVal.InRangeInt(min = 1, max = 2147483647)
    public static int resistance = 25;

    @ConfigVal(
            comment = "Maximum gene level. Default: [15]"
    )
    @ConfigVal.InRangeInt(min = 1, max = 2147483647)
    public static int yield = 15;

}
