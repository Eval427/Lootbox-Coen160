public class Main {
    public static void main(String[] args) {
        StartWindow start = new StartWindow();
        GameWindow game = new GameWindow();
        start.showWindow();
        while (!start.isGameStart()) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
            }
        }
        start.hideWindow();
        game.showWindow();
    }
}