package reaction;

import graphicsLib.G;
import graphicsLib.I;

import java.util.ArrayList;

public class Gesture {
    private static List UNDO = new List();
    public Shape shape;
    public G.VS vs;

    private Gesture(Shape shape, G.VS vs) {
        this.shape = shape;
        this.vs = vs;
    }

    public static Gesture getNew(Ink ink) {  // can return null
        Shape s = Shape.recognize(ink);
        return (s == null)? null : new Gesture(s, ink.vs);
    }

    // Don't add to UNDO list (it's already on it)
    public void redoGesture() {
        Reaction r = Reaction.best(this);
        if (r != null) {r.act(this);}
    }

    public void doGesture() {
        Reaction r = Reaction.best(this);
        if (r != null) {UNDO.add(this); r.act(this);}
    }

    public static void undo() {
        if (UNDO.size() > 0) {
            UNDO.remove(UNDO.size() - 1);
            Layer.nuke();  // eliminates all teh masses
            Reaction.nuke();  // clear byShape map then reload initial reactions
            UNDO.redo();
        }
    }

    public static I.Area AREA = new I.Area(){
        public boolean hit(int x, int y) {return true;}
        public void dn(int x, int y) {Ink.BUFFER.dn(x, y);}
        public void drag(int x, int y) {Ink.BUFFER.drag(x, y);}
        public void up(int x, int y) {
            Ink.BUFFER.add(x, y);
            Ink ink = new Ink();
            Gesture gest = Gesture.getNew(ink);  // can fail if unrecognized
            Ink.BUFFER.clear();
            if (gest != null) {
                if(gest.shape.name.equals("N-N")) {
                    undo();
                } else {
                    gest.doGesture();
                }
            //    Reaction r = Reaction.best(gest);  // can fail if no reaction wanted
            //    if (r != null) {r.act(gest);}
            }
        }
    };

    //------------------------------lIST-----------------------------------------------
    public static class List extends ArrayList<Gesture> {

        public void redo() {
            for(Gesture g: this) {g.redoGesture();}
        }
    }
}
