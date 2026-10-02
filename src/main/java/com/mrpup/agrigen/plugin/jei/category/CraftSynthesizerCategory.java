package com.mrpup.agrigen.plugin.jei.category;

import com.mrpup.agrigen.block.MachineBlockTile;
import com.mrpup.agrigen.block.ModBlocks;
import com.mrpup.agrigen.block.tile.CraftSynthesizerTile;
import com.mrpup.agrigen.plugin.jei.AgriGenJeiPlugin;
import com.mrpup.agrigen.recipe.machine.CraftSynthesizerRecipe;
import com.mrpup.clumapi.component.progress.ProgressTypes;
import com.mrpup.clumapi.component.slot.SlotTypes;
import com.mrpup.clumapi.component.storage.StorageTypes;
import com.mrpup.clumapi.component.tank.FluidTankTypes;
import com.mrpup.clumapi.plugin.jei.JeiSlotLayout;
import com.mrpup.clumapi.plugin.jei.renderer.JeiCustomRenderer;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.fluids.FluidStack;

public class CraftSynthesizerCategory implements IRecipeCategory<RecipeHolder<CraftSynthesizerRecipe>> {

    private final IDrawable icon;
    private final IDrawable tankSmallOverlay;
    private final JeiSlotLayout<CraftSynthesizerRecipe> layout;

    public CraftSynthesizerCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(ModBlocks.CRAFT_SYNTHESIZER.getBlock()));
        this.tankSmallOverlay = guiHelper.createDrawable(FluidTankTypes.BG, 196, 1, 18, 19);

        this.layout = new JeiSlotLayout<CraftSynthesizerRecipe>()
                .itemSlot(RecipeIngredientRole.INPUT, CraftSynthesizerTile.INPUT_1_X - 5, CraftSynthesizerTile.INPUT_1_Y - 10, CraftSynthesizerRecipe::input1)
                .itemSlot(RecipeIngredientRole.INPUT, CraftSynthesizerTile.INPUT_2_X - 5, CraftSynthesizerTile.INPUT_2_Y - 10, CraftSynthesizerRecipe::input2)
                .itemSlot(RecipeIngredientRole.INPUT, CraftSynthesizerTile.INPUT_3_X - 5, CraftSynthesizerTile.INPUT_3_Y - 10, CraftSynthesizerRecipe::input3)
                .itemSlot(RecipeIngredientRole.INPUT, CraftSynthesizerTile.INPUT_4_X - 5, CraftSynthesizerTile.INPUT_4_Y - 10, CraftSynthesizerRecipe::input4)
                .fluidSlot(
                        RecipeIngredientRole.INPUT,
                        CraftSynthesizerTile.FLUID_IN_X - 5,
                        CraftSynthesizerTile.FLUID_IN_Y - 10,
                        16,
                        16,
                        r -> r.fluidInput().ingredient().fluids().stream()
                                .map(fluid -> new FluidStack(fluid, r.fluidInput().amount()))
                                .toArray(FluidStack[]::new),
                        r -> r.fluidInput().amount(),
                        tankSmallOverlay,
                        0,
                        0
                )
                .outputItemSlot(CraftSynthesizerTile.OUTPUT_X - 5, CraftSynthesizerTile.OUTPUT_Y - 10, recipe -> recipe.output().create());
    }

    @Override
    public IRecipeType<RecipeHolder<CraftSynthesizerRecipe>> getRecipeType() {
        return AgriGenJeiPlugin.CRAFT_SYNTHESIZER;
    }

    @Override
    public Component getTitle() {
        return Component.translatable(ModBlocks.CRAFT_SYNTHESIZER.getBlock().getDescriptionId());
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<CraftSynthesizerRecipe> recipe, IFocusGroup focuses) {
        layout.apply(builder, recipe.value());
    }

    @Override
    public int getWidth() {
        return 160;
    }

    @Override
    public int getHeight() {
        return 82;
    }

    @Override
    public void draw(RecipeHolder<CraftSynthesizerRecipe> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
        SlotTypes.SLOT_ITEM.render(guiGraphics, CraftSynthesizerTile.INPUT_1_X - 5, CraftSynthesizerTile.INPUT_1_Y- 10);
        SlotTypes.SLOT_ITEM.render(guiGraphics, CraftSynthesizerTile.INPUT_2_X- 5, CraftSynthesizerTile.INPUT_2_Y- 10);
        SlotTypes.SLOT_ITEM.render(guiGraphics, CraftSynthesizerTile.INPUT_3_X- 5, CraftSynthesizerTile.INPUT_3_Y- 10);
        SlotTypes.SLOT_ITEM.render(guiGraphics, CraftSynthesizerTile.INPUT_4_X- 5, CraftSynthesizerTile.INPUT_4_Y- 10);

        JeiCustomRenderer.multiSlot(CraftSynthesizerTile.OUTPUT_X - 5, CraftSynthesizerTile.OUTPUT_Y - 10, 1, 3, SlotTypes.OUTPUT_SLOT_ITEM, guiGraphics);

        FluidTankTypes.TankType.TANK.render(guiGraphics, CraftSynthesizerTile.FLUID_OUT_X- 9, CraftSynthesizerTile.FLUID_OUT_Y- 10);

        StorageTypes.StorageType.ENERGY_STORAGE.render(guiGraphics, MachineBlockTile.ENERGY_X - 5, MachineBlockTile.ENERGY_Y - 10);
        StorageTypes.StorageType.ENERGY_STORAGE_FULL.render(guiGraphics, MachineBlockTile.ENERGY_X - 2, MachineBlockTile.ENERGY_Y - 7);

        ProgressTypes.ProgressType.ARROW.render(guiGraphics, MachineBlockTile.getProgressX() + 28, MachineBlockTile.getProgressY() - 11);
    }
}