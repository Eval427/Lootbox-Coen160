// package Project;

public class Main {
    public static void main(String[] args) {
        StartWindow start = new StartWindow();
        GameWindow game = new GameWindow();
        GameWindow.startBackgroundMusic("./src/epicbkmusic.wav");
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

/*
The Epic 2 by Rafael Krux
Link: https://filmmusic.io/song/5384-the-epic-2-
License: http://creativecommons.org/licenses/by/4.0/
Music promoted on https://www.chosic.com/free-music/all/

In Dreams by Scott Buckley | www.scottbuckley.com.au
Music promoted by https://www.chosic.com/free-music/all/
Attribution 4.0 International (CC BY 4.0)
https://creativecommons.org/licenses/by/4.0/ 
*/