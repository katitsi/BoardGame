package ce326.hw2;

public abstract class BoardElement {
  String symbol;

  public BoardElement(String symbol) {
    this.symbol = symbol;
  }

  String getSymbol() {
    return this.symbol;
  }

  void setSymbol(String symbol) {
    this.symbol = symbol;
  }

  @Override
  public String toString() {
    return " " + this.symbol;
  }
}
