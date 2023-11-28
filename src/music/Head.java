package music;

import graphicsLib.UC;
import reaction.Gesture;
import reaction.Mass;
import reaction.Reaction;

import java.awt.*;
import java.util.ArrayList;

public class Head extends Mass implements Comparable<Head> {
    public Staff staff;
    public int line;  // line 0: top line, line 1: first space, line 2: next line.
    public Time time;
    public Glyph forcedGlyph = null;  // keeping a space for future addition of other shapes
    public Stem stem = null;  // can be null
    public boolean wrongSide = false;

    public Head(Staff staff, int x, int y) {
        super("NOTE");
        this.staff = staff;
        this.time = staff.sys.getTime(x);
        line = staff.lineOfY(y);
        time.heads.add(this);
        System.out.println("Head constructor line: " + line);

        addReaction(new Reaction("S-S") {
            @Override
            public int bid(Gesture g) {
                int x = g.vs.xM(), y1 = g.vs.yL(), y2 = g.vs.yH();
                int w = Head.this.W(), hY = Head.this.y();
                if (y1 > y || y2 < y) {return UC.noBid;}
                int hL = Head.this.time.x, hR = hL + w;
                if (x < hL - w || x > hR + w) {return UC.noBid;}
                if (x < hL + w/2) {return hL - x;}
                if (x > hR - w/2) {return x - hR;}
                return UC.noBid;
            }

            @Override
            public void act(Gesture g) {
                int x = g.vs.xM(), y1 = g.vs.yL(), y2 = g.vs.yH();
                Staff STAFF = Head.this.staff;
                Time t = Head.this.time;
                int w = Head.this.W();
                boolean up = x > t.x + w/2;
                if (Head.this.stem == null) {
//                    t.stemHeads(STAFF, up, y1, y2);
                    Stem.getStem(staff, time, y1, y2, up);
                } else {
                    t.unStemHeads(y1, y2);
                }
            }
        });

        addReaction(new Reaction("DOT") {
            @Override
            public int bid(Gesture g) {
                int xH = Head.this.x(), yH = Head.this.y(), h = Head.this.H(), w = Head.this.W();
                int x = g.vs.xM(), y = g.vs.yM();
                if (x < xH || x > xH + 2*w || y < yH - h || y > yH + h) {return UC.noBid;}
                return Math.abs(xH + w - x) + Math.abs(yH - y);
            }

            @Override
            public void act(Gesture g) {if (Head.this.stem != null){Head.this.stem.cycleDot();}}
        });
    }

    @Override
    public void show(Graphics g) {
        int H = staff.H();
        g.setColor(wrongSide? Color.GREEN : Color.BLUE);
        if (stem != null && stem.heads.size() != 0 && this == stem.firstHead()) {
            g.setColor(Color.RED);
        }
        // Glyph.HEAD_Q.showAt(g, H, time.x, staff.yTop() + line*H);
        (forcedGlyph == null? normalGlyph() : forcedGlyph).showAt(g, H, x(), y());
        if (stem != null) {
            int off = UC.gapRestToFirstDot, sp = UC.gapBetweenAugDot;
            for (int i=0; i < stem.nDot; i++) {
                g.fillOval(time.x + off + i*sp, y() - 3*H/2, 2*H/3, 2*H/3);
            }
        }
    }

    public int W() {return 24*staff.H()/10;}  // calculate width of single head

    public int H() {return staff.H();}

    public int y() {return staff.yLine(line);}

    public int x() {
        int res = time.x;
        if (wrongSide) {
            res += (stem!=null && stem.isUp)? W():-W();
        }
        return res;
    }

    public Glyph normalGlyph() {
        if (stem == null) {return Glyph.HEAD_Q;}
        if (stem.nFlag == -1) {return Glyph.HEAD_HALF;}
        if (stem.nFlag == -2) {return Glyph.HEAD_W;}
        return Glyph.HEAD_Q;
    }

    public void deleteMass() {  // this is a STUB
        time.heads.remove(this);
    }

    public void unStem() {
        if (stem != null) {
            stem.heads.remove(this);
            if (stem.heads.size() == 0) {stem.deleteStem();}
            stem = null;
            wrongSide = false;
        }
    }

//    public void joinStem(Stem s) {
//        if (stem != null) {unStem();}  // make sure this head is not on some other stem
//        s.heads.add(this);
//        stem = s;
//    }

    @Override
    public int compareTo(Head h) {
        return (staff.iStaff != h.staff.iStaff)? staff.iStaff - h.staff.iStaff : line - h.line;
    }

    //------------------------------------------List-------------------------------------------------------
    public static class List extends ArrayList<Head>{

    }
}
