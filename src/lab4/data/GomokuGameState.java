package lab4.data;

import java.util.Observable;
import java.util.Observer;
import lab4.client.GomokuClient;

/**
 * Class handling the some gui/game logic
 */
public class GomokuGameState extends Observable implements Observer{

    // Game variables
    private final int DEFAULT_SIZE = 15;
    private GameGrid gameGrid;

    private GameState currentState;
    private GomokuClient client;

    private String message = "Gomoku!";

    /**
     * The constructor
     *
     * @param gc The lab4.client used to communicate with the other player
     */
    public GomokuGameState(GomokuClient gc){
        client = gc;
        client.addObserver(this);
        gc.setGameState(this);
        currentState = GameState.NOT_STARTED;
        gameGrid = new GameGrid(DEFAULT_SIZE);
    }


    /**
     * Returns the message string
     *
     * @return the message string
     */
    public String getMessageString(){
        return message;
    }

    /**
     * Returns the game grid
     *
     * @return the game grid
     */
    public GameGrid getGameGrid(){
        return gameGrid;
    }

    /**
     * This player makes a move at a specified location
     *
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public void move(int x, int y){
        if(currentState == GameState.MY_TURN) {
            boolean moved = gameGrid.move(x, y, SqrType.ME);
            if(moved) {
                boolean sent = client.sendMoveMessage(x, y);
                boolean won = gameGrid.isWinner(SqrType.ME);
                if(won) {
                    message = "You won the game! But now you lost it (deal with it)";
                    currentState = GameState.FINISHED;
                } else {
                    currentState = GameState.OTHERS_TURN;
                    message = "It's the other players turn!";
                }
                setChanged();
                notifyObservers();
            }
        }
    }

    /**
     * Starts a new game with the current lab4.client
     */
    public void newGame(){
        gameGrid.clearGrid();
        currentState = GameState.OTHERS_TURN;
        message = "Starting a new game!";

        boolean sent = client.sendNewGameMessage();

        setChanged();
        notifyObservers();
    }

    /**
     * Other player has requested a new game, so the
     * game state is changed accordingly
     */
    public void receivedNewGame(){
        gameGrid.clearGrid();
        message = "The other player started a new game! It's your turn!";
        currentState = GameState.MY_TURN;
        setChanged();
        notifyObservers();
    }

    /**
     * The connection to the other player is lost,
     * so the game is interrupted
     */
    public void otherGuyLeft(){
        gameGrid.clearGrid();
        message = "The other player left!";
        currentState = GameState.FINISHED;
        setChanged();
        notifyObservers();
    }

    /**
     * The player disconnects from the lab4.client
     */
    public void disconnect(){
        gameGrid.clearGrid();
        message = "You disconnected from the game!";
        currentState = GameState.FINISHED;
        setChanged();
        notifyObservers();
        client.disconnect();
    }

    /**
     * The player receives a move from the other player
     *
     * @param x The x coordinate of the move
     * @param y The y coordinate of the move
     */
    public void receivedMove(int x, int y){
        boolean moved = gameGrid.move(x, y, SqrType.OTHER);
        if(moved) {
            boolean won = gameGrid.isWinner(SqrType.OTHER);
            if(won) {
                message = "The other player won!";
                currentState = GameState.FINISHED;
            } else {
                currentState = GameState.MY_TURN;
                message = "It's your turn!";
            }

            setChanged();
            notifyObservers();
        }
    }

    public void update(Observable o, Object arg) {
        switch(client.getConnectionStatus()){
            case GomokuClient.CLIENT:
                message = "Game started, it is your turn!";
                currentState = GameState.MY_TURN;
                break;
            case GomokuClient.SERVER:
                message = "Game started, waiting for other player...";
                currentState = GameState.OTHERS_TURN;
                break;

            default: break;
        }

        setChanged();
        notifyObservers();
    }

}
