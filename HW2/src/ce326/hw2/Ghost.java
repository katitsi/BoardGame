/*This class describe the character 'ghost' of the game*/
package ce326.hw2;

import java.util.LinkedList;
import java.util.List;

public class Ghost extends BoardElement implements Movable{
  int row;
  int col;

  public Ghost() {
    super("@");
  }

  public Ghost(int row, int col) {
    super("@");
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

  /*Checks for the valid positions in the board that a Ghost could
   *possibly move. Returs a list of int arrays size 2. The first position
   *of array represent the row of the board and the second the column*/
  public  List<int[]> moveOptions(Board board) {
    List<int[]> list = new LinkedList<int[]>();
    int[] posiblePos;
    int row,col;
    int[] moveRow = {-1,1,0,0};
    int[] moveCol = {0,0,-1,1};
        
    for (int i=0; i<4; i++) {
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

  /*Checks if a position in the board is valid for a ghost to move*/
  static boolean checkMove(Board board, int row, int col) {
    BoardCell cell = board.getAt(row, col);

    if (cell != null && !cell.getElements().isEmpty() &&
      cell.getElements().getLast() instanceof Obstacle) {
      return false;
    }   
    return true;
    }
}
