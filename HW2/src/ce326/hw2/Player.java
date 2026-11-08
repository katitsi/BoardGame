package ce326.hw2;

import java.util.LinkedList;
import java.util.List;

public class Player extends BoardElement implements Movable{
  int row;
  int col;

  public Player() {
    super("X");
  }

  public Player(int row, int col) {
    super("X");
    this.row = row;
    this.col = col;
  }

  public int getRow() {
    return row;
  }

  public int getCol() {
    return col;
  }
    
  public void setRow(int row) {
    this.row = row;
  }

  public void setCol(int col) {
    this.col = col;
  }

  /*Checks for the valid positions in the board that the Player could
   *possibly move. Returs a list of int arrays size 2. The first position
   *of array represent the row of the board and the second the column*/
  public List<int[]> moveOptions(Board board) {
    List<int[]> list =  new LinkedList<int[]>();
    int[] posiblePos;
    int[] moveRow = {-1,-1,-1, 0, 0, 1,1,1};
    int[] moveCol = {-1, 0, 1,-1, 1,-1,0,1};
    int row,col;
        
    //Check from right to left and from above to below 
    //for valid potitions in the board.
    for(int i=0; i<8 ; i++) {
      row = this.row + moveRow[i];
      col = this.col + moveCol[i];

      if (row >= board.getRows()) {
        row = 0;
      }
      else if (row < 0) {
        row = board.getRows()-1;
      }

      if (col >= board.getCols()) {
        col = 0;
      }
      else if (col < 0) {
        col = board.getCols()-1;
      }

      if(checkMove(board, row, col)) {
        posiblePos = new int[2];
        posiblePos[0] = row;
        posiblePos[1] = col;
        list.add(posiblePos);
      }
    }    
    return list;
  }

  //Moves the player from the current position to the new
  public void move(Board board, int[] newPos) {
    BoardCell cell;
    BoardElement element;
    int energy;

    cell = board.getAt(newPos[0], newPos[1]);
    energy = board.getEnergy()-1;
    if (cell != null) {
      if (!cell.getElements().isEmpty()) {
        element = cell.getElements().getLast();
        if (element instanceof Vegatable) {
          Vegatable veg = new Vegatable();
          energy = energy + veg.eaten();
        }
        else if (element instanceof Fish) {
          Fish fish = new Fish();
          energy = energy + fish.eaten();
        }
        else if (element instanceof Meat) {
          Meat meat = new Meat();
          energy = energy + meat.eaten();
        }
        else if (element instanceof Ghost) {
          energy = 0;
        }
        
        if (!(element instanceof Ghost)) {
          cell.removeElement();
        }
      }
    }

    //remove player from current cell
    cell = board.getAt(this.row, this.col);
    cell.removeElement();

    //update the position of player in the board
    this.row = newPos[0];
    this.col = newPos[1];
    board.setAt(newPos[0], newPos[1], this);
    board.setPlayerEnergy(energy);
  }

  static boolean checkMove(Board board, int row, int col) {
    BoardCell cell = board.getAt(row, col);

    if (cell != null && !cell.getElements().isEmpty() &&
      cell.getElements().getLast() instanceof Obstacle) {
      return false;
    }   
    return true;
  }
}