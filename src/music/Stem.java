package music;

import graphicsLib.UC;
import reaction.Gesture;
import reaction.Reaction;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

public class Stem extends Duration implements Comparable<Stem> {
    public Staff staff;
    public Head.List heads = new Head.List();
    public boolean isUp = true;
    public Beam beam = null;

    public Stem(Staff staff, boolean up) {
        this.staff = staff;
        // staff.sys.stems.addStem(this);  // this is done in the Time class
        isUp = up;

        addReaction(new Reaction("E-E") {  // increment flags
            @Override
            public int bid(Gesture g) {
                int y = g.vs.yM(), x1 = g.vs.xL(), x2 = g.vs.xH();
                int xS = Stem.this.heads.get(0).time.x;
                if (x1 > xS || x2 < xS) {return UC.noBid;}
                int y1 = Stem.this.yLo(), y2 = Stem.this.yHi();
                if (y < y1 || y > y2) {return UC.noBid;}
                return Math.abs(y - (y1 + y2)/2);
            }

            @Override
            public void act(Gesture g) {Stem.this.incFlag();}
        });

        addReaction(new Reaction("W-W") {  // decrement flags
            @Override
            public int bid(Gesture g) {
                int y = g.vs.yM(), x1 = g.vs.xL(), x2 = g.vs.xH();
                int xS = Stem.this.heads.get(0).time.x;
                if (x1 > xS || x2 < xS) {return UC.noBid;}
                int y1 = Stem.this.yLo(), y2 = Stem.this.yHi();
                if (y < y1 || y > y2) {return UC.noBid;}
                return Math.abs(y - (y1 + y2)/2);
            }

            @Override
            public void act(Gesture g) {Stem.this.decFlag();}
        });
    }

    public void show(Graphics g){
        if (nFlag >= -1 && heads.size() > 0) {
            int x = x(), h = staff.H(), yH = yFirstHead(), yB = yBeamEnd();
            g.drawLine(x, yH, x, yB);
            if (nFlag > 0) {
                if (nFlag == 1) {(isUp? Glyph.FLAG1D:Glyph.FLAG1U).showAt(g, h, x, yB);}
                if (nFlag == 2) {(isUp? Glyph.FLAG2D:Glyph.FLAG2U).showAt(g, h, x, yB);}
                if (nFlag == 3) {(isUp? Glyph.FLAG3D:Glyph.FLAG3U).showAt(g, h, x, yB);}
                if (nFlag == 4) {(isUp? Glyph.FLAG4D:Glyph.FLAG4U).showAt(g, h, x, yB);}
            }
        }
    }

    public Head firstHead() {return heads.get(isUp? heads.size() - 1:0);}  // either first or last

    public Head lastHead() {return heads.get(isUp? 0: heads.size() - 1);}  // either first or last

    public int yFirstHead() {Head h = firstHead(); return h.staff.yLine(h.line);}

    public int yBeamEnd() {
        Head h = lastHead();
        int line = h.line;
        line += isUp? - 7:7;  // up stem or down stem
        int flagInc = nFlag > 2? 2*(nFlag -2):0;
        line += isUp? - flagInc:flagInc;
        if ((isUp && line > 4) || (!isUp && line < 4)) {line = 4;}
        return h.staff.yLine(line);
    }

    public int x() {Head h = firstHead(); return h.time.x + (isUp? h.W():0);}

    public int yLo() {return isUp? yBeamEnd():yFirstHead();}

    public int yHi() {return isUp? yFirstHead():yBeamEnd();}

    public void deleteStem() {
        staff.sys.stems.remove(this);
        deleteMass();
    }

    public void setWrongSides() {
        Collections.sort(heads);
        int i, last, next;
        if (isUp) {
            i = heads.size() - 1;
            last = 0;
            next = -1;
        } else {
            i = 0;
            last = heads.size() - 1;
            next = 1;
        }
        Head pH = heads.get(i); // previous head
        pH.wrongSide = false;
        while (i != last) {
            i += next;
            Head nH = heads.get(i);  // next head
            nH.wrongSide = pH.staff == nH.staff && (Math.abs(nH.line - pH.line)<= 1 && !pH.wrongSide);
            pH = nH;
        }
    }

    @Override
    public int compareTo(Stem s) {return x() - s.x();}

    //-------------------------------------------List--------------------------------------------------

    public static class List extends ArrayList<Stem> {
        public int yMin = 1_000_000, yMax = -1_000_000;

        public void addStem(Stem s) {
            add(s);
            if (s.yLo() < yMin) {yMin = s.yLo();}
            if (s.yHi() < yMax) {yMax = s.yHi();}
        }

        public boolean fastReject(int y){return y < yMin || y > yMax;}

        public void sort() {Collections.sort(this);}
    }
}
