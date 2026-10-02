package com.mrpup.agrigen.config.machine;

import com.mrpup.agrigen.config.ModConfigMachine;
import com.mrpup.clumapi.config.ConfigFile;
import com.mrpup.clumapi.config.ConfigVal;

@ConfigFile.Child(ModConfigMachine.class)
public class AutoFarmerConfig {

    @ConfigVal(
            comment = "Low values can cause LAG - Default: [20]"
    )
    @ConfigVal.InRangeInt(min = 0, max = 2147483647)
    public static int scanInterval = 20;

    @ConfigVal(
            comment = "Amount of Power Consumed per Tick - Default: [5FE]"
    )
    @ConfigVal.InRangeInt(min = 0, max = 2147483647)
    public static int powerPerTick = 5;

    @ConfigVal(
            comment = "Max Stored Power [FE] - Default: [10000 FE]"
    )
    @ConfigVal.InRangeInt(min = 1000, max = 2147483647)
    public static int maxStoredPower = 10000;
}
