package music;

import graphicsLib.G;
import graphicsLib.UC;
import graphicsLib.Window;
import reaction.Gesture;
import reaction.Ink;
import reaction.Layer;
import reaction.Reaction;

import java.awt.*;
import java.awt.event.MouseEvent;

public class AppMusicEd extends Window {
    static {new Layer("BACK"); new Layer("FORE");}
    public static Page PAGE;

    public AppMusicEd() {
        super("music editor", UC.mainWindowWidth, UC.mainWindowHeight);
        Reaction.initialReactions.addReaction(new Reaction("E-E"){
            public int bid(Gesture g) {return 10;}

            public void act(Gesture g) {
                int y = g.vs.yM();
                Sys.Fmt sysFmt = new Sys.Fmt();
                PAGE = new Page(sysFmt);
                PAGE.margins.top = y;
                PAGE.addNewSys();
                PAGE.addNewStaff(0);
                this.disable();
            }
        });
    }

    public void paintComponent(Graphics g) {
        G.fillBack(g);
        g.setColor(Color.BLACK);
        Layer.ALL.show(g);
        Ink.BUFFER.show(g);
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

    public static void main(String[] args) {
        PANEL = new AppMusicEd();
        Window.launch();
    }
}
