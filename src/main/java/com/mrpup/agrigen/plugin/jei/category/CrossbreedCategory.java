package com.mrpup.agrigen.plugin.jei.category;

import com.mrpup.agrigen.block.ModBlocks;
import com.mrpup.agrigen.plugin.jei.AgriGenRecipeTypes;
import com.mrpup.agrigen.recipe.crossbreed.CrossbreedRecipe;
import com.mrpup.clumapi.component.slot.SlotTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class CrossbreedCategory implements IRecipeCategory<CrossbreedRecipe> {

    public static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("agrigen", "textures/gui/jei/crossbreed_bg.png");

    private final IDrawable background;
    private final IDrawable icon;

    public CrossbreedCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(TEXTURE, 0, 0, 160, 82);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(ModBlocks.CROP_STICK.getBlock()));
    }

    @Override
    public RecipeType<CrossbreedRecipe> getRecipeType() {
        return AgriGenRecipeTypes.CROSSBREED;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.agrigen.crossbreed");
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
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CrossbreedRecipe recipe, IFocusGroup focuses) {
        ItemStack parent1Stack = resolveItem(recipe.parent1());
        ItemStack parent2Stack = resolveItem(recipe.parent2());
        ItemStack resultStack = resolveItem(recipe.result());

        builder.addSlot(RecipeIngredientRole.INPUT, 29, 60)
                .addItemStack(parent1Stack);

        builder.addSlot(RecipeIngredientRole.INPUT, 115, 60)
                .addItemStack(parent2Stack);

        builder.addSlot(RecipeIngredientRole.OUTPUT, 72, 56)
                .addItemStack(resultStack);
    }

    @Override
    public void draw(CrossbreedRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        this.background.draw(guiGraphics, 0, 0);
        SlotTypes.SLOT_ITEM.render(guiGraphics, 29, 60);
        SlotTypes.SLOT_ITEM.render(guiGraphics, 115, 60);
        SlotTypes.OUTPUT_SLOT_ITEM.render(guiGraphics, 72, 56);
    }

    private static ItemStack resolveItem(String id) {
        try {
            ResourceLocation loc = ResourceLocation.parse(id);
            Item item = BuiltInRegistries.ITEM.get(loc);
            return item == Items.AIR ? ItemStack.EMPTY : new ItemStack(item);
        } catch (Exception e) {
            return ItemStack.EMPTY;
        }
    }
}
