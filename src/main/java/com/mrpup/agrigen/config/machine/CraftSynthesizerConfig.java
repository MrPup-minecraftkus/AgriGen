package com.mrpup.agrigen.config.machine;

import com.mrpup.agrigen.config.ModConfigMachine;
import com.mrpup.clumapi.config.ConfigFile;
import com.mrpup.clumapi.config.ConfigVal;

@ConfigFile.Child(ModConfigMachine.class)
public class CraftSynthesizerConfig {

    @ConfigVal(
            comment = "Cooldown Time in Ticks [20 Ticks per Second] - Default: [100 (5s)]"
    )
    @ConfigVal.InRangeInt(min = 0, max = 2147483647)
    public static int maxProgress = 100;

    @ConfigVal(
            comment = "Amount of Power Consumed per Tick - Default: [200FE]"
    )
    @ConfigVal.InRangeInt(min = 0, max = 2147483647)
    public static int powerPerTick = 200;

    @ConfigVal(
            comment = "Max Stored Power [FE] - Default: [20000 FE]"
    )
    @ConfigVal.InRangeInt(min = 1000, max = 2147483647)
    public static int maxStoredPower = 20000;
}
