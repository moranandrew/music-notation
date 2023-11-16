package music;

import java.awt.*;
import java.util.Collections;

public class Stem extends Duration {
    public Staff staff;
    public Head.List heads = new Head.List();
    public boolean isUp = true;

    public Stem(Staff staff, boolean up) {
        this.staff = staff;
        isUp = up;
    }

    public void show(Graphics g){
        if (nFlag >= -1 && heads.size() > 0) {
            int x = x(), h = staff.H(), yH = yFirstHead(), yB = yBeamEnd();
            g.drawLine(x, yH, x, yB);
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

    public void deleteStem() {
        deleteMass();}

    public void setWrongSides() {  // STUB
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
            nH.wrongSide = (Math.abs(nH.line - pH.line) <= 1 && !pH.wrongSide);
            pH = nH;
        }
    }
}
