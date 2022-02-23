import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class Main {
    public static class GUI {
        private JTextField textField1;
        private JTextField textField2;
        private JButton runButton;
        private JPanel panel1;

        public GUI() {

            runButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int vectorSize = Integer.parseInt(textField1.getText());
                    int noOfVectors = Integer.parseInt(textField2.getText());

                    int[][] pixels = VectorQ.ImageClass.readImage("image.jpg");

                    VectorQ v = new VectorQ();

                    v.DivideImage(pixels, vectorSize);

                    try {
                        v.compress(noOfVectors);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    try {
                        v.Decompress();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    } catch (ClassNotFoundException ex) {
                        ex.printStackTrace();
                    }


                    JOptionPane.showMessageDialog(null,"Done..!");
                    System.exit(0);
                }

            });
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("App");
        GUI gui=new GUI();
        frame.setContentPane(gui.panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(500,150));
        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);
/////////////////////////////
    }
}
