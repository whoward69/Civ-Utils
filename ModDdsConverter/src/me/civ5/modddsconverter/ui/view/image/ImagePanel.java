package me.civ5.modddsconverter.ui.view.image;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import me.civ5.image.ModImage;
import me.civ5.modutils.utils.ModUtilsImages;

public class ImagePanel extends JPanel {
	private static final Color CLIPPING_RECT_COLOR = new Color(200, 200, 255);
	private static final AlphaComposite CLIPPING_ALPHA = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
	
	private boolean tracking = false;

	private int maxWidth, maxHeight;
    private ModImage image = null;
    
    private int displayWidth, displayHeight;
    
    private float clipScale, clipRescale;
	private int clipX, clipY, clipWidth, clipHeight;
	private int clipMinX, clipMinY, clipMaxX, clipMaxY;
	
    public ImagePanel(int maxWidth, int maxHeight) {
    	this.maxWidth = maxWidth;
    	this.maxHeight = maxHeight;
    	
    	setBorder(new EtchedBorder());

    	ImageResizeMouseAdapter mouseAdapter = new ImageResizeMouseAdapter();
		addMouseListener(mouseAdapter);
		addMouseMotionListener(mouseAdapter);
    }
    
    public void setTracking(boolean tracking) {
    	this.tracking = tracking;
    }

    public void setImage(ModImage image) {
    	this.image = image;
    	repaint();
    }
    
    @Override
	public Dimension getPreferredSize() {
		return new Dimension(maxWidth, maxHeight);
	}

	@Override
	public Dimension getMaximumSize() {
		return getPreferredSize();
	}

	@Override
	public Dimension getMinimumSize() {
		return getPreferredSize();
	}
	
	public void setClipping(int actualWidth, int actualHeight, int targetWidth, int targetHeight) {
		clipScale = Math.min(maxWidth / (actualWidth * 1.0f), maxHeight / (actualHeight * 1.0f));

		clipRescale = 1.0f;
		clipWidth = (int) (targetWidth * clipScale);
		clipHeight = (int) (targetHeight * clipScale);
		clipX = (maxWidth - clipWidth) / 2;
		clipY = (maxHeight - clipHeight) / 2;
		
		displayWidth = (int) (actualWidth * clipScale);
		displayHeight = (int) (actualHeight * clipScale);
		
		clipMinX = (maxWidth - displayWidth) / 2;
		clipMinY = (maxHeight - displayHeight) / 2;
		clipMaxX = clipMinX + (displayWidth - clipWidth);
		clipMaxY = clipMinY + (displayHeight - clipHeight);
	}
	
	private int getClipWidth() {
		return (int) (clipWidth * clipRescale);
	}
	
	private int getClipHeight() {
		return (int) (clipHeight * clipRescale);
	}
	
	public int getOffsetX() {
		return (int) ((clipX - clipMinX) / clipScale);
	}
	
	public int getOffsetY() {
		return (int) ((clipY - clipMinY) / clipScale);
	}
	
	public int getTargetWidth() {
		return (int) (clipWidth * clipRescale / clipScale); // Don't use getClipWidth() as it introduces rounding errors too early
	}

	public int getTargetHeight() {
		return (int) (clipHeight * clipRescale / clipScale); // Don't use getClipHeight() as it introduces rounding errors too early
	}

	@Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
        
        if (image != null) {
			int width = image.getWidth();
			int height = image.getHeight();
			float scale = Math.min(maxWidth / (width * 1.0f), maxHeight / (height * 1.0f));
			
        	g.drawImage(ModUtilsImages.getCenteredImage(ModUtilsImages.getScaledImage(image.getData(), (int) (width * scale), (int) (height * scale)), maxWidth, maxHeight), 0, 0, null);
        }

		if (tracking) {
			g2.setColor(CLIPPING_RECT_COLOR);
			g2.setComposite(CLIPPING_ALPHA);
			Rectangle top = new Rectangle(0, 0, image.getWidth(), clipY);
			g2.fill(top);
			Rectangle bottom = new Rectangle(0, clipY + getClipHeight(), image.getWidth(), image.getHeight() - (clipY + getClipHeight()));
			g2.fill(bottom);
			Rectangle left = new Rectangle(0, clipY, clipX, getClipHeight());
			g2.fill(left);
			Rectangle right = new Rectangle(clipX + getClipWidth(), clipY, image.getWidth() - (clipX + getClipWidth()), getClipHeight());
			g2.fill(right);
		}
	}

	private class ImageResizeMouseAdapter extends MouseAdapter {
		int clipDeltaX, clipDeltaY;
		boolean scaling = false;
		
		@Override
		public void mouseEntered(MouseEvent e) {
			super.mouseEntered(e);
			
			if (tracking) {
				if (scaling) {
					setCursor(new Cursor(Cursor.MOVE_CURSOR));
				} else {
					setCursor(new Cursor(Cursor.HAND_CURSOR));
				}
			}
		}

		@Override
		public void mouseExited(MouseEvent e) {
			if (tracking) {
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
			
			super.mouseExited(e);
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			if (tracking) {
				if (inResizeArea(e)) {
					setCursor(new Cursor(Cursor.MOVE_CURSOR));
				} else {
					setCursor(new Cursor(Cursor.HAND_CURSOR));
				}
			}

			super.mouseMoved(e);
		}

		@Override
		public void mousePressed(MouseEvent e) {
			if (inResizeArea(e)) {
				scaling = true;
				clipDeltaX = e.getPoint().x - clipX - getClipWidth();
				clipDeltaY = e.getPoint().y - clipY - getClipHeight();
			} else {
				scaling = false;
				clipDeltaX = e.getPoint().x - clipX;
				clipDeltaY = e.getPoint().y - clipY;
			}

			mouseDragged(e);
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			if (scaling) {
				int cursorX = Math.min(clipMinX + displayWidth, e.getPoint().x - clipDeltaX);
				int cursorY = Math.min(clipMinY + displayHeight, e.getPoint().y - clipDeltaY);
				
				clipRescale = Math.min((cursorX - clipX) / (clipWidth * 1.0f), (cursorY - clipY) / (clipHeight * 1.0f));
			} else {
				clipX = Math.max(clipMinX, Math.min(e.getPoint().x - clipDeltaX, clipMaxX));
				clipY = Math.max(clipMinY, Math.min(e.getPoint().y - clipDeltaY, clipMaxY));
			}
			
			repaint();
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			mouseDragged(e);

			if (scaling) {
				// Recalculate the bottom right extent
				clipMaxX = clipMinX + displayWidth - getClipWidth();
				clipMaxY = clipMinY + displayHeight - getClipHeight();
				scaling = false;
			}
		}
		
		protected boolean inResizeArea(MouseEvent e) {
			int mouseX = e.getPoint().x; 
			int mouseY = e.getPoint().y;
			
			int bottomX = clipX + getClipWidth();
			int bottomY = clipY + getClipHeight();
			
			return (mouseX >= bottomX-10 && mouseX <= bottomX+10 && mouseY >= bottomY-10 && mouseY <= bottomY+10);
		}
	}
}
