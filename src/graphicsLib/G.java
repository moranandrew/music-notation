package graphicsLib;


import java.awt.Color;
import java.awt.Graphics;
import java.io.Serializable;
import java.util.Random;

public class G implements Serializable {

  public static Random RND = new Random();
  public static int rnd(int max) {return RND.nextInt(max);}
  public static Color rndColor() {return new Color(rnd(256), rnd(256), rnd(256));}
  public static void fillBack(Graphics g) {g.setColor(Color.WHITE); g.fillRect(0, 0, 5000, 5000);}

  //-------------------------------------V----------------------------------------------------------
  // Vector
  public static class V implements Serializable{

    public static Transform T = new Transform();

    public int x,y;

    public V(int x, int y) {this.set(x, y);}

    public void set(int x, int y) {this.x = x; this.y = y;}

    public void set(V v) {x = v.x; y = v.y;}

    public void add(V v) {x += v.x; y += v.y;}

    public void blend(V v, int k) {set((k * x + v.x) / (k + 1), (k * y + v.y) / (k + 1));}

    public void setT(V v) {set(v.tx(), v.ty());}

    public int tx() {return x * T.n / T.d + T.dx;}

    public int ty() {return y * T.n / T.d + T.dy;}

    //----------------------------------Transform---------------------------------------------------
    // single scaling for x and y

    //TODO:
    public static class Transform implements Serializable{
      int dx, dy, n , d;  // n = numerator, d = dominator

      public void set() {}

      public void set() {}

      public void setScale() {}

      public int setOff(int )
    }
  }

  //-------------------------------------VS----------------------------------------------------------
  // Vector Size  //GOOD
  public static class VS implements Serializable{
    public V loc, size;

    public VS(int x, int y, int w, int h) {loc = new V(x, y); size = new V(w, h);}

    public void fill(Graphics g, Color c) {
      g.setColor(c);
      g.fillRect(loc.x, loc.y, size.x, size.y);
    }

    public boolean hit(int x, int y) {return loc.x<=x && loc.y<=y && x<=(loc.x + size.x) && y<=(loc.y + size.y);}
  }

  //-------------------------------------LoHi--------------------------------------------------------
  // Two points sorted by lowest then highest value
  public static class LoHi implements Serializable{}

  //-------------------------------------BBox----------------------------------------------------------
  // Bounding Box
  public static class BBox implements Serializable{   // TODO: need to figure this out
    public LoHi h, v;

    public  BBox() {h = new LoHi(0,0); v = LoHi(0,0);}

    public void set(int x, int y) {h.set(x); v.set(y);}

    public void add(int x, int y) {h.add(x); v.add(y);}

    public void add(V v) {add(v.x, v.y);}

    public VS get????() {return new VS(h.lo, v.lo, h.size(), v.size());}

    public void draw(Graphics g) {g.drawRect(h.lo, v.lo, h.size(), v.size());}
  }

  //-------------------------------------PL----------------------------------------------------------
  // Poly Line
  public static class PL implements Serializable{
    public V[] points;

    public PL(int count) {
      points = new V[count];
      for (int i = 0; i < count; i++) {
        points[i] = new V(0, 0);
      }
    }

    public int size() {return points.length;}

    public void drawN(Graphics g, int n) {    // draw N number of points
      for (int i = 1; i < n; i++) {
        g.drawLine(points[i - 1].x, points[i - 1].y, points[i].x, points[i].y);
      }
    }

    public void draw(Graphics g) {drawN(g, size());}
  }
}
