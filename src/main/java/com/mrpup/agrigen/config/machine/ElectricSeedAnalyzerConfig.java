package com.mrpup.agrigen.config.machine;

import com.mrpup.clumapi.config.ConfigFile;
import com.mrpup.clumapi.config.ConfigVal;
import com.mrpup.agrigen.config.ModConfigMachine;

@ConfigFile.Child(ModConfigMachine.class)
public class ElectricSeedAnalyzerConfig {

    @ConfigVal(
            comment = "Cooldown Time in Ticks [20 Ticks per Second] - Default: [100 (5s)]"
    )
    @ConfigVal.InRangeInt(min = 0, max = 2147483647)
    public static int maxProgress = 100;

    @ConfigVal(
            comment = "Amount of Power Consumed per Tick - Default: [40FE]"
    )
    @ConfigVal.InRangeInt(min = 0, max = 2147483647)
    public static int powerPerTick = 40;

    @ConfigVal(
            comment = "Max Stored Power [FE] - Default: [10000 FE]"
    )
    @ConfigVal.InRangeInt(min = 1000, max = 2147483647)
    public static int maxStoredPower = 10000;
}
