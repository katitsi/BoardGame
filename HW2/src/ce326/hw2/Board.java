/*This class describe the board of the game.*/

package ce326.hw2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Board {
  BoardCell[][] boardArray;
  int rows;
  int columns;
  int playerEnergy;
  LinkedList<Ghost> ghosts;
  Player player;
    
  public Board(Scanner sc) throws FileNotFoundException,IOException,JSONException{
    String fileName;
    File game;
    int energy;
    JSONArray initArray;
    BoardElement element;

    do {
      System.out.print("\nEnter input filename: ");
      fileName = sc.nextLine();
      game = new File(fileName);
      if(game.exists() && game.canRead()) {
        break;
      }
      System.out.println("\nUnable to read " + fileName);
    }while(!game.exists() && !game.canRead());

    StringBuilder json = new StringBuilder();
    BufferedReader reader = new BufferedReader(new FileReader(game));
    String line;
    while((line = reader.readLine()) != null) {
      json.append(line + "\n");
    }
    reader.close();

    JSONObject jObject = new JSONObject(json.toString());

    this.rows = jObject.getInt("rows");
    this.columns = jObject.getInt("columns");
    energy = jObject.getInt("energy");
    initArray =  jObject.getJSONArray("init");

    if( this.rows <= 0 || this.columns <=0 || energy <= 0 || initArray.length() != (this.rows*this.columns)) {
      throw new IllegalArgumentException("Illegal arguments in " + fileName);    
    }

    this.playerEnergy = energy;
    this.boardArray = new BoardCell[this.rows][this.columns];
    this.ghosts = new LinkedList<Ghost>();
    Ghost ghost;
    int index=0;
    for (int i=0; i<rows; i++) {
      for (int j=0; j<this.columns; j++) {
        element = null;
        if (initArray.getString(index).equals("v")){
          element = new Vegatable();
        }
        else if (initArray.getString(index).equals("f")) {
          element = new Fish();  
        }
        else if (initArray.getString(index).equals("m")){
          element = new Meat();
        }
        else if (initArray.getString(index).equals("X")) {
          player = new Player(i,j);
          element = player;
        }
        else if (initArray.getString(index).equals("@")) {
          ghost = new Ghost(i,j);
          element = ghost;
          ghosts.addLast(ghost);
        }
        else if (initArray.getString(index).equals("#")) {
          element = new Obstacle();
        }
                
        if(element!=null) {
          boardArray[i][j] = new BoardCell();
          boardArray[i][j].addElement(element);
          if ( element instanceof Obstacle) {
            boardArray[i][j].playerVisited(true);
          }
          else {
            boardArray[i][j].playerVisited(false);
          }
        }
        index++;
      }
    }
  }

    //copy constructor
  public Board(Board board) {
    Ghost snapGhost;
    Obstacle obstacle;
    Vegatable veg;
    Meat meat;
    Fish fish;
    LinkedList<BoardElement> elements;

    this.columns = board.columns;
    this.rows = board.rows;
    this.playerEnergy = board.getEnergy();
    this.ghosts = new LinkedList<Ghost>();

    this.boardArray = new BoardCell[this.rows][this.columns];
    for (int i=0; i<this.rows; i++) {
      for (int j=0; j<this.columns; j++) {
        if (board.getAt(i, j) != null) {
          this.boardArray[i][j] = new BoardCell();
          elements = board.getAt(i, j).getElements();
          if (!elements.isEmpty()) {
            for (BoardElement element: elements) {
              if (element instanceof Player) {
                this.player = new Player(i, j);
                this.boardArray[i][j].addElement(this.player);
              }
              else if (element instanceof Ghost) {
                snapGhost = new Ghost(i,j);
                this.ghosts.addLast(snapGhost);
                this.boardArray[i][j].addElement(snapGhost);
              }
              else if (element instanceof Obstacle) {
                obstacle = new Obstacle();
                this.boardArray[i][j].addElement(obstacle);
              }
              else if (element instanceof Vegatable) {
                veg = new Vegatable();
                this.boardArray[i][j].addElement(veg);
              }
              else if (element instanceof Meat) {
                meat = new Meat();
                this.boardArray[i][j].addElement(meat);
              }
              else if (element instanceof Fish) {
                fish = new Fish();
                this.boardArray[i][j].addElement(fish);
              }
            }
          }
          this.boardArray[i][j].playerVisited(board.getAt(i, j).isVisited);
        }
      }
    }
  }

  public BoardCell getAt(int row, int col) {
    return this.boardArray[row][col];
  }

  public void setAt(int row, int col, BoardElement element) {
    if (this.boardArray[row][col] == null) {
      this.boardArray[row][col] = new BoardCell();
    }

    if(element instanceof Player) {
      this.boardArray[row][col].playerVisited(true);
    }

    this.boardArray[row][col].addElement(element);
  }

  public int getCols(){
    return this.columns;
  }

  public int getRows(){
    return this.rows;
  }

  public int getEnergy(){
    return this.playerEnergy;
  }

  public BoardCell[][] getBoardArray() {
    return this.boardArray;
  }

  public List<Ghost> getGhosts() {
    return this.ghosts;
  }


  public void setPlayerEnergy(int energy) {
    this.playerEnergy = energy;
  }

  public Player getPlayer() {
    return this.player;
  }

  @Override
  public String toString() {
    StringBuilder str = new StringBuilder();
    char ch;
    BoardCell cell;

    str.append("\n\t  ");
    for (int i=1; i<=this.columns; i++) {
      str.append(String.format("%3s", i));
    }
    str.append("\n");

    ch = 'A';
    for(int i=0; i<this.rows; i++) {
      str.append("\t"+ch+"  ");
      ++ch;
      for (int j=0; j<this.columns; j++) {
        cell = this.getAt(i, j);
        if (cell==null) {
          str.append(" - ");
        }
        else {
          if(cell.getElements().isEmpty()) {
            if(cell.isVisited()) {
              str.append("   ");
            }
            else {
              str.append(" - ");
            }
          }
          else {
            str.append(this.boardArray[i][j].getElements().getLast().toString()+" ");
          }
        }
      }
      str.append("\n");
    }
    str.append("\n\tEnergy: " + this.playerEnergy + "\n");
    return str.toString();
  }
}
