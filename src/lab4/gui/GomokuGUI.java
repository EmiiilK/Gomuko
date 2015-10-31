package lab4.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Observable;
import java.util.Observer;

import lab4.client.GomokuClient;
import lab4.data.GomokuGameState;

import javax.swing.*;

/**
 * The GUI class
 */
public class GomokuGUI implements Observer{

    private GomokuClient client;
    private GomokuGameState gameState;

    private JLabel messageLabel;
    private JButton connectButton;
    private JButton newGameButton;
    private JButton disconnectButton;
    private JFrame frame;

    private GamePanel gamePanel;

    /**
     * The constructor
     *
     * @param g   The game state that the GUI will visualize
     * @param c   The lab4.client that is responsible for the communication
     */
    public GomokuGUI(GomokuGameState g, GomokuClient c){
        client = c;
        gameState = g;
        client.addObserver(this);
        gameState.addObserver(this);

        createLayout();
    }

    /**
     * Creates the window and the expected layout
     */
    private void createLayout() {
        frame = new JFrame("Gomoku!");
        messageLabel = new JLabel(gamestate.getMessageString());
        connectButton = new JButton("Connect");
        newGameButton = new JButton("New game");
        disconnectButton = new JButton("Disconnect");
        gamePanel = new GamePanel(gamestate.getGameGrid());

        gamePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                int[] gridPos = gamePanel.getGridPosition(e.getX(), e.getY());
                gameState.move(gridPos[0], gridPos[1]);
            }
        });

        newGameButton.setEnabled(false);
        disconnectButton.setEnabled(false);

        EventListener el = new EventListener();
        connectButton.addActionListener(el);
        newGameButton.addActionListener(el);
        disconnectButton.addActionListener(el);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocation(100,100);
        frame.setResizable(false);
        int size = gamePanel.getMinimumSize().width+6;
        frame.setSize(size, size + 90);

        Container contentPane = frame.getContentPane();

        SpringLayout sp = new SpringLayout();
        contentPane.setLayout(sp);

        contentPane.add(gamePanel);
        contentPane.add(connectButton);
        contentPane.add(newGameButton);
        contentPane.add(disconnectButton);
        contentPane.add(messageLabel);

        sp.putConstraint(SpringLayout.NORTH, gamePanel, 0, SpringLayout.NORTH, contentPane);

        sp.putConstraint(SpringLayout.NORTH, connectButton, 5, SpringLayout.SOUTH, gamePanel);
        sp.putConstraint(SpringLayout.WEST, connectButton, 8, SpringLayout.WEST, contentPane);

        sp.putConstraint(SpringLayout.NORTH, newGameButton, 5, SpringLayout.SOUTH, gamePanel);
        sp.putConstraint(SpringLayout.WEST, newGameButton, 5, SpringLayout.EAST, connectButton);

        sp.putConstraint(SpringLayout.NORTH, disconnectButton, 5, SpringLayout.SOUTH, gamePanel);
        sp.putConstraint(SpringLayout.WEST, disconnectButton, 5, SpringLayout.EAST, newGameButton);

        sp.putConstraint(SpringLayout.NORTH, messageLabel, 5, SpringLayout.SOUTH, disconnectButton);
        sp.putConstraint(SpringLayout.WEST, messageLabel, 5, SpringLayout.WEST, contentPane);

        //frame.pack();
        frame.setVisible(true);
    }

    public void update(Observable o, Object arg) {

        // Update the buttons if the connection status has changed
        if(o == client){
            if(client.getConnectionStatus() == GomokuClient.UNCONNECTED){
                connectButton.setEnabled(true);
                newGameButton.setEnabled(false);
                disconnectButton.setEnabled(false);
            }else{
                connectButton.setEnabled(false);
                newGameButton.setEnabled(true);
                disconnectButton.setEnabled(true);
            }
        }

        // Update the status text if the gamestate has changed
        if(o == gamestate){
            messageLabel.setText(gamestate.getMessageString());
        }
    }

    /**
     * Class for listening for button presses
     */
    private class EventListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JButton button = (JButton)e.getSource();
            if(button == connectButton) {
                ConnectionWindow cw = new ConnectionWindow(client);
                connectButton.setEnabled(false);
                disconnectButton.setEnabled(true);
            } else if(button == newGameButton) {
                gamestate.newGame();
            } else if(button == disconnectButton) {
                gamestate.disconnect();
                connectButton.setEnabled(true);
                disconnectButton.setEnabled(false);
            }
        }
    }
}
