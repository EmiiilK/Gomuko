package lab4.data;

import java.awt.*;
import java.util.Arrays;
import java.util.Observable;

public class GameGrid extends Observable {

    public static final int INROW = 5;

    private SqrType[][] board;
    private Point[][] signatures;

    /**
     * Constructor
     *
     * @param size The width/height of the game grid
     */
    public GameGrid(int size){
        board = new SqrType[size][size];
        clearGrid();

        createWinningSignatures();
    }

    /**
     * Creates signatures for matching the winning board
     */
    private void createWinningSignatures() {
        signatures = new Point[4][INROW];
        for(int i = 0; i<INROW; i++) {
            signatures[0][i] = new Point(i, 0);
            signatures[1][i] = new Point(0, i);
            signatures[2][i] = new Point(i, i);
            signatures[3][i] = new Point(-i, i);
        }
    }

    /**
     * Reads a location of the grid
     *
     * @param x The x coordinate
     * @param y The y coordinate
     * @return the value of the specified location
     */
    public SqrType getLocation(int x, int y){
        if(!canTake(x, y))
            return null;

        return board[x][y];
    }

    /**
     * Returns the size of the grid
     *
     * @return the grid size
     */
    public int getSize(){
        return board.length;
    }

    private boolean canTake(int x, int y) {
        int s = getSize();
        return x < s || y < s;
    }

    /**
     * Enters a move in the game grid
     *
     * @param x the x position
     * @param y the y position
     * @param player
     * @return true if the insertion worked, false otherwise
     */
    public boolean move(int x, int y, SqrType player){
        if(!canTake(x, y))
            return false;

        SqrType type = board[x][y];
        if(type == SqrType.EMPTY) {
            board[x][y] = player;
            setChanged();
            notifyObservers();
            return true;
        }

        return false;
    }

    /**
     * Clears the grid of pieces
     */
    public void clearGrid(){
        boolean changed = false;
        for(int x = 0; x<board.length; x++) {
            if(!changed) {
                for (int y = 0; y < board[x].length; y++) {
                    if (board[x][y] != SqrType.EMPTY)
                        changed = true;
                    board[x][y] = SqrType.EMPTY;
                }
            } else {
                Arrays.fill(board[x], SqrType.EMPTY);
            }
        }

        if(changed) {
            setChanged();
            notifyObservers();
        }
    }

    /**
     * Check if a player has INROW in row
     *
     * @param player the player to check for
     * @return true if player has INROW in row, false otherwise
     */
    public boolean isWinner(SqrType player){
        for(int y = 0; y<board.length; y++) {
            for(int x = 0; x<board[y].length; x++) {
                for(int i = 0; i<signatures.length; i++) {
                    if(signaturePasses(new Point(y, x), signatures[i], player))
                        return true;
                }
            }
        }

        return false;
    }

    /**
     * Test signatures created by createWinningSignatures method
     *
     * @param testPoint Point on board to test signature on
     * @param signature The signature you want to test
     * @param type Which SqrType to test the signature against
     * @return true if matches signature
     */
    private boolean signaturePasses(Point testPoint, Point[] signature, SqrType type) {
        for(int i = 0; i<signature.length; i++) {
            Point p = signature[i];
            Point tmpPoint = new Point(p.x + testPoint.x, p.y + testPoint.y);
            if(!canTake(tmpPoint.x, tmpPoint.y))
                return false;

            if(board[tmpPoint.y][tmpPoint.x] != type)
                return false;
        }

        return true;
    }
}