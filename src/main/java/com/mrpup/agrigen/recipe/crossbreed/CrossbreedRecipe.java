package com.mrpup.agrigen.recipe.crossbreed;


import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record CrossbreedRecipe(String parent1, String parent2, String result, int chance) {

    public static final Codec<CrossbreedRecipe> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.STRING.fieldOf("parent1").forGetter(CrossbreedRecipe::parent1),
            Codec.STRING.fieldOf("parent2").forGetter(CrossbreedRecipe::parent2),
            Codec.STRING.fieldOf("result").forGetter(CrossbreedRecipe::result),
            Codec.INT.optionalFieldOf("chance", 10).forGetter(CrossbreedRecipe::chance)
    ).apply(inst, CrossbreedRecipe::new));

    public boolean matches(String a, String b) {
        return (parent1.equals(a) && parent2.equals(b)) || (parent1.equals(b) && parent2.equals(a));
    }
}
