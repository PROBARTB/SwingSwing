import javax.swing.*;

public class Main {
    public static void main(String[] args) {

        System.out.println("Hello Swing");

        JFrame frame = new JFrame("SwingSwing Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 400);
        frame.add(new GamePanel());
        frame.setVisible(true);
    }
}