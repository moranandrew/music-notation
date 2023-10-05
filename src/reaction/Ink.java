package reaction;

import graphicsLib.G;
import graphicsLib.I;
import graphicsLib.UC;
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

public class Ink extends G.PL implements I.Show {
  public static Buffer BUFFER = new Buffer();

  public Ink() {
    super(BUFFER.n);
    for(int i = 0; i < BUFFER.n; i++) {
      points[i].set(BUFFER.points[i]);
    }
  }

  @Override
  public void show(Graphics g) {
    // g.setColor(Color.RED);
    // g.fillRect(100, 100, 100, 100);
    draw(g);
  }

  //------------------------------------------LIST--------------------------------------------------
  public static class List extends ArrayList<Ink> implements I.Show {

    @Override
    public void show(Graphics g) {
      for (Ink ink:this) {ink.show(g);}
    }
  }

  //-----------------------------------------BUFFER-------------------------------------------------
  public static class Buffer extends G.PL implements I.Show, I.Area {
    public static final int MAX = UC.inkBufferMax;
    public int n;  // this is how many points are in the buffer

    private Buffer() {super(MAX);}  // is private to make sure no one else can create one.

    public void clear() {n = 0;}  // clear the buffer

    public void add(int x, int y) {
      if (n < MAX) {points[n++].set(x, y);}
    }

    @Override
    public boolean hit(int x, int y) {return true;}

    @Override
    public void dn(int x, int y) {clear(); add(x, y);}

    @Override
    public void up(int x, int Y) {

    }

    @Override
    public void drag(int x, int y) {add(x, y);}

    @Override
    public void show(Graphics g) {drawN(g, n);}
  }
}
