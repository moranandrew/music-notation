package reaction;

import graphicsLib.G;
import graphicsLib.G.V;
import graphicsLib.I;
import graphicsLib.UC;
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

public class Ink implements I.Show {

  public static Buffer BUFFER = new Buffer();
  public Norm norm;
  public G.VS vs;

  public Ink() {
    norm = new Norm();
    vs = BUFFER.bBox.getNewVS();
  }

  @Override
  public void show(Graphics g) {
    // g.setColor(Color.RED);
    // g.fillRect(100, 100, 100, 100);
    //draw(g);
    g.setColor(UC.inkColor);
    norm.drawAt(g, vs);
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

    //show the actual buffer
    @Override
    public void show(Graphics g) {drawN(g, n); bBox.draw(g);}
  }

  //------------------------------Norm-----------------------------------------
  // normalized coordinate system

  public static class Norm extends G.PL {
    public static final int N = UC.normSampleSize, MAX = UC.normCoordMax;
    public static final G.VS NCS = new G.VS(0, 0, MAX, MAX); // coordinate box for transform

    public Norm() {
      super(N);
      BUFFER.superSample(this);
      G.V.T.set(BUFFER.bBox, NCS);
      this.transform();
    }

    public void drawAt(Graphics g, G.VS vs) {
      G.V.T.set(NCS, vs);
      for(int i = 1; i < N; i++) {
        g.drawLine(points[i-1].tx(), points[i-1].ty(), points[i].tx(), points[i].ty());
      }
    }

    public int dist(Norm norm) {
      int res = 0;
      for (int i=0; i < N; i++) {
        int dx = points[i].x - norm.points[i].x, dy = points[i].y - norm.points[i].y;
        res += dx * dx + dy * dy;
      }
      return res;
    }

    public void blend(Norm norm, int nBlend) {
      for (int i = 0; i < N; i++) {
        points[i].blend(norm.points[i], nBlend);
      }
    }
  }
}
