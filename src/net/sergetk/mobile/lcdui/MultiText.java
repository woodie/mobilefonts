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
package net.sergetk.mobile.lcdui;

import java.util.Vector;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 * <p>Text formatting and drawing utility.<p> 
 * <p>This code is a part of the Mobile Fonts Project (http://sourceforge.net/projects/mobilefonts)</p>
 * 
 * @author Sergey Tkachev <a href="http://sergetk.net">http://sergetk.net</a>
 */
public class MultiText {
	/**
	 * Latin and cyrillic vowels
	 */
	private static final String VOWELS = "aeiouy\u0430\u0435\u0451\u0438\u043E\u0443\u044B\u044D\u044E\u044F";
	/**
	 * Cyrillic hard and soft signs
	 */
	private static final String DONT_TEAR_OFF_AT_END = "\u044A\u044C'";
	/**
	 * Text wrapping mode: wrapping is off
	 */
	public static final int WRAPPING_NONE = 0;
	/**
	 * Text wrapping mode: wrapping by words
	 */
	public static final int WRAPPING_WORDS = 1;
	/**
	 * Text wrapping mode: wrapping by syllables
	 */
	public static final int WRAPPING_SYLLABLES = 2;
	/**
	 * Text left marging
	 */
	public int leftMargin = 8;
	/**
	 * Text right marging
	 */
	public int rightMargin = 8;
	/**
	 * Text top marging
	 */
	public int topMargin = 5;
	/**
	 * Text bottom marging
	 */
	public int bottomMargin = 5;
	/**
	 * Image alignment: can be LEFT, RIGHT or CENTER
	 */
	public int imageAlignment = Graphics.LEFT;
	/**
	 * An horizontal distance between the text and the image
	 */
	public int imageHorizontalMargin = 10;
	/**
	 * A vertical distance between the text and the image
	 */
	public int imageVerticalMargin = 5;
	/**
	 * Distance between paragraphs
	 */
	public int paragraphIndent = 5;
	/**
	 * Text area width
	 */
	public int width;
	/**
	 * Text font must be an instance of one of two classes: Font or BitmapFont
	 */
	public Object font;

	private int textHeight;
	/**
	 * A number of the lines of the text
	 */
	public int linesCount;
	/**
	 * Actual text width: maximum width of the text lines
	 */
	public int actualWidth;
	/**
	 * Image
	 */
	public Image image;
	/**
	 * Text
	 */
	public String text;

	public int cursorPosition;
	public int cursorWidth;
	public int cursorX;
	public int cursorY;

	/**
	 * If text color is default, it will be rendered in globalColor. This method allows to change
	 * text color without re-formatting.
	 */
	public int globalColor;
	/**
	 * Default text color.
	 */
	public int textColor;
	/**
	 * Text alignment mode, can be LEFT, RIGHT or CENTER
	 */
	public int textAlignment = Graphics.LEFT;
	/**
	 * Text wrapping mode
	 */
	public int wrappingMode = WRAPPING_SYLLABLES;

	private int currentTextColor;
	private int currentTextAlignment;
	private int nextTextAlignment;
	private int currentFontHeight;
	private int cursorPartIndex;

	private Object currentFont;
	private int currentWrappingMode;

	public boolean editMode;
	public boolean underlined;

	private Vector textParts = new Vector(); // text parts
	private int imageX, imageY; // image coordinates

	private FontFacade fontFacade;

	/**
	 * Creates a new instance of MultiText object.
	 * @param font text font, must be an instance of Font or BitmapFont
	 * @param text the text
	 * @param image the image, can be null if you don't need it
	 */
	public MultiText(Object font, String text, Image image) {
		this.font = font;
		this.text = text;
		this.image = image;
	}

	/**
	 * Sets the width and formats the text.
	 * @param width the width in pixels
	 */
	public void setWidth(int width) {
		if (width != this.width) {
			this.width = width;
			format();
		}
	}

	/**
	 * Gets the height of the formatted text. You must call this method only
	 * after text had been formatted (by calling format() of setWidth()).
	 * @return height in pixels
	 */
	public int getHeight() {
		if(image == null || (textHeight > image.getHeight()) )
			return textHeight;
		else
			return image.getHeight();
	}

	protected TextPart getTextPart(int index) {
		return (TextPart)this.textParts.elementAt(index);
	}

	/**
	 * Looks for splitting point for the current line.
	 * @param part
	 * @param start start index
	 * @param x start x position
	 * @param maxX maximum x value
	 * @return split position index
	 */
	protected int splitBySyllables(TextPart part, int start, int x, int maxX) {
		boolean hasVowel = false;
		boolean eol = false;
		boolean overrun = false;
		boolean isVowel = false;

		int hyphenWidth = fontFacade.charWidth('-');
		maxX -= hyphenWidth;

		int lastValidSplit = -1;
		int lastValidX = 0;

		int splitCandidate = -1;
		int splitCandidateX = 0; 

		int consIndex2 = -1;
		int consX2 = 0;
		int consCount = 0;

		int p = start;

		char c = text.charAt(p);

		for (;;) {
			if (p < text.length()) {
				c = text.charAt(p);
				if (c == ' ') {
					eol = true;
				} else {
					isVowel = VOWELS.indexOf(toLowerCase(c)) != -1;
				}
			} else {
				c = 0;
				eol = true;
			}

			if (isVowel) {
				if (hasVowel) {
					if (consCount > 1) { // closed syllable
						splitCandidate = consIndex2;
						splitCandidateX = consX2;
					} else
						if (consCount == 1) { // opened syllable
							splitCandidate = consIndex2;
							splitCandidateX = consX2;
						} else { // гласная
							splitCandidate = p;
							splitCandidateX = x;
						}

					if (splitCandidateX <= maxX) {
						lastValidSplit = splitCandidate;
						lastValidX = splitCandidateX;
					} else {
						overrun = true;
					}
				}
				hasVowel = true;
				consIndex2 = -1;
				consCount = 0;
			} else {
				if (hasVowel) {
					c = toLowerCase(c);
					if (DONT_TEAR_OFF_AT_END.indexOf(toLowerCase(c)) < 0) {
						consIndex2 = p;
						consX2 = x;
					}
					consCount++;
				}
			}

			if (eol) {
				if (lastValidSplit == start + 1)
					return -1;

				if (lastValidSplit != -1) {
					part.hasHyphen = true;
					part.width = lastValidX - part.x + hyphenWidth;
					part.end = lastValidSplit;
				}
				return lastValidSplit;
			}

			if (!overrun)
				x += fontFacade.charWidth(c);
			overrun = x > maxX;

			p++;
		}
	}

	/**
	 * Formats the text. Call this method if you changed any parameters such as text, alignment or margins.
	 * Note that setWidth() calls this method itself.
	 */
	public void format() {
		this.cursorPartIndex = -1;
		this.textHeight = 0;
		this.textParts.removeAllElements();
		boolean cursorFound = false;

		// if it is no text or text has zero width...
		if ((text == null || text.length() == 0) && this.image == null) {
			cursorX = 0;
			cursorY = 0;
			return;
		}

		currentWrappingMode = wrappingMode;
		currentTextColor = textColor;
		nextTextAlignment = currentTextAlignment = this.textAlignment;
		currentFont = font;
		if (fontFacade == null) {
			fontFacade = new FontFacade(font);
		} else {
			fontFacade.setFont(font);
		}
		currentFontHeight = fontFacade.getFontHeight();

		linesCount = 0;

		int y = this.topMargin;					// y position of current character		
		int maxX = this.width - this.rightMargin;
		int minX = this.leftMargin;

		int imageWidth = 0;
		int imageHeight = 0;

		if (this.image != null) {			
			imageHeight = this.image.getHeight();
			imageWidth  = this.image.getWidth();
			this.imageX = this.leftMargin;
			this.imageY = this.topMargin;

			switch (this.imageAlignment) {
			case Graphics.LEFT:
				minX += imageWidth + this.imageHorizontalMargin;
				maxX = this.width - rightMargin;
				break;
			case Graphics.HCENTER:
				this.imageX += (maxX - minX - imageWidth) / 2;
				y += imageHeight;
				break;
			case Graphics.RIGHT:
				maxX -= imageWidth + this.imageHorizontalMargin;
				this.imageX = this.width - this.rightMargin - imageWidth;
				break;
			}
		}


		int textLength = 0;
		if (text == null || text.length() == 0) {
			this.textHeight = y + bottomMargin;
			return;
		}

		textLength= text.length();

		int x = minX;
		int lineHeight = 0;

		TextPart currentPart = null;

		boolean isWord = false;
		boolean isSpace = false;
		boolean newLine = false;
		boolean partEnds = false;

		int p = 0;				// index of the current character 
		int spaceStart = -1;
		int wordStart = -1;
		int wordX = 0;
		int spaceX = 0;

		int firstPartAtLine = -1;
		int lastPartAtLine = -1;
		int localCursorX = -1;

		while (p < textLength) {
			boolean isCursor = (!cursorFound && p == cursorPosition);
			char c = text.charAt(p);

			/* tag parsing is switched off in the current version
			if (c == '<') {
				if (currentPart != null) {
					currentPart.end = p;
					currentPart.width = x - currentPart.x;
					partEnds = true;
				}

				//p = parseTag(p + 1) - 1;
			} else 
			 */
			{
				if (c == '\n' || c == '\r') { // new line
					if (currentPart != null) {
						currentPart.end = p;
						currentPart.width = x - currentPart.x;
						partEnds = true;
					}
					newLine = true;
					y += paragraphIndent;
				} else {
					boolean charIsSpace = c == ' ';

					// new text part if:
					// there are no any text parts
					// or current character isn't space or part isnt first at line
					if (currentPart == null && (editMode || !charIsSpace || firstPartAtLine != -1)) {
						currentPart = new TextPart();
						currentPart.start = p;
						currentPart.x = x;
						currentPart.y = y;
						currentPart.font = this.currentFont;
						currentPart.height = currentFontHeight;
						currentPart.color = this.currentTextColor;
						lineHeight = Math.max(lineHeight, currentPart.height);

						wordStart = spaceStart = -1;
						isWord = isSpace = false;
					}

					// if current part is started, process the character
					if (currentPart != null) {
						if (charIsSpace) {
							if (!isSpace) {
								isSpace = true;
								isWord = false;
								spaceStart = p;
								spaceX = x;
							}
						} else {
							if (!isWord) {
								isSpace = false;
								isWord = true;
								wordStart = p;
								wordX = x;
							}
						}

						int charWidth = fontFacade.charWidth(c);
						if (isCursor) {
							localCursorX = x;
							charWidth += cursorWidth;
						}
						if (x + charWidth > maxX) {
							newLine = true;
							int split = -1;

							switch (this.currentWrappingMode) {
							case WRAPPING_NONE: {
								split = p;
								currentPart.width = x - currentPart.x;							
							} break;

							case WRAPPING_WORDS: {
								if(editMode)
								{
									if(spaceStart != -1 )
									{
										if(wordStart != -1 && wordStart>spaceStart)
											split = wordStart;
										else 
											split = p;
									}
									else
									{
										split = p;
									}
								}
								else
								{
									if (spaceStart != -1) { // split by spaces
										split = spaceStart;
										currentPart.width = spaceX - currentPart.x;
									}
									else {
										if (firstPartAtLine != -1) {
											p = currentPart.start - 1;
											currentPart = null;
											newLine = true;
										} else {
											split = p;
											currentPart.width = x - currentPart.x;									
										}
									}
								}
							} break;

							case WRAPPING_SYLLABLES: {
								// trying to split by syllables
								if (wordStart != -1)
									if (isSpace) {
										split = spaceStart;
										currentPart.width = spaceX - currentPart.x;
									} else {
										split = splitBySyllables(currentPart, wordStart, wordX, maxX);
									}

								if (split == -1) { // can't split
									if (spaceStart != -1) { // split by spaces
										split = spaceStart;
										currentPart.width = spaceX - currentPart.x;
									} else { // can't fit, split by words
										if (firstPartAtLine != -1) {
											p = currentPart.start - 1;
											currentPart = null;
											newLine = true;
										} else {
											split = p;
											currentPart.width = x - currentPart.x;									
										}
									}
								}							
							} break;
							}

							if (split != -1) {
								currentPart.end = split;
								partEnds = true;
								p = split - 1;
							}
						} else {
							if (p == textLength-1) {
								newLine = true;
								currentPart.end = textLength;
								currentPart.width = x - currentPart.x + charWidth;
								partEnds = true;
							}
						}

						if (currentPart != null) {
							x += charWidth;
						}
					}
				}
			}
			if (partEnds) {
				if (currentPart != null) {
					if (!cursorFound
							&& cursorPosition >= currentPart.start 
							&& cursorPosition <= currentPart.end)
					{
						cursorX = currentPart.x;
						if (localCursorX < 0) {
							cursorX += currentPart.width;
						} else {
							cursorX += localCursorX;
						}
						cursorY = currentPart.y;
						cursorPartIndex = textParts.size();

						cursorFound = true;
					}
					textParts.addElement(currentPart);
					if (firstPartAtLine == -1)
						firstPartAtLine = textParts.size()-1;
					if (lastPartAtLine == -1)
						lastPartAtLine = textParts.size()-1;
					currentPart = null;
				}
				partEnds = false;
			}

			if (newLine) {
				int lineWidth = 0;
				int widthShortage = 0;

				if (firstPartAtLine >= 0 && lastPartAtLine >=0) {
					TextPart firstPart = getTextPart(firstPartAtLine);
					TextPart lastPart = getTextPart(lastPartAtLine);
					lineWidth = lastPart.x + lastPart.width - firstPart.x;
					actualWidth = Math.max(actualWidth, lineWidth);
					widthShortage = (maxX - minX) - lineWidth;
				}

				if (firstPartAtLine != -1 && currentTextAlignment != Graphics.LEFT) {

					if (widthShortage > 0) { // need to align
						if (currentTextAlignment == Graphics.RIGHT || currentTextAlignment == Graphics.HCENTER) {
							int dx = currentTextAlignment == Graphics.RIGHT ? widthShortage : (widthShortage) / 2;
							for (int i = firstPartAtLine; i <= lastPartAtLine; i++) {
								getTextPart(i).x += dx;
							}
						}
					}
				}

				firstPartAtLine = lastPartAtLine = -1;

				// go to new line
				y += lineHeight;
				linesCount++;
				if (y >= this.imageY + imageHeight + this.imageVerticalMargin) {
					minX = this.leftMargin;
					maxX = this.width - this.rightMargin;
				}
				x = minX;

				newLine = false;
				lineHeight = 0;
				this.currentTextAlignment = this.nextTextAlignment;
			} // newLine

			p++;
		}

		if (!cursorFound) {
			cursorX = x;
			cursorY = y;
			cursorPartIndex = textParts.size();
		}

		this.textHeight = y + bottomMargin;
	}

	/**
	 * Draws the text.
	 * @param g the graphics context
	 * @param x the x position
	 * @param y the y position
	 */
	public void draw(Graphics g, int x, int y) {
		draw(g, x, y, 0, textParts.size());
	}

	/**
	 * Draws the text in the specified range of lines.
	 * @param g the graphics context
	 * @param x the x position
	 * @param y the y position
	 * @param start the start line to be drawn
	 * @param end the end line to be drawn
	 */
	public void draw(Graphics g, int x, int y, int start, int end) {
		try {
			int clipLeft = g.getClipX();
			int clipTop = g.getClipY();
			int clipRight = clipLeft + g.getClipWidth();
			int clipBottom = clipTop + g.getClipHeight();

			if (image != null) {
				int xPos = x + this.imageX;
				int yPos = y + this.imageY;

				if (xPos + image.getWidth() >= clipLeft
						&& xPos < clipRight
						&& yPos + image.getHeight() >= clipTop
						&& yPos < clipBottom)
				{
					g.drawImage(image, xPos, yPos, Graphics.LEFT | Graphics.TOP);
				}
			}

			int partsCount = textParts.size();
			if (start < 0 || start >= partsCount || end <= start) {
				return;
			}
			if (end > partsCount) {
				end = partsCount;
			}
			if (fontFacade == null) {
				fontFacade = new FontFacade(font);
			}
			for (int i = start; i < end; i++) {
				TextPart part = getTextPart(i);
				fontFacade.setFont(part.font);

				int xPos = x + part.x;
				int yPos = y + part.y;

				if (xPos + part.width >= clipLeft
						&& xPos < clipRight
						&& yPos + part.height >= clipTop
						&& yPos < clipBottom)
				{
					g.setColor(part.color == textColor ? globalColor : part.color);
					int hyphenX = 0;
					if (editMode && cursorWidth > 0 && i == cursorPartIndex) {
						int xx = fontFacade.drawSubstring(g, text, part.start, cursorPosition - part.start, xPos, yPos, Graphics.TOP|Graphics.LEFT) + cursorWidth;
						hyphenX = fontFacade.drawSubstring(g, text, cursorPosition, part.end - cursorPosition, xx, yPos, Graphics.TOP|Graphics.LEFT);
					} else {
						hyphenX = fontFacade.drawSubstring(g, text, part.start, part.end - part.start, xPos, yPos, Graphics.TOP|Graphics.LEFT);
					}

					if (part.hasHyphen) {
						fontFacade.drawChar(g, '-', hyphenX, yPos, Graphics.TOP|Graphics.LEFT);
					}

//					g.setColor(0xFF0000);
//					g.drawRect(xPos, yPos, part.width - 1, part.height - 1);
				}
			}
		} catch (Exception e) {
		}
	}

	/**
	 * Converts a character to lower case. Many mobile devices have no full implementation
	 * of toLowerCase() method. They aren't support national alphabets. Following method
	 * can convert cyrillic strings to lower case. You can extend it to another alphabets.
	 * @param c source character
	 * @return character in lower case
	 */
	public static char toLowerCase(char c) {
		if ((c >= '\u0430' && c <= '\u044F') || c == '\u0451') {
			return c;
		} else if (c >= '\u0410' && c <= '\u042F') {
			return (char)(c + ('\u0430' - '\u0410'));
		} else if (c == '\u0401') {
			return '\u0451';
		} else {
			return Character.toLowerCase(c);	
		}
	}

	/**
	 * Converts a string to lower case. Many mobile devices have no full implementation
	 * of toLowerCase() method. They aren't support national alphabets. Following method
	 * can convert cyrillic strings to lower case. You can extend it to another alphabets.
	 * @param s the source string
	 * @return the string in lower case
	 */
	public static String toLowerCase(String s) {
		int count = s.length();
		char[] result = new char[count];
		for (int i = 0; i < count; i++) {
			result[i] = toLowerCase(s.charAt(i));
		}
		return new String(result);
	}

	/**
	 * Atomic part of the text.
	 * @author Sergey Tkachev <a href="http://sergetk.net">http://sergetk.net</a>
	 */
	public class TextPart {
		Object font;
		int color;
		int x;
		int y;
		int width;
		int height;
		int start, end;
		boolean hasHyphen = false;
	}
}