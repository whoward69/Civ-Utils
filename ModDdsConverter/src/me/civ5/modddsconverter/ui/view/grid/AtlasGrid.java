package me.civ5.modddsconverter.ui.view.grid;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import me.civ5.ui.component.ModComboBox;
import me.civ5.xml.XmlHelper;
import me.civ5.xpath.XpathHelper;

import org.jdom.Element;

public class AtlasGrid extends JPanel implements ActionListener {
	public static final int minRows = 1;
	public static final int maxRows = 8;
	public static final int minCols = 2;
	public static final int maxCols = 8;
	
    private int rows, cols;
    private JPanel gridPanel;
    private GridLayout gridLayout;
    
    private AtlasSize rowControl, colControl;
    
    public AtlasGrid(int rows, int cols) {
    	this.rows = 0;
    	this.cols = 0;
    	
		BoxLayout layout = new BoxLayout(this, BoxLayout.Y_AXIS);
		this.setLayout(layout);

    	this.gridLayout = new GridLayout(maxRows, maxCols);
    	this.gridPanel = new JPanel(gridLayout);
    	
    	for (int i = 0; i < maxRows * maxCols; i++) {
    		gridPanel.add(AtlasCellFactory.getAtlasCell());
    		gridPanel.getComponent(i).setVisible(false);
    	}
    	
    	add(gridPanel);
		add(Box.createVerticalGlue());
    	
    	adjustRows(rows);
    	adjustCols(cols);
    	
    	rowControl = new AtlasSize(minRows, maxRows, rows, this);
    	colControl = new AtlasSize(minCols, maxCols, cols, this);
    }
    
	@Override
	public Dimension getPreferredSize() {
    	Dimension cell = AtlasCellFactory.getPreferredSize();
    	return new Dimension(cell.width * maxCols, cell.height * maxRows);
	}

	@Override
	public Dimension getMaximumSize() {
		return getPreferredSize();
	}

	@Override
	public Dimension getMinimumSize() {
		return getPreferredSize();
	}
	
	public int getRows() {
		return rows;
	}

	public int getCols() {
		return cols;
	}

	protected void adjustRows(int newRows) {
    	if (newRows > rows) {
    		while (newRows > rows) {
    			for (int i = 0; i < cols; i++) {
    				gridPanel.getComponent(rows * maxCols + i).setVisible(true);
    			}
    			
    			++rows;
    		}
    	} else if (newRows < rows) {
    		while (newRows < rows) {
    			for (int i = 0; i < cols; i++) {
    				gridPanel.getComponent((rows-1) * maxCols + i).setVisible(false);
    			}

	    		--rows;
    		}
    	}
    }
    
    protected void adjustCols(int newCols) {
    	if (newCols > cols) {
    		while (newCols > cols) {
    			for (int i = 0; i < rows; i++) {
    				gridPanel.getComponent(i * maxCols + cols).setVisible(true);
    			}
	    		
	    		++cols;
    		}
    	} else if (newCols < cols) {
    		while (newCols < cols) {
    			for (int i = 0; i < rows; i++) {
    				gridPanel.getComponent(i * maxCols + (cols-1)).setVisible(false);
    			}
	    		
	    		--cols;
    		}
    	}
    }
    
    public BufferedImage getIconImage(int row, int col, int size) {
    	return ((AtlasCell) gridPanel.getComponent(row * maxCols + col)).getIconImage(size);
    }
    
    public AtlasSize getRowControl() {
    	return rowControl;
    }
    
    public AtlasSize getColControl() {
    	return colControl;
    }
    
	public void serialise(Element container) {
		Element me = XmlHelper.newElement(container, "grid");
		me.setAttribute("rows", Integer.toString(rows));
		me.setAttribute("cols", Integer.toString(cols));

		for (int r = 0; r < maxRows; r++) {
			Element row = XmlHelper.newIdElement(me, "row", Integer.toString(r));
			for (int c = 0; c < maxCols; c++) {
				Element col = XmlHelper.newIdElement(row, "col", Integer.toString(c));
				col.setText(Integer.toString(((AtlasCell) gridPanel.getComponent(r * maxCols + c)).getSelectedIndex()));
			}
		}
	}
	
	public void deserialise(Element container) {
		Element me = XpathHelper.getElement(container, "./grid");
		
		adjustRows(XpathHelper.getInt(me, "./@rows", 2));
		adjustCols(XpathHelper.getInt(me, "./@cols", 2));

		for (int r = 0; r < maxRows; r++) {
			for (int c = 0; c < maxCols; c++) {
				((AtlasCell) gridPanel.getComponent(r * maxCols + c)).setSelectedIndex(XpathHelper.getInt(me, "./row[@id='" + Integer.toString(r) + "']/col[@id='" + Integer.toString(c) + "']", 0));
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == rowControl) {
			adjustRows(rowControl.getValue());
		} else if (e.getSource() == colControl) {
			adjustCols(colControl.getValue());
		}
	}
	

	public class AtlasSize extends ModComboBox<Integer> {
		int min;
		
		public AtlasSize(int min, int max, int selected, ActionListener listener) {
			this.min = min;
			
			for (int i = min; i <= max; i++) {
				addItem(new Integer(i));
			}
			
			setSelectedIndex(selected - min);
			
			addActionListener(listener);
		}
		
		public int getValue() {
			return getSelectedIndex() + min;
		}
	}
}
