package reaction;

import graphicsLib.G;
import graphicsLib.UC;
import java.awt.Color;
import java.awt.Graphics;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeMap;

public class Shape implements Serializable {
  public static TreeMap<String, Shape> DB = loadShapeDB();
  public static Collection<Shape> shapeList = DB.values();  // list backed by DB; changes to DB show here
  public static Shape DOT = DB.get("DOT");
  public Prototype.List prototypes = new Prototype.List();
  public String name;

  public Shape(String name) {this.name = name;}

  public static TreeMap<String, Shape> loadShapeDB(){
    TreeMap<String, Shape> res = new TreeMap<>();
    res.put("DOT", new Shape("DOT"));
    String fileName = UC.shapeDbFileName;
    try{
      System.out.println("attempting DB load...");
      ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName));
      res = (TreeMap<String, Shape>) ois.readObject();
      System.out.println("successful load");
      ois.close();
    } catch(Exception e) {
      System.out.println("load failed");
      System.out.println(e);
    }
    return res;
  }

  public static void saveShapeDB(){
    String fileName = UC.shapeDbFileName;
    try{
      ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName));
      oos.writeObject(DB);
      System.out.println("saved " + fileName);
      oos.close();
    } catch(Exception e) {
      System.out.println("failed database save");
      System.out.println(e);
    }
  }

  public static Shape recognize(Ink ink) {  // can return Null
    if (ink.vs.size.x < UC.dotThreshold && ink.vs.size.y < UC.dotThreshold) {return DOT;}
    Shape bestMatch = null;
    int bestSoFar = UC.noMatchDist;
    for (Shape s: shapeList) {
      int d = s.prototypes.bestDist(ink.norm);
      if (d < bestSoFar) {bestMatch = s; bestSoFar = d;}
    }
    return bestMatch;
  }

  //------------------------------------Prototype--------------------------------------------
  public static class Prototype extends Ink.Norm implements Serializable {
    int nBlend = 1;

    public void blend(Ink.Norm norm) {
      blend(norm, nBlend);
      nBlend ++;
    }

    //----------------------------------List-------------------------------------------------
    public static class List extends ArrayList<Prototype> implements Serializable {
      public static Prototype bestMatch;  // side effect of bestDist

      public int bestDist(Ink.Norm norm) {
        bestMatch = null;
        int bestSoFar = UC.noMatchDist;
        for (Prototype p: this) {
          int d = p.dist(norm);
          if (d < bestSoFar) {
            bestMatch = p;
            bestSoFar = d;
          }
        }
        return bestSoFar;
      }

      private static int m = 10, w = 60;
      private static G.VS showBox = new G.VS(m, m, w, w);

      public void show(Graphics g) {  // show boxes across top of screen
        g.setColor(Color.ORANGE);
        for (int i = 0; i < size(); i++) {
          Prototype p = get(i);
          int x = m + i * (m + w);
          showBox.loc.set(x, m);
          p.drawAt(g, showBox);
          g.drawString("" + p.nBlend, x, 20);
        }
      }
    }
  }
}
