package com.mrpup.agrigen.item;

import com.mrpup.agrigen.item.genetic.GeneticTemplateItem;
import com.mrpup.agrigen.item.upgrade.RadiusUpgradeItem;
import com.mrpup.clumapi.items.ItemsHolder;
import com.mrpup.clumapi.items.ItemsProperties;
import com.mrpup.clumapi.items.RegItems;
import com.mrpup.agrigen.item.upgrade.EfficiencyUpgradeItem;
import com.mrpup.agrigen.item.upgrade.SpeedUpgradeItem;
import net.minecraft.world.item.Item;

public class ModItems {

    public static final ItemsHolder<Item> SPEED_UPGRADE_1 = RegItems.reg("speed_upgrade_1",
            () -> new SpeedUpgradeItem(new Item.Properties().stacksTo(16), 1, 0.5f));

    public static final ItemsHolder<Item> SPEED_UPGRADE_2 = RegItems.reg("speed_upgrade_2",
            () -> new SpeedUpgradeItem(new Item.Properties().stacksTo(16), 2, 1f));

    public static final ItemsHolder<Item> SPEED_UPGRADE_3 = RegItems.reg("speed_upgrade_3",
            () -> new SpeedUpgradeItem(new Item.Properties().stacksTo(16), 3, 2f));

    public static final ItemsHolder<Item> EFFICIENCY_UPGRADE_1 = RegItems.reg("efficiency_upgrade_1",
            () -> new EfficiencyUpgradeItem(new Item.Properties().stacksTo(16), 1, 0.5f));

    public static final ItemsHolder<Item> EFFICIENCY_UPGRADE_2 = RegItems.reg("efficiency_upgrade_2",
            () -> new EfficiencyUpgradeItem(new Item.Properties().stacksTo(16), 2, 1f));

    public static final ItemsHolder<Item> EFFICIENCY_UPGRADE_3 = RegItems.reg("efficiency_upgrade_3",
            () -> new EfficiencyUpgradeItem(new Item.Properties().stacksTo(16), 3, 2f));

    public static final ItemsHolder<Item> RADIUS_UPGRADE_1 = RegItems.reg("radius_upgrade_1",
            () -> new RadiusUpgradeItem(new Item.Properties().stacksTo(16), 1, 3f));

    public static final ItemsHolder<Item> RADIUS_UPGRADE_2 = RegItems.reg("radius_upgrade_2",
            () -> new RadiusUpgradeItem(new Item.Properties().stacksTo(16), 2, 5f));

    public static final ItemsHolder<Item> RADIUS_UPGRADE_3 = RegItems.reg("radius_upgrade_3",
            () -> new RadiusUpgradeItem(new Item.Properties().stacksTo(16), 3, 7f));

    public static final ItemsHolder<Item> BLANK_UPGRADE = RegItems.reg("blank_upgrade",
            () -> new Item(new Item.Properties().stacksTo(64)));




    public static final ItemsHolder<Item> BLANK_GENE_SAMPLE =
            RegItems.reg("blank_gene_sample",
                    () -> new Item(new Item.Properties().stacksTo(64)));

    public static final ItemsHolder<Item> GENE_SAMPLE =
            RegItems.reg("gene_sample",
                    () -> new Item(new Item.Properties().stacksTo(1)));

    public static final ItemsHolder<Item> GENETIC_TEMPLATE =
            RegItems.reg("genetic_template",
                    () -> new GeneticTemplateItem(new Item.Properties().stacksTo(1)));

    public static final ItemsHolder<Item> HERBICIDE =
            RegItems.reg("herbicide",
                    () -> new Item(new Item.Properties().stacksTo(1).durability(50)));

    public static final ItemsHolder<Item> GENETIC_WASTE =
            RegItems.reg("genetic_waste",
                    () -> new Item(new Item.Properties()));

    public static final ItemsHolder<Item> FERTILIZER =
            RegItems.reg("fertilizer",
                    () -> new Item(new Item.Properties()));

    public static void register() {

    }
}
