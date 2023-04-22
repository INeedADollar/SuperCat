package org.pjc;

import javax.swing.JTextField;
import javax.swing.text.Document;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class LineEdit extends JTextField {
    private String placeholder;

    public LineEdit() {
    	
    }

    public LineEdit(Document pDoc, String pText, int pColumns) {
        super(pDoc, pText, pColumns);
    }

    public LineEdit( int pColumns) {
        super(pColumns);
    }

    public LineEdit(String pText) {
        super(pText);
    }

    public LineEdit(String pText,  int pColumns) {
        super(pText, pColumns);
    }


    public void setPlaceholder(String s) {
        placeholder = s;
    }
    
    public String getPlaceholder() {
        return placeholder;
    }
    
    @Override
    protected void paintComponent(Graphics pG) {
        super.paintComponent(pG);

        if (placeholder == null || placeholder.length() == 0 || getText().length() > 0) {
            return;
        }

        Graphics2D g = (Graphics2D) pG;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(getDisabledTextColor());
        g.drawString(placeholder, getInsets().left, pG.getFontMetrics().getMaxAscent() + getInsets().top);
    }
}
