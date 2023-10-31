// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
        StartWindow start = new StartWindow();
        GameWindow game = new GameWindow(10);
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