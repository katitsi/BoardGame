/*This class impliments the action game*/

package ce326.hw2;

import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Scanner;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Game {
  Board board;
  LinkedList<Game> snapshots;
  SortedMap<Integer,String> movesHistory;
  int numOfMoves;
    
  public Game(Board board){
    this.board = board;
    snapshots = new LinkedList<Game>();
    movesHistory = new TreeMap<Integer,String>();
    numOfMoves = 0;
  }

  //copy constructor
  public Game(Game game) {
    this.board = new Board(game.getBoard());
    this.numOfMoves = game.numOfMoves;

    this.snapshots = new LinkedList<Game>();
    for(Game snap: game.getSnapshots()) {
      this.snapshots.addLast(snap);
    }

    this.movesHistory = new TreeMap<Integer,String>();
    for (Integer key: game.getMoveHistory().keySet()) {
      this.movesHistory.put(Integer.valueOf(key), game.getMoveHistory().get(key));
    }

  }

  private void setNumOfMoves(int numOfMoves) {
    this.numOfMoves = numOfMoves;
  }

  private int getNumOfMoves() {
    return this.numOfMoves;
  }

  private Board getBoard() {
    return this.board;
  }

  private SortedMap<Integer,String> getMoveHistory() {
    return this.movesHistory;
  }

  private LinkedList<Game> getSnapshots() {
    return this.snapshots;
  }

  /*Moves character acording to the block that the user inputs,
  *moves ghosts of the game acording Dijkstra algorithm,
  *determins if the user won or lost the game.*/
  public Mode playGame(Mode gameMode, Scanner sc) {
    String move;
    int[] pos;
    Player player;
    BoardCell cell;
    int[][] dijkstraArray;

    if (this.playerIsBlocked()) {
      return Mode.GAMEOVER;
    }

    System.out.println(this.board);
    do {
      addToSnapshots(this);
      player = this.board.getPlayer();
            
      //The user inputs the new move
      System.out.print("Your Move: ");
      move = sc.nextLine();
      if (move.equals("Z") || move.equals("z")) {
        return Mode.PAUSE;
      }

      pos = checkMove(move, board, player);
      while (pos[0] == -1) {
        System.out.print("\nInvalid input. Try again... ");
        move = sc.nextLine();
        if (move.equals("Z") || move.equals("z")) {
          return Mode.PAUSE;
        }
        pos = checkMove(move, board , player);
      }
   
      //update moves history
      String movement = toString(player.getRow(),player.getCol())+"-"+toString(pos[0],pos[1]);
      this.movesHistory.put(this.getNumOfMoves(),movement);
      this.setNumOfMoves( this.getNumOfMoves() + 1);
           
      player.move(this.board,pos);
      cell = this.board.getAt(pos[0], pos[1]);

      //character goes to a block tha has a ghost
      if (cell.getElements().size() == 2) {
        cell.removeElement();
        gameMode =  Mode.LOST;
        break;
      }
      else if (this.board.getEnergy() == 0) {
        gameMode = Mode.LOST;
        break;
      }
      else if (this.playerIsBlocked()) { /*Character cannot move anymore*/
        gameMode = Mode.GAMEOVER;
        break;
      }
      else if (playerWon(board)) {
        System.out.println(this.board);
        gameMode = Mode.WIN;
        break;
      }

      dijkstraArray = dijkstraArray(board, player);
      moveGhosts(this, dijkstraArray);
      if (gameMode == Mode.DEBUG) {
        printDijkstra(dijkstraArray,this.board.getRows(),this.board.getCols());
      }

      //A ghost eat the character
      if(cell.getElements().getLast() instanceof Ghost) {
        gameMode =  Mode.LOST;
        break;
      }
      else if (this.playerIsBlocked()) { /*Character cannot move anymore*/
        gameMode = Mode.GAMEOVER;
        break;
      }
      System.out.println(this.board);
    }while(gameMode == Mode.GAME || gameMode == Mode.DEBUG);
        
    System.out.println(this.board);
    return gameMode;
  }

  /*Jump to a previous snapshot of the game if it is possible.*/
  public Game jump(int move) {
    int i,pos;
    Game game=null;

    if (!movesHistory.keySet().contains(move)) {
      System.out.println("Wrong number");
      return this;
    }

    pos = move/2;
    if (pos % 2 != 0) {
      pos = pos-1;
    }

    i=0;
    for (Game snap : this.snapshots) {
      if (i==pos) {
        game = snap;
        break;
      }
      i++;
    }
    game = new Game(game);
    return game;
  }

  /*Prints the movements of the character and the ghosts so far, in JSON format*/
  public void printHistory() {
    System.out.println("{\n   \"moves\": {");
    for (Integer key: this.movesHistory.keySet()) {
      System.out.println("      \"" + key + "\": " + "\"" + this.movesHistory.get(key) +"\"");
    }
      System.out.println("   }\n}");
  }

  /*Checks if the character of the game is blocked by ghosts*/
  private boolean playerIsBlocked() {
    Player player = this.board.getPlayer();
    int row;
    int col;
    int[] moveRow = {-1,1,0,0};
    int[] moveCol = {0,0,-1,1};
        
    int canMove=0;
    int ghosts =0;
    for (int pos[]: player.moveOptions(board)) {
      for (int i=0; i<4; i++) {
        row = pos[0] + moveRow[i];
        col = pos[1] + moveCol[i];
    
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
    
        if (isGhost(board, row, col)) {
          ghosts++;
        }
        else if (!isObstacle(board, row, col)) {
          canMove++;
        }
      }

    }
    if (canMove!=0 && ghosts<player.moveOptions(board).size()) {
      return false;
    }    
    return true;
  }
 ////////////////////////////////////////////////////////////////////////////////////////////////////
 ////////////////////////////Static helpers methods/////////////////////////////////////////////////    

  static private void addToSnapshots(Game game) {
    Game snapGame = new Game(game);
    game.getSnapshots().addLast(snapGame);
  }

  /*Checks if the character has visited all the blocks of the board*/
  private static Boolean playerWon(Board board) {
    for (int i=0; i<board.getRows(); i++) {
      for (int j=0; j<board.getCols(); j++) {
        if (board.getAt(i, j) == null) {
          return false;
        }
        if (!board.getAt(i, j).isVisited()) {
          return false;
        }
      }
    }
    return true;
  }

  private static String toString(int rows,int cols) {
    String row;
    String col;
    char[] chars = new char[1];

    char ch = 'A';
    for(int i=0; i<rows; i++) {
      ch++;
    }
    chars[0] = ch;
    row = new String(chars);
    col = String.valueOf(cols + 1);
    return row+col;
  }

  /*Desides where the ghosts of the game would move in the board and replaces
  *them based on the Dijkstra algorithm.Two ghosts cannot be in the same cell
  *The movement is random*/
  private static void moveGhosts(Game game, int[][] dijkstra) {
    List<int[]> moveOptions;
    List<int[]> minDistance = new LinkedList<>();
    int row,col;
    int[] newPos = new int[2];
    BoardCell cell;
    StringBuilder ghostsMovement = new StringBuilder();

    int i=0;
    for (Ghost ghost: game.getBoard().getGhosts()) {
      cell = game.getBoard().getAt(ghost.getRow(), ghost.getCol());

      moveOptions = ghost.moveOptions(game.getBoard());
      for (int[] pos : moveOptions) {
        row = pos[0];
        col = pos[1];
        if (dijkstra[row][col]<dijkstra[ghost.getRow()][ghost.getCol()]) {
          if (!isGhost(game.getBoard(), row, col)) {
            minDistance.add(pos);
          }
        }
      }

      //ghost does not move
      if (minDistance.isEmpty()) {
        String movement = toString(ghost.getRow(),ghost.getCol())+"-"+toString(ghost.getRow(),ghost.getCol());
        ghostsMovement.append(movement + ",");
        continue;
      }

      //decide where the ghost would move
      Random randomPick = new Random();
      int index;
      index = randomPick.nextInt(minDistance.size());
      i=0;
      for (int[] pos: minDistance) {
        if (i==index) {
          newPos = pos;
          break;
        }
        i++;
      }

      String movement = toString(ghost.getRow(),ghost.getCol())+"-"+toString(newPos[0],newPos[1]);
      ghostsMovement.append(movement + ",");

      cell.removeElement();
      ghost.setRow(newPos[0]);
      ghost.setCol(newPos[1]);
      game.getBoard().setAt(newPos[0], newPos[1], ghost);      
      minDistance.clear();
    }

    //update game history
    ghostsMovement.deleteCharAt(ghostsMovement.length()-1);
    game.getMoveHistory().put(game.getNumOfMoves(),ghostsMovement.toString());
    game.setNumOfMoves(game.getNumOfMoves() +1);
  }

  private static boolean isGhost(Board board, int row, int  col) {
    BoardCell cell = board.getAt(row, col);

    if (cell!=null && !cell.getElements().isEmpty() && 
      cell.getElements().getLast()instanceof Ghost) {
      return true;
    }
    return false;
  }

  private static boolean isObstacle(Board board, int row, int  col) {
    BoardCell cell = board.getAt(row, col);

    if (cell!=null && !cell.getElements().isEmpty() && 
      cell.getElements().getLast()instanceof Obstacle) {
      return true;
    }

    return false;
  }

  /*Checks whether a input move of the player is valid or no.
  *Returns the position in the board as an int array.
  *(array[0]->row, array[1]->column)*/
  private static int[] checkMove(String move, Board game, Player player) {
    Pattern p = Pattern.compile("[a-yA-Y]\\d+");
    Matcher m = p.matcher(move);
    int[] pos = {-1,-1};
    int[] newMove = new int[2];
    int row;
    int col;
    List<int[]> moveOptions;

    if (!m.matches()) {
      return pos;
    }

    move = move.toLowerCase();
    row = move.charAt(0) - 'a';
    col = Integer.parseInt(move.substring(1));
    if(row >= game.getRows() || col-1 >= game.getCols() || col-1<0) {
      return pos;
    }

    newMove[0] = row;
    newMove[1] = col-1; 

    moveOptions = player.moveOptions(game);
    if(contains(moveOptions,newMove)) {
      return newMove;
    }
    return pos;
  }

  /*Checks if a list of arrays contain a specific array */
  private static boolean contains(List<int[]> list, int[] array) {
    for(int[] a : list) {
      if (a[0] == array[0] && a[1] == array[1]) {
        return true;
      }
    }
    return false;
  }

                              ///////Dijkstra Algorithm///////
 ///////////////////////////////////////////////////////////////////////////////////////////////////////

  static enum Explored{
    YES,
    NO
  }

  /*Returns a 2D array that contains the minimun moves that a ghost 
  *from a source block needs to do to visit each one of the other 
  *blocks of the board*/
  static private int[][] dijkstraArray(Board board, Player player) {
    int[][] dijkstraArray = new int[board.getRows()][board.getCols()];
    Explored isVisited[][] = new Explored[board.getRows()][board.getCols()];
    BoardCell cell;

    //initialize the array of distances from player(source)
    for (int i=0; i<board.getRows();i++) {
      for (int j=0; j<board.getCols(); j++) {
        cell = board.getAt(i, j);
        if (cell!=null && !cell.getElements().isEmpty()) {
          if (cell.getElements().getLast() instanceof Obstacle) {
            dijkstraArray[i][j] = -2;
          }
          else if (!(cell.getElements().getLast() instanceof Player)) {
            dijkstraArray[i][j] = -1;
          }
        }
        else {
          dijkstraArray[i][j] = -1;
        }
        isVisited[i][j] = Explored.NO;
      }
    }
    dijkstra(board, dijkstraArray, isVisited);
    return dijkstraArray;
  }

  //Helper class for the implementation of Dijkstra algorithm
  static class Node implements java.lang.Comparable<Node>{
    int row;
    int col;
    int distance; //distance from source node

    public Node(int row, int col, int distance) {
      this.row = row;
      this.col = col;
      this.distance = distance;
    }

    public int getRow() {
      return row;
    }

    public int getCol() {
      return col;
    }

    public int getDistance() {
      return distance;
    }

    public int compareTo(Node node) {
      if(this.distance < node.getDistance()) {
        return -1;
      }
      else if (this.distance > node.getDistance()) {
        return 1;
      }
      return 0;
    }
  }

  /*Implementation of Dijkstra algorithm with source node the position of player.
   *In array distances are stored the minimum moves that a ghost can do from the
   *source position to any other position of the board*/ 
  static private void dijkstra(Board board,int[][] distances, Explored[][] isVisited) {
    PriorityQueue<Node> nodes = new PriorityQueue<Node>();

    Node source = new Node(board.getPlayer().getRow(),board.getPlayer().getCol(),0);
    nodes.add(source);

    while(!nodes.isEmpty()) {
      Node curr = nodes.poll();
      int row = curr.getRow();
      int col = curr.getCol();

      if (isVisited[row][col] == Explored.YES) {
        continue;
      }
      isVisited[row][col] = Explored.YES;

      //find neighbors of the curr node and explore them
      Ghost ghost = new Ghost(row, col);
      List<int[]> neighbors = ghost.moveOptions(board);
      for (int[] pos : neighbors) {
        int prow = pos[0], pcol = pos[1];
        if (isVisited[prow][pcol]==Explored.NO) {
          int distance = distances[prow][pcol];
          if ( distance == -1 || distance>curr.getDistance()) {
            distances[prow][pcol] = curr.getDistance()+1;
          }
          Node node = new Node(pos[0],pos[1],distances[prow][pcol]);
            nodes.add(node);
        }
      }
    } 
  }

  static private void printDijkstra(int[][] array, int rows, int cols) {
    StringBuilder str = new StringBuilder();
    char ch;

    str.append("\n\t  ");
    for (int i=1; i<=cols; i++) {
      str.append(String.format("%3d",i));
    }
    str.append("\n\n");

    ch = 'A';
    for (int i=0; i<rows; i++) {
      str.append("\t"+ch+" ");
      ++ch;
      for (int j=0; j<cols; j++) {
        if (array[i][j] == -2) {
          str.append(String.format("%3c",'#'));
        }
        else if (array[i][j] == -1) {
          str.append(String.format("%3c",'?'));
        }
        else {
          str.append(String.format("%3d",array[i][j]));
        }
      }
      str.append("\n");
    }
    str.append("\n");
    System.out.println(str);
  }
}

