package sandbox;

import graphicsLib.G;
import graphicsLib.Window;
import reaction.*;

import java.awt.*;
import java.awt.event.MouseEvent;

public class ReactionTest extends Window {
    static {new Layer("BACK"); new Layer("FOR");}

    public ReactionTest() {
        super("Reaction Test", 1000, 700);
        Reaction.initialReactions.addReaction(new Reaction("SW-SW") {
            public int bid(Gesture g) {return 0;}
            public void act(Gesture g) {new Box(g.vs);}
        });
    }

    public void paintComponent(Graphics g) {
        G.fillBack(g);
        g.setColor(Color.BLUE);
        Ink.BUFFER.show(g);
        Layer.ALL.show(g);
    }

    public void mousePressed(MouseEvent me) {
        Gesture.AREA.dn(me.getX(), me.getY());
        repaint();
    }

    public void mouseDragged(MouseEvent me) {
        Gesture.AREA.drag(me.getX(), me.getY());
        repaint();
    }

    public void mouseReleased(MouseEvent me) {
        Gesture.AREA.up(me.getX(), me.getY());
        repaint();
    }

    public static void main (String[] args) {
        PANEL = new ReactionTest();
        Window.launch();
    }

    //-------------------------------------Box-----------------------------------------
    public static class Box extends Mass {
        public G.VS vs;
        public Color c = G.rndColor();

        public Box(G.VS vs) {
            super("BACK");
            this.vs = vs;
        }

        @Override
        public void show(Graphics g) {
            vs.fill(g, c);
        }
    }
}
