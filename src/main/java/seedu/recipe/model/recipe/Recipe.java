package seedu.recipe.model.recipe;

import static seedu.recipe.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.recipe.ui.BrowserPanel.DEFAULT_PAGE;
import static seedu.recipe.ui.UiPart.FXML_FILE_FOLDER;

import java.net.URL;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

import seedu.recipe.MainApp;
import seedu.recipe.model.tag.Tag;
import seedu.recipe.model.tag.UniqueTagList;

/**
 * Represents a Recipe in the recipe book.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Recipe {

    private final Name name;
    private final Ingredient ingredient;
    private final Instruction instruction;
    private final CookingTime cookingTime;
    private final PreparationTime preparationTime;
    private final Calories calories;
    private final Servings servings;
    private final Url url;
    private final UniqueTagList tags;

    /**
     * Every field must be present and not null.
     */
    public Recipe(Name name, Ingredient ingredient, Instruction instruction,
                  CookingTime cookingTime, PreparationTime preparationTime,
                  Calories calories, Servings servings, Url url, Set<Tag> tags) {
        requireAllNonNull(name, preparationTime, ingredient, instruction, url, tags);
        this.name = name;
        this.ingredient = ingredient;
        this.instruction = instruction;
        this.cookingTime = cookingTime;
        this.preparationTime = preparationTime;
        this.calories = calories;
        this.servings = servings;
        this.url = url;
        // protect internal tags from changes in the arg list
        this.tags = new UniqueTagList(tags);
    }

    public Name getName() {
        return name;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public Instruction getInstruction() {
        return instruction;
    }

    public CookingTime getCookingTime() {
        return cookingTime;
    }

    public PreparationTime getPreparationTime() {
        return preparationTime;
    }

    public Calories getCalories() {
        return calories;
    }

    public Servings getServings() {
        return servings;
    }

    public Url getUrl() {
        return url;
    }

    /**
     * Returns an immutable tag set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags.toSet());
    }

    public String getRecipeInHtmlFormat() {
        URL defaultPage = MainApp.class.getResource(FXML_FILE_FOLDER + "Recipe.css");

        return "<html>"
                + "<head>"
                + "<link rel='stylesheet' type='text/css' href='" + defaultPage.toExternalForm() + "' />"
                + "</head>"
                + "<body>"
                + "<h1>" + name + "</h1>"
                + "<p>" + cookingTime + "</p>"
                + "<p>" + preparationTime + "</p>"
                + "<p>" + calories + "</p>"
                + "<p>" + servings + "</p>"
                + "<p>" + ingredient + "</p>"
                + "<p>" + instruction + "</p>"
                + "</body>"
                + "</html>";
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof Recipe)) {
            return false;
        }

        Recipe otherRecipe = (Recipe) other;
        return otherRecipe.getName().equals(this.getName())
                && otherRecipe.getIngredient().equals(this.getIngredient())
                && otherRecipe.getInstruction().equals(this.getInstruction())
                && otherRecipe.getCookingTime().equals(this.getCookingTime())
                && otherRecipe.getPreparationTime().equals(this.getPreparationTime())
                && otherRecipe.getCalories().equals(this.getCalories())
                && otherRecipe.getServings().equals(this.getServings())
                && otherRecipe.getUrl().equals(this.getUrl());
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, preparationTime, ingredient, instruction, url, tags);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(getName())
                .append(" PreparationTime: ")
                .append(getPreparationTime())
                .append(" Ingredient: ")
                .append(getIngredient())
                .append(" Instruction: ")
                .append(getInstruction())
                .append(" Url: ")
                .append(getUrl())
                .append(" Tags: ");
        getTags().forEach(builder::append);
        return builder.toString();
    }

}
