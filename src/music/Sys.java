package music;

import reaction.Mass;

import java.awt.*;
import java.util.ArrayList;

import static music.AppMusicEd.PAGE;

public class Sys extends Mass {
    public Staff.List staffs = new Staff.List();
    public Page page = PAGE;
    public int iSys;
    public Sys.Fmt fmt;

    public Sys(int iSys, Sys.Fmt sysFmt) {
        super("BACK");
        this.iSys = iSys;
        this.fmt = sysFmt;
        for (int i = 0; i < sysFmt.size(); i++) {
            staffs.add(new Staff(i, sysFmt.get(i)));
        }
    }

    public int yTop() {return page.sysTop(iSys);}



    @Override
    public void show(Graphics g) {
        int y = yTop(), x = PAGE.margins.left;
        g.drawLine(x, y, x, y + fmt.height());
    }

    //--------------------------------------------Fmt-----------------------------------------------
    public static class Fmt extends ArrayList<Staff.Fmt> {
        public ArrayList<Integer> staffOffset = new ArrayList<>();

        public int height() { int last = size() - 1; return staffOffset.get(last) + get(last).height();}

        public void showAt(Graphics g, int y) {
            for (int i = 0; i < size(); i++) {
                get(i).showAt(g, y + staffOffset.get(i));
            }
        }
    }

    //------------------------------------------List-------------------------------------------------
    public static class List extends ArrayList<Sys> {

    }
}
