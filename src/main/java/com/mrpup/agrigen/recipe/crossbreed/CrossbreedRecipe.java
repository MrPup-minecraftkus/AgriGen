package com.mrpup.agrigen.recipe.crossbreed;

public record CrossbreedRecipe(String parent1, String parent2, String result, int chance) {
    public boolean matches(String a, String b) {
        return (parent1.equals(a) && parent2.equals(b)) || (parent1.equals(b) && parent2.equals(a));
    }
}
