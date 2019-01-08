/*
 * Copyright (c) 2005-2009 Sergey Tkachev http://sergetk.net
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
 * OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF
 * OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package net.sergetk.mobile.example;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import net.sergetk.mobile.lcdui.BitmapFont;

/**
 * An example of the various font styles.
 * This code is a part of the Mobile Fonts Project (http://sourceforge.net/projects/mobilefonts)
 *
 * @author Sergey Tkachev http://sergetk.net
 */
public class StylesTestCanvas extends Canvas {
	private BitmapFont fontNormal;
	private BitmapFont fontBold;
	private BitmapFont fontItalic;
	private BitmapFont fontBoldItalic;
	private BitmapFont fontUnderlined;

	public StylesTestCanvas() {
		fontNormal = new BitmapFont("/serif.fnt");
		fontBold = fontNormal.getFont(Font.STYLE_BOLD);
		fontItalic = fontNormal.getFont(Font.STYLE_ITALIC);
		fontBoldItalic = fontNormal.getFont(Font.STYLE_BOLD | Font.STYLE_ITALIC);
		fontUnderlined = fontNormal.getFont(Font.STYLE_UNDERLINED);
	}

	/**
	 * Draws the screen
	 */
	protected void paint(Graphics g) {
		int w = getWidth();
		int h = getHeight();
		g.setColor(0xFFFFFF);
		g.fillRect(0, 0, w, h);
		g.setColor(0x000000);

		int x = 5;
		int y = 5;
		int anchors = Graphics.LEFT | Graphics.TOP;
		fontNormal.drawString(g, "Normal example", x, y, anchors);
		y += fontNormal.getHeight();
		fontBold.drawString(g, "Bold example", x, y, anchors);
		y += fontBold.getHeight();
		fontItalic.drawString(g, "Italic example", x, y, anchors);
		y += fontItalic.getHeight();
		fontBoldItalic.drawString(g, "Bold italic example", x, y, anchors);
		y += fontBoldItalic.getHeight();
		fontUnderlined.drawString(g, "Underlined example", x, y, anchors);
	}
}
