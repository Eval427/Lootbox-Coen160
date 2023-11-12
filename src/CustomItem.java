import java.awt.*;

// Stores basic information about every item in a chest
public class CustomItem {
    private int chance, increment;
    private String name, icon, rewardDisplay;
    private Color color;

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
