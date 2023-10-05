package sandbox;

import graphicsLib.G;
import graphicsLib.G.VS;
import graphicsLib.Window;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class Squares extends Window {
  public static G.VS vs = new VS(100, 100, 200, 300);
  public static Color color = G.rndColor();
  public static Square.List list = new Square.List();

  public Squares() {super("squares", 1000, 700);}

  public void paintComponent(Graphics g) {
    G.fillBack(g);
    vs.fill(g, color);
    list.draw(g);
  }

  public void mousePressed(MouseEvent me) {
    int x = me.getX(), y = me.getY();
    if(vs.hit(x, y)) {color = G.rndColor();}
    list.add(new Square(x, y));
    repaint();
  }

  public static void main(String[] args) {
    PANEL = new Squares();
    Window.launch();
  }

  //---------------------------------------Square--------------------------------------------------

  public static class Square extends G.VS {

    public Color c = G.rndColor();

    public Square(int x, int y) {
      super(x, y, 100, 100);
    }

    //------------------------------------List-----------------------------------------------------
    public static class List extends ArrayList<Square> {

      public void draw(Graphics g) {for (Square s : this) {s.fill(g, s.c);}}
    }
  }
}
