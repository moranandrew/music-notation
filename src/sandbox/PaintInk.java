package sandbox;

import graphicsLib.G;
import graphicsLib.UC;
import graphicsLib.Window;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import reaction.Ink;

public class PaintInk extends Window {
  public static Ink.List inkList = new Ink.List();
  static {inkList.add(new Ink());}

  public PaintInk() {
    super("Paint Ink", UC.mainWindowWidth, UC.mainWindowHeight);
  }

  public void paintComponent(Graphics g) {
    G.fillBack(g);
    g.setColor(Color.BLUE);
    g.drawString("points: " + Ink.BUFFER.n, 20, 20);
    // g.drawLine(0, 0, 100, 200);
    inkList.show(g);
    g.setColor(Color.RED);
    Ink.BUFFER.show(g);
  }

  public void mousePressed(MouseEvent me) {
    Ink.BUFFER.dn(me.getX(), me.getY());
    repaint();
  }

  public void mouseDragged(MouseEvent me) {
    Ink.BUFFER.drag(me.getX(), me.getY());
    repaint();
  }

  public void mouseReleased(MouseEvent me) {
    Ink.BUFFER.up(me.getX(), me.getY());
    inkList.add(new Ink());
    repaint();
  }

  public static void main(String[] args) {
    PANEL = new PaintInk();
    Window.launch();
  }
}
