package org.pjc;

import javax.swing.JButton;

import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.RenderingHints;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.BasicStroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.Cursor;

public class CatButton extends JButton {

	private Shape buttonShape = createButtonShape(false);
	private Dimension buttonDimension = new Dimension(0, 0);
	
	private Color borderColor = Color.black;
	private Color backgroundColor = Color.black;
	private Color textColor = Color.black;
	
	private Color borderColorOnHover = Color.black;
	private Color backgroundColorOnHover = Color.black;
	private Color textColorOnHover = Color.black;
	
	private int borderSize = 1;
	
	private boolean isHovered = false;
	
	public CatButton(String text) {
		super(text);
		setCursor(new Cursor(Cursor.HAND_CURSOR));
		
		addEventListeners();
	}
	
	public CatButton() {
		super();
		addEventListeners();
	}
	
	public void resize(Dimension d) {
		this.buttonDimension = d;
		this.buttonShape = createButtonShape(false);
		
		repaint();
	}
	
	public void setBorderSize(int s) {
		this.borderSize = s;
		
		repaint();
	}
	
	public void setBorderColor(Color c) {
		this.borderColor = c;
		
		repaint();
	}
	
	public void setBackgroundColor(Color c) {
		this.backgroundColor = c;
		
		repaint();
	}
	
	public void setBorderColorOnHover(Color c) {
		this.borderColorOnHover = c;
		
		repaint();
	}
	
	public void setBackgroundColorOnHover(Color c) {
		this.backgroundColorOnHover = c;
		
		repaint();
	}
	
	public void setTextColor(Color c) {
		this.textColor = c;
		
		repaint();
	}
	
	public void setTextColorOnHover(Color c) {
		this.textColorOnHover = c;
		
		repaint();
	}
	
	@Override
	public void paintBorder( Graphics g ) {
		Graphics2D g2d = (Graphics2D)g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setStroke(new BasicStroke(borderSize));

		if(isHovered)
			g2d.setPaint(borderColorOnHover);
		else
			g2d.setPaint(borderColor);
		
        g2d.draw(buttonShape);
    }
	
	@Override
	public void paintImmediately(int x, int y, int w, int h) {

	}
	
	@Override
    public void paintComponent( Graphics g ) {
		Graphics2D g2d = (Graphics2D)g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		if(isHovered)
			g2d.setPaint(backgroundColorOnHover);
		else
			g2d.setPaint(backgroundColor);
		
		Shape background = createButtonShape(true);
        g2d.fill(background);
        
        if(!getText().trim().isEmpty()) {
        	FontMetrics fm = g2d.getFontMetrics();

        	Dimension d = getPreferredSize();
        	double x = (d.getWidth() - fm.stringWidth(getText())) / 2;
        	double y = ((d.getHeight() + .30 * d.getHeight() - fm.getHeight()) / 2) + fm.getAscent();

        	if(isHovered)
        		g2d.setPaint(textColorOnHover);
        	else
        		g2d.setPaint(textColor);
        	
        	g2d.drawString(getText(), (int)x, (int)y);
        }
    }
    
	@Override
    public Dimension getPreferredSize() {
		Dimension d = (buttonDimension == null || (buttonDimension.getWidth() == 0 && buttonDimension.getHeight() == 0)
				? super.getPreferredSize() : buttonDimension);

	    return d;
    }
    
	@Override
    public boolean contains(int x, int y) {
        return buttonShape.contains(x, y);
    }

    private Shape createButtonShape(boolean isBackground) {
        Polygon p = new Polygon();
        
        Dimension d = getPreferredSize();

        if(isBackground) {
	        p.addPoint(borderSize - 1, borderSize - 1);
	        p.addPoint(borderSize - 1, d.height - borderSize);
	        p.addPoint(d.width - borderSize, d.height - borderSize);
	        p.addPoint(d.width - borderSize, borderSize);
	        p.addPoint((int)(d.width  + borderSize - (.15 * (d.width - borderSize))), (int)(.30 * (d.height - borderSize)));
	        p.addPoint((int)(.15 * (d.width - borderSize)), (int)(.30 * (d.height - borderSize)));
        }
        else {
        	p.addPoint(0, 0);
	        p.addPoint(0, d.height - 1);
	        p.addPoint(d.width - 1, d.height - 1);
	        p.addPoint(d.width - 1, 0);
	        p.addPoint((int)(d.width - (.15 * d.width)), (int)(.30 * d.height));
	        p.addPoint((int)(.15 * d.width), (int)(.30 * d.height));
        }

        return p;
    }
    
    private void addEventListeners() {
    	addMouseListener(new MouseAdapter() {
    	    public void mouseEntered(MouseEvent evt) {
    	        isHovered = true;
    	    }

    	    public void mouseExited(MouseEvent evt) {
    	        isHovered = false;
    	    }
    	});
    }
    
}
