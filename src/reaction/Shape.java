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
  public static Database DB = Database.load();
  public static Collection<Shape> shapeList = DB.values();  // list backed by DB; changes to DB show here
  public static Shape DOT = DB.get("DOT");
  public Prototype.List prototypes = new Prototype.List();
  public String name;

  public Shape(String name) {this.name = name;}

  public static void saveShapeDB() {Database.save();}
/**
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
 */

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

      public void train(Ink.Norm norm) {
        if (bestDist(norm) < UC.noMatchDist) {  // found match, so blend
          bestMatch.blend(norm);
        } else {  // no match, so add new prototype
          add(new Shape.Prototype());
        }
      }

      private int showNdx(int x) {return x / (m + w);}

      public boolean isShowDelete(G.VS vs) {
        return vs.loc.y < m + w && showNdx(vs.loc.x) < size();
      }

      public void showDelete(G.VS vs) {remove(showNdx(vs.loc.x));}

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
  //----------------------------------Database------------------------------------------
  public static class Database extends TreeMap<String, Shape> {

    private Database() {
      super();
      String dot = "DOT";
      put(dot, new Shape(dot));
    }

    private Shape forceGet(String name) {  // Always will return a shape
      if (!DB.containsKey(name)) {DB.put(name, new Shape(name));}
      return DB.get(name);
    }

    public void train(String name, Ink.Norm norm) {
      if (isLegal(name)) {
        forceGet(name).prototypes.train(norm);
      }
    }

    public static boolean isLegal(String name) {
      return !name.equals("") && !name.equals("DOT");
    }

    public static Database load() {
      Database db = null;
      try{
        System.out.println("attempting DB load...");
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(UC.shapeDbFileName));
        db = (Database)ois.readObject();
        System.out.println("successful load");
        ois.close();
      } catch(Exception e) {
        System.out.println("load failed");
        System.out.println(e);
        db = new Database();
      }
      return db;
    }

    public static void save() {
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
  }
}
