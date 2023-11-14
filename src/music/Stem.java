package music;

import java.awt.*;

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
}
