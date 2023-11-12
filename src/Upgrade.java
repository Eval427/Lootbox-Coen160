import java.util.HashMap;

public abstract class Upgrade {
    private HashMap<String, Integer> cost;
    private boolean alreadyDone;
    private String name;

    /**
     * Creates an upgrade that can then be used to change the UI
     * @param name Name of the upgrade
     * @param cost Hashmap containing prices for an upgrade
     */
    Upgrade(String name, HashMap<String, Integer> cost) {
        this.cost = cost;
        this.alreadyDone = false;
        this.name = name;
    }

    public String getUpgradeString() {
        String costText = "";
        boolean begin = true;
        for (String s : cost.keySet()) {
            if (begin) {
                costText = costText + s + " " + cost.get(s);
                begin = false;
            } else {
                costText = costText + " - " + s + cost.get(s);
            }
        }

        return String.format("<html>%s<br/>%s", name, costText);
    }

    public void setDone() {
        alreadyDone = true;
    }

    public boolean isDone() {
        return alreadyDone;
    }

    public String getName() {
        return name;
    }

    public HashMap<String, Integer> getCost() {
        return cost;
    }

    public abstract void upgradeAction();
}