import javax.swing.*;

import static com.sun.glass.ui.Cursor.setVisible;

public class Game extends JFrame {
    public Game() {
        Controller ctrl = new Controller();
        add(ctrl);
        JFrame jf=new JFrame();
        setTitle("̰贪吃蛇");
        setSize(435,390);
        setLocation(200, 200);
        setVisible(true);
    }
    public static void main(String[] args) {
        new Game();
    }
}
