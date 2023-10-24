package reaction;

import graphicsLib.I;

import java.util.ArrayList;
import java.util.TreeMap;

public abstract class Reaction implements I.React {
    public static Map byShape = new Map();
    public static List initialReactions = new List();  // used by undo to restart everything

    public Shape shape;

    public Reaction(String shapeName) {
        shape = Shape.DB.get(shapeName);
        if (shape == null) {System.out.println("WTF? - Shape DB does not have: " + shapeName);}
    }


    //--------------------------------------List----------------------------------------------
    public static class List extends ArrayList<Reaction> {

    }

    //--------------------------------------Map------------------------------------------------
    public static class Map extends TreeMap<Shape, List> {

    }
}
