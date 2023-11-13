// package Project;

import java.awt.*;
import java.io.Serializable;

// Stores basic information about every item in a chest
public class CustomItem implements Serializable {
    private final int chance;
    private final int increment;
    private final String name, icon, rewardDisplay;
    private final Color color;

    public CustomItem(String name, int chance, int increment, String icon, String rewardDisplay, Color color) {
        this.name = name;
        this.chance = chance;
        this.increment = increment;
        this.icon = icon;
        this.rewardDisplay = rewardDisplay;
        this.color = color;
    }

    public String getName() { return this.name; };

    public int getChance() { return this.chance; }

    public int getIncrement() { return this.increment; }

    public String getRewardDisplay() { return this.rewardDisplay; }

    public String getIcon() { return this.icon; }

    public Color getColor() { return this.color; }
}
