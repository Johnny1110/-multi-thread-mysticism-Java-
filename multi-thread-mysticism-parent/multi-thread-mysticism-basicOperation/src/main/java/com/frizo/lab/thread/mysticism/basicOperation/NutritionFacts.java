package com.frizo.lab.thread.mysticism.basicOperation;

public class NutritionFacts {

    private final int servingSize;  // (ml)             required
    private final int serving;      // (per container)  required
    private final int calories;     // (per serving)    optional
    private final int fat;          // (g/serving)      optional
    private final int sodium;       // (mg/serving)     optional
    private final int carbohydrate; // (g/serving)      optional

    public NutritionFacts(Builder builder) {
        this.servingSize = builder.servingSize;
        this.serving = builder.servings;
        this.calories = builder.calories;
        this.fat = builder.fat;
        this.sodium = builder.sodium;
        this.carbohydrate = builder.carbohydrate;
    }

    public static class Builder {
        private final int servingSize;
        private final int servings;
        private int calories;
        private int fat;
        private int sodium;
        private int carbohydrate;

        public Builder(int servingSize, int servings){
            this.servingSize = servingSize;
            this.servings = servings;
        }

        public Builder calories(int calories) {
            this.calories = calories;
            return this;
        }

        public Builder fat(int fat) {
            this.fat = fat;
            return this;
        }

        public Builder sodium(int sodium) {
            this.sodium = sodium;
            return this;
        }

        public Builder carbohydrate(int carbohydrate) {
            this.carbohydrate = carbohydrate;
            return this;
        }

        public NutritionFacts build() {
            return new NutritionFacts(this);
        }
    }

    public static void main(String[] args) {
        NutritionFacts facts = new Builder(1000, 5)
                .calories(149)
                .fat(14)
                .sodium(11)
                .carbohydrate(200)
                .build();
    }
}
