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

import java.io.IOException;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Font;

import net.sergetk.mobile.lcdui.BitmapFont;
import net.sergetk.mobile.lcdui.MultiText;

/**
 * An example of formatting and drawing of a long text using a bitmap font.
 * This code is a part of the Mobile Fonts Project (http://sourceforge.net/projects/mobilefonts)
 *
 * @author Sergey Tkachev http://sergetk.net
 */
public class MultiTextTestCanvas extends Canvas {
	private final static String MOSCOW_TEXT = "Moscow\n" +
	"Moscow is the capital and the largest city of the Russian Federation. " +
	"It is also the largest city in Europe.\n" + 
	"Moscow is a major economic centre and is home to the largest number of billionaires in the world; " +
	"in 2008 Moscow was named the world's most expensive city for foreign employees " +
	"for the third year in a row.\n" +
	"The first Russian reference to Moscow dates from 1147 when Yuri Dolgoruki called upon the prince of the Novgorod Republic to \"come to me, brother, to Moscow\".\n" +
	"This text was taken from the Wikipedia.";

	private BitmapFont fontAlaRuss;
	private Font systemFont;

	private MultiText multiText;
	private Image moscowImage, upIcon, downIcon;
	private int yPos = 0;
	private int scrollStep;
	private boolean canScrollUp, canScrollDown;

	public MultiTextTestCanvas() {
		fontAlaRuss = new BitmapFont("/alaruss.fnt");
		systemFont = Font.getDefaultFont(); 
		try {
			moscowImage = Image.createImage("/moscow.png");
			upIcon = Image.createImage("/up.png");
			downIcon = Image.createImage("/down.png");
		} catch (IOException e) { }
		multiText = new MultiText(fontAlaRuss, MOSCOW_TEXT, moscowImage);
		multiText.setWidth(getWidth());
		scrollStep = getHeight() / 3;
		scroll(0);
	}

	/**
	 * Forces to use bitmap font
	 */
	public void useBitmapFont() {
		multiText.font = fontAlaRuss;
		multiText.format();
		repaint();
	}
	
	/**
	 * Forces to use system font
	 */
	public void useSystemFont() {
		multiText.font = systemFont;
		multiText.format();
		repaint();		
	}

	/**
	 * Draws the screen
	 */
	protected void paint(Graphics g) {
		int w = getWidth();
		int h = getHeight();

		// draw the background
		int leftEdge = multiText.leftMargin - 2;
		int rightEdge = w - multiText.rightMargin + 2;
		g.setColor(0xA8A088);
		g.fillRect(0, 0, leftEdge, h);
		g.fillRect(rightEdge, 0, w - rightEdge, h);
		g.setColor(0xE0D8A0);
		g.fillRect(leftEdge, 0, rightEdge - leftEdge, h);

		// draw the text
		multiText.draw(g, 0, - yPos);

		// draw the scroll indicators
		if (canScrollUp && upIcon != null) {
			g.drawImage(upIcon, w - upIcon.getWidth(), 0, Graphics.LEFT | Graphics.TOP);
		}
		if (canScrollDown && downIcon != null) {
			g.drawImage(downIcon, w - downIcon.getWidth(), h - downIcon.getHeight(), Graphics.LEFT | Graphics.TOP);
		}
	}
	
	/**
	 * Process key presses
	 */
	protected void keyPressed(int keyCode) {
		int action = getGameAction(keyCode);
		if (action == UP) {
			scroll(- scrollStep);
		} else if (action == DOWN) {
			scroll(+ scrollStep);
		} else if (action == LEFT) {
			scroll(-1);
		} else if (action == RIGHT) {
			scroll(+1);
		}
	}

	/**
	 * Scroll the screen
	 * @param value scroll value in pixels, scroll down if it is positive
	 */
	private void scroll(int value) {
		canScrollUp = true;
		canScrollDown = true;
		int newYPos = yPos + value;
		int maxYPos = multiText.getHeight() - getHeight();
		if (newYPos <= 0) {
			newYPos = 0;
			canScrollUp = false;
		} else if (newYPos >= maxYPos) {
			newYPos = maxYPos;
			canScrollDown = false;
		}
		if (newYPos != yPos) {
			yPos = newYPos;
			repaint();
		}
	}
}
