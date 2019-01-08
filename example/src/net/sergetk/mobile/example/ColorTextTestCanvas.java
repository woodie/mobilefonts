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

import java.util.Calendar;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;

import net.sergetk.mobile.lcdui.BitmapFont;

/**
 * An example of drawing of a color text using a bitmap font.
 * This code is a part of the Mobile Fonts Project (http://sourceforge.net/projects/mobilefonts)
 *
 * @author Sergey Tkachev http://sergetk.net
 */
public class ColorTextTestCanvas extends Canvas {
	private static final int SLEEP_INTERVAL = 250;

	private static final String[] MONTHS = {
		"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"};
	
	private static final String[] DAYS_OF_WEEK = {
		"SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY"};
	
	private BitmapFont smallLcd;
	
	private int color = 0;
	private int h = 0;
	private String dateStr;
	private String dayOfWeekStr;
	private String timeStr;
	private boolean stop = false;

	public ColorTextTestCanvas() {
		smallLcd = new BitmapFont("/lcd_small.fnt", 1);
		initDate();
	}

	/**
	 * Starts the clock update three
	 */
	public void start() {
		new Thread(new Runnable() {
			public void run() {
				while (!stop) {
					color = getRainbowColor(h);
					h+=5;
					initDate();
					repaint();
					try {
						Thread.sleep(SLEEP_INTERVAL);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	/**
	 * Stops the clock update three
	 */
	public void stop() {
		stop = true;
	}

	/**
	 * Paints the screen
	 */
	protected void paint(Graphics g) {
		int w = getWidth();
		int h = getHeight();
		// clean the background
		g.setColor(0x000000);
		g.fillRect(0, 0, w, h);
		// draw date, time and day of week
		g.setColor(color);
		smallLcd.drawString(g,
				dateStr,
				0,
				5,
				Graphics.LEFT | Graphics.TOP);

		smallLcd.drawString(g,
				timeStr,
				w / 2,
				h / 2,
				Graphics.HCENTER | Graphics.VCENTER);

		smallLcd.drawString(g,
				dayOfWeekStr,
				w,
				h - 5,
				Graphics.RIGHT | Graphics.BOTTOM);
	}

	/**
	 * Initializes time and day of week strings
	 */
	private void initDate() {
		Calendar cal = Calendar.getInstance();
		int hour = cal.get(Calendar.HOUR);
		int minute = cal.get(Calendar.MINUTE);
		int second = cal.get(Calendar.SECOND);
		int ampm = cal.get(Calendar.AM_PM);
		boolean hasComma = cal.get(Calendar.MILLISECOND) % 1000 > 500;
		char timeSplit = hasComma ? ':' : ' ';
		
		StringBuffer sb = new StringBuffer();
		if (hour < 10) sb.append('0');
		sb.append(hour);
		sb.append(timeSplit);
		
		if (minute < 10) sb.append('0');
		sb.append(minute);
		sb.append(timeSplit);

		if (second < 10) sb.append('0');
		sb.append(second);
		
		sb.append(ampm == Calendar. AM ? " AM" : " PM");
		timeStr = sb.toString();

		sb = new StringBuffer();
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		sb.append(day);
		sb.append(' ');
		sb.append(MONTHS[month]);
		sb.append(' ');
		sb.append(year);
		dateStr = sb.toString();
		
		dayOfWeekStr = DAYS_OF_WEEK[cal.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY];
	}
	
	/**
	 * Calculates RGB for a given hue value
	 * @param hue hue value
	 * @return RGB color value
	 */
	private int getRainbowColor(int hue) {
		hue = hue % 360;
		int hi = hue / 60;
		int f = hue % 60;
		int v = 255;
		int s = 255;
		
		int p = v * (255 - s) / 255;
		int q = v * (255 * 59 - f * s) / (256 * 60);
		int t = v * (255 * 59 - (59 - f) * s) / (256 * 60);

		int r = 0; int g = 0; int b = 0;
		switch (hi) {
		case 0: r = v; g = t; b = p; break;
		case 1: r = q; g = v; b = p; break;
		case 2: r = p; g = v; b = t; break;
		case 3: r = p; g = q; b = v; break;
		case 4: r = t; g = p; b = v; break;
		case 5: r = v; g = p; b = q; break;
		}
		return (r << 16) | (g << 8) | b;
	}

}
