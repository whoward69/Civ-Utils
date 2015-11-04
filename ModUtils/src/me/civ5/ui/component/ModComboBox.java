package me.civ5.ui.component;

import java.awt.Dimension;

import javax.swing.JComboBox;

public class ModComboBox<E> extends JComboBox<E> {
    @Override
    public Dimension getMaximumSize() {
        Dimension max = super.getMaximumSize();
        max.height = getPreferredSize().height;
        return max;
    }
}
