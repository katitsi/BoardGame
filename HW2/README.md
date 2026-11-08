## Java Console Action Game

A turn-based console Java game in which the player (X) navigates a board, collects food (v,f,m) to maintain energy, avoids ghosts (@) and attempts to visit all the board's traversable cells (-). Ghosts pursue the player using shortest-path distances calculated with Dijkstra's algorithm

This is a academic project for Object-Oriented Programming

## Gameplay

- The board is loaded from a JSON file. Its edges wrap around, so the board behaves like a torus.
- **Player (X)** moves to any of the 8 neighbouring cells. Each move costs 1 energy.
- **Food** restores energy: vegetable v +6, fish f +10, meat m +14.
- **Obstacles (#)** cannot be entered.
- **Ghosts (@)** move in 4 directions after each player move. Each ghost picks at random one of the neighbouring cells that bring it strictly closer to the player (dikstra's algorithm). Two ghosts never share a cell.
- **You win** when every non-obstacle cell has been visited.
- **You lose** when your energy reaches 0 or a ghost reaches you. The **game is over** when you can no longer move.

## Menu

| Key | Action |
| L/l | Load a board from a JSON file |
| D/d | Toggle debug mode (prints the ghost distance map every turn)|
| H/h | Print the move history of player and ghosts as JSON |
| J/j | Jump back to an earlier move (undo through snapshots) |
| C/c | Continue a paused game |
| Z/z | Pause and return to the menu (in game) |
| Q/q | Quit the program |

Moves are typed as (row letter)(column number), e.g A2

## Technical Highlights

- **Shortest-path**: a Dijkstra search (priority queue) runs from the player's position each turn and produces a distance map over the wrap-around grid. Ghosts move down this distance gradient. In debug mode the map is printed.
- **Undo/Jump**: the game takes a deep-copy snapshot of the state (board, ghosts, history) every turn, and J/j restores any earlier one.
- **Move history** is kept in a TreeMap and exported in JSON format.
- **Object-oriented model**: an abstract BoardElement with concrete pieces (Player, Ghost, Obstacle, Vegatable, Fish, Meat). The interfaces Movable and Eatable describe movement and food. A BoardCell holds a stack of elements, so a ghost can stand on food.

## Board File Format

```json
{
  "rows": 4,
  "columns": 5,
  "energy": 10,
  "init": [
    "X",
    "-",
    "v",
    "-",
    "#",
    "-",
    "#",
    "-",
    "f",
    "-",
    "-",
    "-",
    "m",
    "-",
    "@",
    "#",
    "-",
    "-",
    "-",
    "-"
  ]
}
```

`init` must contain exactly `rows x columns` entries. Any symbol other than `X @ # v f m` counts as an empty cell. The should be only one player `(X)`

## Requirements

- Java Development Kit **(JDK 14+)**
- [org.json](https://mvnrepository.com/artifact/org.json/json) jar.

## Installation
