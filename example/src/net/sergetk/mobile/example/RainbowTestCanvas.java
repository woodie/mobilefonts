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
import net.sergetk.mobile.lcdui.FontFacade;

/**
 * An example of many colors drawing.
 * This code is a part of the Mobile Fonts Project (http://sourceforge.net/projects/mobilefonts)
 *
 * @author Sergey Tkachev http://sergetk.net
 */
public class RainbowTestCanvas extends Canvas {
	private static final int TEXT_OUTLINE_COLOR = 0x000000;

	private static final int BACKGROUND_COLOR = 0xFFFFFF;

	private final static int COLORS_COUNT = 7;
	
	private final static String[][] COLOR_MNEMONICS = {
		{"Richard", "Of", "York", "Gave", "Battle", "In", "Vain"},
		{"\u041a\u0430\u0436\u0434\u044b\u0439",
		 "\u041e\u0445\u043e\u0442\u043d\u0438\u043a",
		 "\u0416\u0435\u043b\u0430\u0435\u0442",
		 "\u0417\u043d\u0430\u0442\u044c",
		 "\u0413\u0434\u0435",
		 "\u0421\u0438\u0434\u0438\u0442",
		 "\u0424\u0430\u0437\u0430\u043d"}
	};
	
	private final static int[] COLOR_VALUES = {
		0xFF0000, 0xFF8000, 0xFFFF00, 0x00FF00,
		0x00B0FF, 0x0000FF, 0x8000FF
	};

	public static final int ENGLISH = 0;
	public static final int RUSSIAN = 1;

	private FontFacade fontFacade;
	private int language = ENGLISH;

	public RainbowTestCanvas() {
		BitmapFont font = new BitmapFont("/serif.fnt", COLORS_COUNT).getFont(Font.STYLE_BOLD);
		fontFacade = new FontFacade(font);
	}

	/**
	 * Draws the screen
	 */
	protected void paint(Graphics g) {
		int w = getWidth();
		int h = getHeight();
		g.setColor(BACKGROUND_COLOR);
		g.fillRect(0, 0, w, h);

		int fh = fontFacade.getFontHeight();
		int x = w / 2;
		int y = (h - COLORS_COUNT * fh) / 2;

		for (int i = 0; i < COLORS_COUNT; i++) {
			g.setColor(COLOR_VALUES[i]);
			fontFacade.drawOutlinedString(g,
					TEXT_OUTLINE_COLOR,
					COLOR_MNEMONICS[language][i],
					x, y, Graphics.HCENTER | Graphics.TOP);
			y += fh;
		}
	}

	public void setLanguage(int language) {
		this.language = language;
		repaint();
	}
}
