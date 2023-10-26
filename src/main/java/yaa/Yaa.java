package yaa;

import yaa.pojos.jMold.JMold;

import static yaa.pojos.GlobalData.*;

public class Yaa {
  public static void moveYaaResources(){

  }

  public static void initializeYaa(){

  }

  public static void compileYaa(){

  }


  public static void setUpSpecialClasses() {
    double$clz = yaa.pojos.primitives.DoubleMold.newDouble();
    int$clz = yaa.pojos.primitives.IntMold.newInt();
    long$clz = yaa.pojos.primitives.LongMold.newLong();
    float$clz = yaa.pojos.primitives.FloatMold.newFloat();
    short$clz = yaa.pojos.primitives.ShortMold.newShort();
    char$clz = yaa.pojos.primitives.CharMold.newChar();
    byte$clz = yaa.pojos.primitives.ByteMold.newByte();
    boole$clz = yaa.pojos.primitives.BooleMold.newBoole();
    //This must come last. It uses the above classes.
    object$clz = new JMold().newClz("java.lang.Object");
  }

  public static String main$fun$name;
  public static String main$clz$name;
}