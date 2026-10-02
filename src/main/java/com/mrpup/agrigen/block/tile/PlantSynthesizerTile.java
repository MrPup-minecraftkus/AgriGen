package com.mrpup.agrigen.block.tile;

import com.mrpup.agrigen.block.MachineBlockTile;
import com.mrpup.agrigen.block.ModBlocks;
import com.mrpup.agrigen.component.slot.ModSlotTypes;
import com.mrpup.agrigen.config.machine.PlantSynthesizerConfig;
import com.mrpup.agrigen.plant.AllHelper;
import com.mrpup.agrigen.fluid.ModFluids;
import com.mrpup.agrigen.item.genetic.GeneticTemplateItem;
import com.mrpup.clumapi.component.InventoryComponent;
import com.mrpup.clumapi.component.progress.ProgressTypes;
import com.mrpup.clumapi.component.slot.SlotTypes;
import com.mrpup.clumapi.component.tank.FluidTankComponent;
import com.mrpup.clumapi.helper.ItemHelper;
import com.mrpup.clumapi.helper.OutputHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import java.util.List;
import java.util.Optional;

public class PlantSynthesizerTile extends MachineBlockTile {

    private static final int ENERGY_CAPACITY = PlantSynthesizerConfig.maxStoredPower;
    private static final int ENERGY_PER_TICK = PlantSynthesizerConfig.powerPerTick;
    private static final int PROCESS_TICKS = PlantSynthesizerConfig.maxProgress;
    private static final int TANK_CAPACITY = 4000;

    private final InventoryComponent output;
    private final FluidTankComponent dnaTank;
    private final InventoryComponent templateGeneInput;

    public PlantSynthesizerTile(BlockPos pos, BlockState state) {
        super(ModBlocks.PLANT_SYNTHESIZER.getBlockEntityType(), pos, state, 61,  41,  PROCESS_TICKS, ProgressTypes.ProgressType.LONG_ARROW);

        setMenuType(() -> ModBlocks.PLANT_SYNTHESIZER.getMenuType());

        energyComponent(ENERGY_CAPACITY,  ENERGY_PER_TICK);

        dnaTank = new FluidTankComponent("input_tank", 35, 51, TANK_CAPACITY, 5, false, true);
        addComponent(dnaTank);

        templateGeneInput = new InventoryComponent("template_gene_input", 36, 30, 1, 1, ModSlotTypes.SLOT_GENE_TEMPLATE);
        addComponent(templateGeneInput);

        output = new InventoryComponent("output", 129, 40, 1, 1, SlotTypes.BIG_SLOT_ITEM);
        output.setAllOutput();
        addComponent(output);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide()) return;
        recipe();
        setChanged();
        level.sendBlockUpdated(pos, state, state, 3);
    }

    public void recipe() {
        ItemStack templateStack = templateGeneInput.getContainer().getItem(0);

        boolean hasTemplate = !templateStack.isEmpty() && GeneticTemplateItem.hasAllGenes(templateStack);
        boolean hasEnoughDna = dnaTank.getFluidAmount() >= 100 && dnaTank.getFluid().is(ModFluids.LIQUID_DNA.getFluidType());

        ItemStack result = hasTemplate ? buildResult(templateStack) : ItemStack.EMPTY;

        boolean canOutput = !result.isEmpty() && OutputHelper.canFit(output.getContainer(), result);

        if (canOutput && hasEnoughDna && canRecipeBySettings()) {
            boolean recipeComplete = advanceProgress(ENERGY_PER_TICK);

            if (recipeComplete && extractDna()) {
                OutputHelper.insertAll(output.getContainer(), List.of(result));
                setChanged();
            }
        } else {
            setProgressTile(0);
        }
    }

    private ItemStack buildResult(ItemStack templateStack) {
        CompoundTag genes = GeneticTemplateItem.getGenesTag(templateStack);

        Optional<String> speciesIdOpt = genes.getString("speciesId");
        if (speciesIdOpt.isEmpty()) return ItemStack.EMPTY;

        Identifier loc = Identifier.tryParse(speciesIdOpt.get());
        if (loc == null) return ItemStack.EMPTY;

        Optional<Holder.Reference<Item>> holder = ItemHelper.getItemHolderFromLoc(loc);
        if (holder.isEmpty() || holder.get().value() == Items.AIR) return ItemStack.EMPTY;

        ItemStack resultStack = new ItemStack(holder.get().value(), 1);

        CompoundTag genomeTag = new CompoundTag();
        for (String key : AllHelper.GENE_KEYS) {
            Tag value = genes.get(key);
            if (value != null) {
                genomeTag.put(key, value.copy());
            }
        }

        CustomData existing = resultStack.get(DataComponents.CUSTOM_DATA);
        CompoundTag resultTag = existing != null ? existing.copyTag() : new CompoundTag();
        resultTag.putBoolean("analyzed", true);
        resultTag.put("genome", genomeTag);
        resultStack.set(DataComponents.CUSTOM_DATA, CustomData.of(resultTag));

        return resultStack;
    }

    private boolean extractDna() {
        FluidResource dnaResource = FluidResource.of(ModFluids.LIQUID_DNA.getSource());
        ResourceHandler<FluidResource> tank = dnaTank.getTank();

        try (Transaction tx = Transaction.open(null)) {
            int extracted = tank.extract(0, dnaResource, 100, tx);
            if (extracted == 100) {
                tx.commit();
                return true;
            }
        }
        return false;
    }
}
