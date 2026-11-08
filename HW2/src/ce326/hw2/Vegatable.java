package ce326.hw2;

public class Vegatable extends BoardElement implements Eatable{

  public Vegatable() {
    super("v");
  }

  public int eaten(){
    return 6;
  }  
}
