/*                            Action Game (User Interface)
 *The purpose of the game is the character 'X' to visit all the blocks of the
 *board that can be visited (no obstacles) without being eaten by the ghosts of 
 *the game. The character has initial energy, which is decrised by one each time 
 *the character moves and is incresed if the character eats vegatable,meat 
 *or fish (each of these have different energy). If the energy of character is equal
 *to zero, he losses.
 *(NOTE: the character could move to a block that has a ghost).
 ***********************************************************************************
 *Menu:
 *-L/l: the user loads a file in JSON that contains the board of the game
 *-D/d: enable or disable the debug mode. Prints the minimun number of moves that 
 *      a ghost needs to do to go from current block to all others.
 *-H/h: prints the movements of character and ghosts so far in the game, in JSON format
 *-C/c: continues the game if it was paused and the character is not blocked
 *-J/j: the character undo previous movements
 *-Q/q: quit (programme finish)
 *
 *The user can pause the game with Z/z and then select an option of the menu.
 **********************************************************************************
 *Need to import the org.json library*/
package ce326.hw2;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import org.json.JSONException;

enum Mode{
    GAME,
    NOGAME,
    DEBUG,
    GAMEOVER,
    PAUSE,
    WIN,
    LOST
}

public class HW2 {

  //Prints the menu of the game
  static void printMenu(){
    System.out.println("\n- Load Game      (L/l)");
    System.out.println("- Debug Dijkstra (D/d)");
    System.out.println("- Show History   (H/h)");
    System.out.println("- Jump to move   (J/j)");
    System.out.println("- Continue game  (C/c)");
    System.out.println("- Quit           (Q/q)");
    }

  public static void main(String[] args) throws Exception {
    String option;
    Scanner sc = new Scanner(System.in);
    Mode gameMode = Mode.NOGAME;
    Game game=null, newGame;
    Boolean enableDebug=false;
    int moveNum;
    boolean flag;

    //Start programme
    System.out.println("- Load Game       (L/l)");
    System.out.println("- Quit            (Q/q)");

    do {
      flag = false;
      System.out.print("\nYour option: ");
      option = sc.nextLine();
      switch (option) {
        case "L", "l": {
          Board board=null;
          try {
            board = new Board(sc);
          }
          catch(FileNotFoundException ex) {
            System.out.println("ERROR");
          }
          catch(IOException ex) {
            System.out.println("ERROR");
          }
          catch(JSONException ex) {
            System.out.println("\nFile NOT in JSON format");
          }
          catch(IllegalArgumentException ex){
            System.out.println("\n"+ex.getMessage());
          }

          if(board == null) {
            printMenu();
            break;
          }

          game = new Game(board);
          gameMode = Mode.GAME;
          break;
          }
        case "D", "d": {
          if (!enableDebug) {
            gameMode = Mode.DEBUG;
            enableDebug = true;
          }
          else {
            enableDebug = false;
            gameMode = Mode.GAME;
          }
          printMenu();
          continue;
        }
        case "H", "h": {
          if (game == null) {
            System.out.println("No game");
              printMenu();
            }
          else {
            game.printHistory();
          }
          break;
        }
        case "J", "j": {
          if (game == null) {
            System.out.println("No game");
            printMenu();
          }
          else {
            System.out.print("\nEnter move number: ");
            String move = sc.nextLine();
            moveNum = Integer.valueOf(move);
            newGame = game.jump(moveNum);
            if (newGame!=game) {
              game = newGame;
            }
            gameMode = Mode.GAME;
          }
          break;
        }
        case "C", "c": {
          if (gameMode == Mode.PAUSE ) {
            gameMode = Mode.GAME;
          }
          else if(enableDebug && gameMode==Mode.DEBUG) { 
            gameMode = Mode.DEBUG;
          }
          else if (gameMode != Mode.NOGAME){
            gameMode = Mode.GAMEOVER;
          }
          break;
        }
        case "Q", "q": {
          break;
        }
        default:{
          System.out.println("\nInvalid option");
          printMenu();
          flag = true;
          break;
        }
      }
            
            
      if (gameMode == Mode.GAME || gameMode == Mode.DEBUG) {
        gameMode = game.playGame(gameMode,sc);
      }

      if (!option.equals("q") && !option.equals("Q") && !flag) {
        if (gameMode == Mode.LOST) {
          System.out.println("\nYOU LOST");
          gameMode = Mode.NOGAME;
        }
        else if (gameMode == Mode.WIN) {
          System.out.println("\nYOU WON");
          gameMode = Mode.NOGAME;
        }
        else if (gameMode == Mode.GAMEOVER) {
          System.out.println("\nGAME OVER");          
        }
        printMenu();
      }

    }while (!option.equals("Q") && !option.equals("q"));
    sc.close();
  }
}
