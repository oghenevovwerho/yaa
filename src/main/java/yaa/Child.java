package yaa;

public class Child extends Parent {
  public Child(String name) {
    super(name);
  }

  public void test() {
    System.out.println(super.email);
  }
}