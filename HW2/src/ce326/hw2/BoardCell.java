package ce326.hw2;

import java.util.LinkedList;

public class BoardCell {
  LinkedList<BoardElement> elements;
  boolean isVisited;

  public BoardCell(LinkedList<BoardElement> elements) {
    this.elements = new LinkedList<BoardElement>();
    this.elements = elements;
  }

  public BoardCell() {
    this.elements = new LinkedList<BoardElement>();
  }

  public BoardCell(BoardCell cell) {
    if (cell!=null) {
      this.isVisited = cell.isVisited();
      this.elements = new LinkedList<BoardElement>();
      for (BoardElement element: cell.getElements()) {
        this.elements.addLast(element);
      }
    }
  }

  LinkedList<BoardElement> getElements() {
    return this.elements;
  }

  void addElement(BoardElement element) {
    this.elements.addLast(element);
  }

  void setElements(LinkedList<BoardElement> elements) {
    this.elements = elements;
  }

  void removeElements() {
    this.elements.clear();
    this.elements = null;
  }

  void removeElement() {
    if (!this.elements.isEmpty()) {
      this.elements.removeLast();
    }
  }

  void playerVisited(boolean isVisited) {
    this.isVisited = isVisited;
  }

  public boolean isVisited() {
    return isVisited;
  }
    
}
