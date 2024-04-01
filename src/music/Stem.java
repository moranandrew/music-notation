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

    public Stem(Staff staff, Head.List heads, boolean up) {
        this.staff = staff;
        // staff.sys.stems.addStem(this);  // this is done in the Time class
        isUp = up;
        for (Head h: heads) {h.unStem(); h.stem = this;}
        this.heads = heads;
        staff.sys.stems.addStem(this);
        setWrongSides();

        addReaction(new Reaction("E-E") {  // increment flags
            @Override
            public int bid(Gesture g) {
                int y = g.vs.yM(), x1 = g.vs.xL(), x2 = g.vs.xH();
                int xS = Stem.this.heads.get(0).time.x;
                if (x1 > xS || x2 < xS) {return UC.noBid;}
                int y1 = Stem.this.yLo(), y2 = Stem.this.yHi();
                if (y < y1 || y > y2) {return UC.noBid;}
                return Math.abs(y - (y1 + y2)/2) + 100;  // bias added to allow sys.E-E to win
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

    public static Stem getStem(Staff staff, Time time, int y1, int y2, boolean up) {
        Head.List heads = new Head.List();
        for (Head h: time.heads) {
            int yH = h.y();
            if (yH > y1 && yH < y2) {heads.add(h);}
        }
        if (heads.size() == 0) {return null;}  // no stem created if there are no heads
        Beam b = internalStem(staff.sys, time.x, y1, y2);
        Stem res = new Stem(staff, heads, up);
        if (b != null) {b.addStem(res); res.nFlag = 1;}
        return res;
    }

    private static Beam internalStem(Sys sys, int x, int y1, int y2) {  // returns non-Null if we find a beam crossed by a line
        for (Stem s: sys.stems) {
            if (s.beam != null && s.x() < x && s.yLo() < y2 && s.yHi() > y1) {
                int bX = s.beam.first().x(), bY = s.beam.first().yBeamEnd();
                int eX = s.beam.last().x(), eY = s.beam.last().yBeamEnd();
                if (Beam.verticalLineCrossesSegment(x, y1, y2, bX, bY, eX, eY)){return s.beam;}
            }
        }
        return null;
    }

    public void show(Graphics g){
        if (nFlag >= -1 && heads.size() > 0) {
            int x = x(), h = staff.H(), yH = yFirstHead(), yB = yBeamEnd();
            g.drawLine(x, yH, x, yB);
            if (nFlag > 0 && beam == null) {
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

    public boolean isInternalStem() {
        return beam != null && beam.stems != null && this != beam.first() && this != beam.last();
    }

    public int yBeamEnd() {
        if (isInternalStem()){beam.setMasterBeam(); return Beam.yOfX(x());}
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

        public boolean fastReject(int y1, int y2){
//            return y2 < yMin || y1 > yMax;
            return false;
        }

        public void sort() {Collections.sort(this);}

        public Stem.List allIntersectors(int x1, int y1, int x2, int y2){
            Stem.List res = new Stem.List();
            for (Stem s: this) {
                int x = s.x(), y = Beam.yOfX(x, x1, y1, x2, y2);
                if (x > x1 && x < x2 && y > s.yLo() && y < s.yHi()) {res.add(s);}
            }
            return res;
        }
    }
}
