package d4rk.mc.gui;

import org.lwjgl.opengl.GL11;

import net.minecraft.src.ChatAllowedCharacters;
import net.minecraft.src.FontRenderer;
import net.minecraft.src.Gui;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.GuiTextField;
import net.minecraft.src.Tessellator;

public class TextField extends BasicGui {
	protected GuiTextField textField;
	
    protected int xPos;
    protected int yPos;

    protected int width;
    protected int height;
    
    protected int color = 14737632;

	public TextField(FontRenderer fontRenderer, int xPos, int yPos, int width, int height) {
		super(fontRenderer);
		this.textField = new GuiTextField(fontRenderer, xPos, yPos, width, height);
		this.xPos = xPos;
		this.yPos = yPos;
		this.width = width;
		this.height = height;
	}
	
	private void updateTextFieldPosAndWidth() {
		String text = getText();
		boolean isFocused = isFocused();
		int curPos = getCursorPosition();
		boolean enableBack = getEnableBackgroundDrawing();
		int maxStringLen = getMaxStringLength();
		boolean isVisible = getVisible();
		textField = new GuiTextField(fontRenderer, xPos, yPos, width, height);
		setText(text);
		setTextColor(color);
		setCursorPosition(curPos);
		setFocused(isFocused);
		setEnableBackgroundDrawing(enableBack);
		setMaxStringLength(maxStringLen);
		setVisible(isVisible);
	}

	public GuiTextField getTextField() {
		return textField;
	}

	public int getPosX() {
		return xPos;
	}

	public int getPosY() {
		return yPos;
	}

	public int getHeight() {
		return height;
	}

	public void setPosX(int xPos) {
		this.xPos = xPos;
		this.updateTextFieldPosAndWidth();
	}

	public void setPosY(int yPos) {
		this.yPos = yPos;
		this.updateTextFieldPosAndWidth();
	}

	public void setWidth(int width) {
		this.width = width;
		this.updateTextFieldPosAndWidth();
	}

	public void setHeight(int height) {
		this.height = height;
		this.updateTextFieldPosAndWidth();
	}

	/**
	 * Increments the cursor counter
	 */
	public void updateCursorCounter() {
		textField.updateCursorCounter();
	}

	/**
	 * Sets the text of the textbox.
	 */
	public void setText(String par1Str) {
		textField.setText(par1Str);
	}

	/**
	 * Returns the text beign edited on the textbox.
	 */
	public String getText() {
		return textField.getText();
	}

	/**
	 * @return returns the text between the cursor and selectionEnd
	 */
	public String getSelectedtext() {
		return textField.getSelectedtext();
	}

	/**
	 * replaces selected text, or inserts text at the position on the cursor
	 */
	public void writeText(String par1Str) {
		textField.writeText(par1Str);
	}

	/**
	 * Deletes the specified number of words starting at the cursor position.
	 * Negative numbers will delete words left of the cursor.
	 */
	public void deleteWords(int par1) {
		textField.deleteWords(par1);
	}

	/**
	 * delete the selected text, otherwsie deletes characters from either side
	 * of the cursor. params: delete num
	 */
	public void deleteFromCursor(int par1) {
		textField.deleteFromCursor(par1);
	}

	/**
	 * see @getNthNextWordFromPos() params: N, position
	 */
	public int getNthWordFromCursor(int par1) {
		return textField.getNthWordFromCursor(par1);
	}

	/**
	 * gets the position of the nth word. N may be negative, then it looks
	 * backwards. params: N, position
	 */
	public int getNthWordFromPos(int par1, int par2) {
		return textField.getNthWordFromPos(par1, par2);
	}

	/**
	 * Moves the text cursor by a specified number of characters and clears the
	 * selection
	 */
	public void moveCursorBy(int par1) {
		textField.moveCursorBy(par1);
	}

	/**
	 * sets the position of the cursor to the provided index
	 */
	public void setCursorPosition(int par1) {
		textField.setCursorPosition(par1);
	}

	/**
	 * sets the cursors position to the beginning
	 */
	public void setCursorPositionZero() {
		textField.setCursorPositionZero();
	}

	/**
	 * sets the cursors position to after the text
	 */
	public void setCursorPositionEnd() {
		textField.setCursorPositionEnd();
	}

	/**
	 * Call this method from you GuiScreen to process the keys into textbox.
	 */
	public boolean textboxKeyTyped(char par1, int par2) {
		return textField.textboxKeyTyped(par1, par2);
	}

	/**
	 * Args: x, y, buttonClicked
	 */
	public void mouseClicked(int par1, int par2, int par3) {
		textField.mouseClicked(par1, par2, par3);
	}

	/**
	 * Draws the textbox
	 */
	@Override
	public void draw() {
		textField.drawTextBox();
	}

	public void setMaxStringLength(int par1) {
		textField.setMaxStringLength(par1);
	}

	/**
	 * returns the maximum number of character that can be contained in this
	 * textbox
	 */
	public int getMaxStringLength() {
		return textField.getMaxStringLength();
	}

	/**
	 * returns the current position of the cursor
	 */
	public int getCursorPosition() {
		return textField.getCursorPosition();
	}

	/**
	 * get enable drawing background and outline
	 */
	public boolean getEnableBackgroundDrawing() {
		return textField.getEnableBackgroundDrawing();
	}

	/**
	 * enable drawing background and outline
	 */
	public void setEnableBackgroundDrawing(boolean par1) {
		textField.setEnableBackgroundDrawing(par1);
	}

	/**
	 * Sets the text colour for this textbox (disabled text will not use this
	 * colour)
	 */
	public void setTextColor(int par1) {
		textField.setTextColor(par1);
		color = par1;
	}

	/**
	 * setter for the focused field
	 */
	public void setFocused(boolean par1) {
		textField.setFocused(par1);
	}

	/**
	 * getter for the focused field
	 */
	public boolean isFocused() {
		return textField.isFocused();
	}

	public void setEnabeled(boolean par1) {
		textField.func_82265_c(par1);
	}

	/**
	 * the side of the selection that is not the cursor, maye be the same as the
	 * cursor
	 */
	public int getSelectionEnd() {
		return textField.getSelectionEnd();
	}

	/**
	 * returns the width of the textbox depending on if the the box is enabled
	 */
	public int getWidth() {
		return textField.getWidth();
	}

	/**
	 * Sets the position of the selection anchor (i.e. position the selection
	 * was started at)
	 */
	public void setSelectionPos(int par1) {
		textField.setSelectionPos(par1);
	}

	/**
	 * if true the textbox can lose focus by clicking elsewhere on the screen
	 */
	public void setCanLoseFocus(boolean par1) {
		textField.setCanLoseFocus(par1);
	}

	/**
	 * @return {@code true} if this textbox is visible
	 */
	public boolean getVisible() {
		return textField.getVisible();
	}

	/**
	 * Sets whether or not this textbox is visible
	 */
	public void setVisible(boolean par1) {
		textField.setVisible(par1);
	}
}
