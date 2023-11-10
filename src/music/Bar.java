package music;

import graphicsLib.UC;
import reaction.Gesture;
import reaction.Mass;
import reaction.Reaction;

import java.awt.*;

import static music.AppMusicEd.PAGE;

public class Bar extends Mass {
    public static final int FAT = 0x2, RIGHT = 0x4, LEFT = 0x8;  // Bits in Bar Type
    public Sys sys;
    public int x, barType = 0;


    public Bar(Sys sys, int x) {
        super("BACK");
        this.sys = sys;
        this.x = x;
        if (Math.abs(PAGE.margins.right - x) < UC.barToMarginSnap) {
            this.x = PAGE.margins.right;
        }

        addReaction(new Reaction("S-S") {  // cycle barType
            @Override
            public int bid(Gesture g) {
                int x = g.vs.xM();
                if (Math.abs(x - Bar.this.x) > UC.barToMarginSnap) {return UC.noBid;}
                int y1 = g.vs.yL(), y2 = g.vs.yH();
                if (y1 < Bar.this.sys.yTop() - 20 || y2 > Bar.this.sys.yBot() + 20){return UC.noBid;}
                return Math.abs(x - Bar.this.x);
            }

            @Override
            public void act(Gesture g) {Bar.this.cycleType();}
        });

        addReaction(new Reaction("DOT") {  // putting a dot on the bar
            @Override
            public int bid(Gesture g) {
                int x = g.vs.xM(), y = g.vs.yM();
                if (y < Bar.this.sys.yTop() || y > Bar.this.sys.yBot()) {return UC.noBid;}
                int dist = Math.abs(x - Bar.this.x);
                if (dist > 3*PAGE.sysFmt.maxH) {return UC.noBid;}
                return dist;
            }

            @Override
            public void act(Gesture g) {
                if (g.vs.xM() < Bar.this.x) {Bar.this.toggleLeft();}
                else{Bar.this.toggleRight();}
            }
        });
    }

    public void cycleType() {barType++; if (barType > 2) {barType = 0;}}

    public void toggleLeft() {barType = barType ^ LEFT;}  // exclusive Or for toggling bits

    public void toggleRight() {barType = barType ^ RIGHT;}  // exclusive Or for toggling bits

    @Override
    public void show(Graphics g) {
        int sysTop = sys.yTop(), y1 = 0, y2 = 0;  // y1, y2, top and bottom of connected component
        boolean justSawBreak = true;
        for (int i = 0; i < sys.fmt.size(); i++) {
            Staff.Fmt sf = sys.fmt.get(i);
            Staff staff = sys.staffs.get(i);
            if (justSawBreak) {y1 = staff.yTop();}
            y2 = staff.yBot();
            if (!sf.barContinues) {
                drawLines(g, x, y1, y2);
            }
            justSawBreak = !sf.barContinues;
            if (barType > 3) {
                drawDots(g, x, staff.yTop());
            }
        }

    }

    public static void wings(Graphics g, int x, int y1, int y2, int dX, int dY) {
        g.drawLine(x, y1, x + dX, y1 - dY);
        g.drawLine(x, y2, x + dX, y2 + dY);
    }

    public static void fatBar(Graphics g, int x, int y1, int y2, int dX) {
        g.fillRect(x, y1, dX, y2 - y1);
    }

    public static void thinBar(Graphics g, int x, int y1, int y2) {
        g.drawLine(x, y1, x, y2);
    }

    public void drawDots(Graphics g, int x, int top) {
        int H = PAGE.sysFmt.maxH;
        if ((barType & LEFT) != 0) {
            g.fillOval(x - 3 * H, top + 11 * H / 4, H / 2, H / 2);
            g.fillOval(x - 3 * H, top + 19 * H / 4, H / 2, H / 2);
        }
        if ((barType & RIGHT) != 0) {
            g.fillOval(x + 3 * H / 2, top + 11 * H / 4, H / 2, H / 2);
            g.fillOval(x + 3 * H / 2, top + 19 * H / 4, H / 2, H / 2);
        }
    }

    public void drawLines(Graphics g, int x, int y1, int y2) {
        int H = PAGE.sysFmt.maxH;
        if (barType == 0) {thinBar(g, x, y1, y2);}
        if (barType == 1) {thinBar(g, x, y1, y2); thinBar(g, x - H, y1, y2);}
        if (barType == 2) {fatBar(g, x - H, y1, y2, H); thinBar(g, x - 2*H, y1, y2);}
        if (barType >= 4) {
            fatBar(g, x - H, y1, y2, H);
            if ((barType & LEFT) != 0) {
                thinBar(g, x - 2*H, y1, y2);
                wings(g, x - 2*H, y1, y2, -H, H);}
            if ((barType & RIGHT) != 0) {
                thinBar(g, x + H, y1, y2);
                wings(g, x + H, y1, y2, H, H);}
        }
    }
}
