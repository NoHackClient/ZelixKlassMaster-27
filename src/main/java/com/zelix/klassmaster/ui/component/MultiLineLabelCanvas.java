package com.zelix.klassmaster.ui.component;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.StringTokenizer;
import java.util.Vector;

public class MultiLineLabelCanvas extends Canvas {
    private static final String WORD_DELIMITERS = " \n\r";
    public static int lineSpacing = 1;
    public boolean linesWrapped;
    public Vector lines;
    public String text = "Zelix KlassMaster could not find the class java.lang.Object. Please select the archive file that contains the Java bootstrap classes that you wish Zelix KlassMaster to use.";
    public int wrapWidth = 250;
    public FontMetrics fontMetrics;
    public int ascent;
    public int descent;

    @Override
    public void invalidate() {
        super.invalidate();
        this.linesWrapped = false;
    }

    @Override
    public synchronized void setBounds(int ba, int bb, int bc, int bd) {
        super.setBounds(ba, bb, bc, bd);
        this.wrapWidth = this.getSize().width;
    }

    public int getTextHeight() {
        if (!this.linesWrapped) {
            this.wrapLines();
        }

        int ba;
        if (this.lines == null) {
            ba = 0;
        } else {
            ba = this.lines.size() * (this.ascent + this.descent + lineSpacing);
        }

        return ba;
    }

    @Override
    public Dimension getPreferredSize() {
        return this.getMinimumSize();
    }

    @Override
    public void update(Graphics graphics) {
        this.paint(graphics);
    }

    public void wrapLines() {
        this.lines = new Vector();
        StringTokenizer stringTokenizer = new StringTokenizer(this.text, WORD_DELIMITERS, true);
        StringBuffer stringBuffer = new StringBuffer();
        int ba = 0;
        int bb = stringTokenizer.countTokens();

        for (int i = 0; i < bb; i++) {
            label31:
            {
                String string = stringTokenizer.nextToken();
                Vector vector;
                if (!string.equals("\n")) {
                    if (!string.equals("\r")) {
                        int bd = this.fontMetrics.stringWidth(string);
                        ba += bd;
                        if (ba <= this.wrapWidth) {
                            stringBuffer.append(string);
                        } else {
                            this.lines.addElement(stringBuffer.toString());
                            stringBuffer = new StringBuffer();
                            if (!string.equals(" ")) {
                                stringBuffer.append(string);
                                ba = bd;
                            } else {
                                ba = 0;
                            }
                        }
                        break label31;
                    }

                    vector = this.lines;
                } else {
                    vector = this.lines;
                }

                vector.addElement(stringBuffer.toString());
                stringBuffer = new StringBuffer();
                ba = 0;
            }

            if (i == bb - 1) {
                this.lines.addElement(stringBuffer.toString());
                this.linesWrapped = true;
            } else {
                this.linesWrapped = true;
            }
        }
    }

    @Override
    public void validate() {
        super.validate();
        this.repaint();
    }

    public MultiLineLabelCanvas(Font font) {
        this.setFont(font);
        this.fontMetrics = this.getFontMetrics(this.getFont());
        this.ascent = this.fontMetrics.getAscent();
        this.descent = this.fontMetrics.getDescent();
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(this.wrapWidth, this.getTextHeight());
    }

    @Override
    public void paint(Graphics graphics) {
        if (!this.linesWrapped) {
            this.wrapLines();
        }

        Dimension dimension = this.getSize();
        int bc = dimension.height;
        int bd = dimension.width;
        graphics.clearRect(0, 0, bd, bc);
        int ascent = this.ascent;
        int bb = 0;
        int bf = 0;

        for (Vector vector = this.lines; bf < vector.size(); vector = this.lines) {
            String string = (String) this.lines.elementAt(bb);
            int be = ascent;
            graphics.drawString(string, 0, be);
            ascent += this.ascent + this.descent + lineSpacing;
            bf = ++bb;
        }
    }
}
