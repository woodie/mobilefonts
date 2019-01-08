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

import javax.microedition.lcdui.*;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

/**
 * A MIDlet demonstrating usage of bitmap fonts.
 * This code is a part of the Mobile Fonts Project (http://sourceforge.net/projects/mobilefonts)
 *
 * @author Sergey Tkachev http://sergetk.net
 */
public class BitmapFontTestMidlet extends MIDlet implements CommandListener {
	private Command backCommand = new Command("Back", Command.BACK, 1);
	private Command systemFontCommand = new Command("System Font", Command.ITEM, 1);
	private Command bitmapFontCommand = new Command("Bitmap Font", Command.ITEM, 1);
	private Command englishCommand = new Command("English", Command.ITEM, 1);
	private Command russianCommand = new Command("Russian", Command.ITEM, 1);

	Display display;
	private List menuList;
	private ColorTextTestCanvas colorTextCanvas;
	private MultiTextTestCanvas multiTextCanvas;
	private StylesTestCanvas stylesTextCanvas;
	private RainbowTestCanvas rainbowCanvas;

	public BitmapFontTestMidlet() {
	}

	protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
	}

	protected void pauseApp() {
	}

	protected void startApp() throws MIDletStateChangeException {
		if (display == null) {
			display = Display.getDisplay(this);
		}
		showMenu();
	}

	public void commandAction(Command command, Displayable screen) {
		if (screen == menuList) {
			if (command == List.SELECT_COMMAND) {
				switch (menuList.getSelectedIndex()) {
				case 0: showColorText(); break;
				case 1: showMultiText(); break;
				case 2: showStyles(); break;
				case 3: showRainbow(); break;
				case 4:	notifyDestroyed(); break;
				}
			}
		} else {
			if (command == backCommand) {
				if (screen == colorTextCanvas) {
					colorTextCanvas.stop();
				}
				showMenu();
			} else if (command == systemFontCommand) {
				multiTextCanvas.useSystemFont();
			} else if (command == bitmapFontCommand) {
				multiTextCanvas.useBitmapFont();
			} else if (command == englishCommand) {
				rainbowCanvas.setLanguage(RainbowTestCanvas.ENGLISH);
			} else if (command == russianCommand) {
				rainbowCanvas.setLanguage(RainbowTestCanvas.RUSSIAN);
			}
		}
	}

	private void showMenu() {
		if (menuList == null) {
			menuList = new List("Bitmap Font Test", Choice.IMPLICIT);
			menuList.append("Color Text", null);
			menuList.append("Formatted Text", null);
			menuList.append("Font Styles", null);
			menuList.append("Rainbow", null);
			menuList.append("Exit", null);
			menuList.setCommandListener(this);
		}
		display.setCurrent(menuList);
	}

	private void showColorText() {
		colorTextCanvas = new ColorTextTestCanvas();
		showTestCanvas(colorTextCanvas);
		colorTextCanvas.start();
	}

	private void showMultiText() {
		multiTextCanvas = new MultiTextTestCanvas();
		multiTextCanvas.addCommand(systemFontCommand);
		multiTextCanvas.addCommand(bitmapFontCommand);
		showTestCanvas(multiTextCanvas);
	}

	private void showStyles() {
		stylesTextCanvas = new StylesTestCanvas();
		showTestCanvas(stylesTextCanvas);
	}

	private void showRainbow() {
		rainbowCanvas = new RainbowTestCanvas();
		rainbowCanvas.addCommand(englishCommand);
		rainbowCanvas.addCommand(russianCommand);
		showTestCanvas(rainbowCanvas);
	}

	private void showTestCanvas(Canvas canvas) {
		canvas.addCommand(backCommand);
		canvas.setCommandListener(this);
		display.setCurrent(canvas);		
	}
}
