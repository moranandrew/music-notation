package music;

import reaction.Mass;

import java.awt.*;
import java.util.ArrayList;

import static music.AppMusicEd.PAGE;

public class Staff extends Mass {
    public Sys sys;
    public int iStaff;  // staff index
    public Staff.Fmt fmt;

    public Staff(int iStaff, Staff.Fmt staffFmt){
        super("BACK");
        this.iStaff = iStaff;
        fmt = staffFmt;
    }

    @Override
    public void show(Graphics g) {}


    //------------------------------------------Fmt------------------------------------------------

    public static class Fmt{
        public int nLines = 5, H = 8;

        public int height() {return 2 * H * (nLines - 1);}

        public void showAt(Graphics g, int y) {
            int LEFT = PAGE.margins.left, RIGHT = PAGE.margins.right;
            for (int i = 0; i < nLines; i++) {
                g.drawLine(LEFT, y + 2 * H * i, RIGHT, y + 2 * H * i);
            }
        }
    }

    //------------------------------------------List-------------------------------------------------
    public static class List extends ArrayList<Staff> {

    }
}
