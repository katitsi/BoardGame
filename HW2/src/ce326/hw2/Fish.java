package ce326.hw2;

public class Fish extends BoardElement implements Eatable{

  public Fish() {
    super("f");
  }

  public int eaten() {
    return 10;
  }
}
