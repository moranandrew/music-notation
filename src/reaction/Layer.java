package reaction;

import graphicsLib.I;

import java.awt.*;
import java.util.ArrayList;

public class Layer extends ArrayList<I.Show> implements I.Show {
    public String name;

    public Layer(String name) {this.name = name;}



    @Override
    public void show(Graphics g) {
        for (I.Show item : this) {item.show(g);}
    }
}
