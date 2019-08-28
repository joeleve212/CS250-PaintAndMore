/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Joe Leveille
 */
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
public class GuiCalc implements ActionListener {
    private JFrame frame;
    private JTextField xfield;
    private JLabel rslt, xLabel, yLabel;
    private JTextField field2;
    private JButton computeButton;
    private JPanel xPanel, yPanel;
    public GuiCalc() {
        frame = new JFrame();
        xPanel = new JPanel();
        yPanel = new JPanel();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout());
        xfield = new JTextField("", 5);
        xLabel = new JLabel("X: ");
        xPanel.add(xLabel);
        xPanel.add(xfield);
        frame.add(xPanel);
        field2 = new JTextField("", 5);
        yLabel = new JLabel("Y: ");
        yPanel.add(yLabel);
        yPanel.add(field2);
        frame.add(yPanel);
        rslt = new JLabel("X * Y = ");
        frame.add(rslt);
        computeButton = new JButton("Compute");
        frame.add(computeButton);
        computeButton.addActionListener(this);
        frame.pack();
        frame.setVisible(true);
    }
    public void actionPerformed(ActionEvent event) {
        String xText = xfield.getText();
        String yText = field2.getText();
        int y = Integer.parseInt(yText);
        int x = Integer.parseInt(xText);
        int num = x * y;
        String sum = Integer.toString(num);
        rslt.setText("X * Y = " + sum);
    }
}
