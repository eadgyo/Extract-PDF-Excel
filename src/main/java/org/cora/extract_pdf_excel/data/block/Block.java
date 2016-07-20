package org.cora.extract_pdf_excel.data.block;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.pdf.DocumentFont;
import org.cora.extract_pdf_excel.data.lane.Column;
import org.cora.extract_pdf_excel.data.lane.Line;
import org.cora.extract_pdf_excel.data.lane.Rect;

import java.util.Set;

/**
 * Created by eadgyo on 12/07/16.
 * <p/>
 * Holds text and bounds.
 */
public class Block
{
    private String formattedText;
    private String originalText;

    private Line   line;
    private Column column;

    private Rect bound;

    private Set<BaseColor> fontColors;
    private Set<BaseColor> backColors;
    private Set<DocumentFont> fonts;

    private Direction direction;

    public Block(String originalText,
                 Rect bound,
                 Direction direction,
                 Set<BaseColor> fontColors,
                 Set<BaseColor> backColors,
                 Set<DocumentFont> fonts)
    {
        this.originalText = originalText;
        this.formattedText = originalText;

        this.bound = bound;
        this.direction = direction;

        this.fontColors = fontColors;
        this.backColors = backColors;

        this.line = null;
        this.column = null;

        this.direction = null;
        this.fonts = fonts;
    }

    /*
     * Getter and setter
     */
    public String getFormattedText()
    {
        return formattedText;
    }

    public void setFormattedText(String formattedText)
    {
        this.formattedText = formattedText;
    }

    public String getOriginalText()
    {
        return originalText;
    }

    public void setOriginalText(String originalText)
    {
        this.originalText = originalText;
    }

    public Line getLine()
    {
        return line;
    }

    public void setLine(Line line)
    {
        this.line = line;
    }

    public Column getColumn()
    {
        return column;
    }

    public void setColumn(Column column)
    {
        this.column = column;
    }

    public Rect getBound()
    {
        return bound;
    }

    public void setBound(Rect bound)
    {
        this.bound = bound;
    }

    public void setPos(int i, double value) { bound.setPos(i, value); }

    public double getPos(int i) { return bound.getPos(i); }

    public void setLength(int i, double value) { bound.setLength(i, value); }

    public double getLength(int i) { return bound.getLength(i); }

    public Set<DocumentFont> getFonts()
    {
        return fonts;
    }

    public boolean containFonts(DocumentFont font)
    {
        return fonts.contains(font);
    }

    public Set<BaseColor> getFontColor()
    {
        return fontColors;
    }

    public boolean containFontColor(BaseColor fontColor)
    {
        return fontColors.contains(fontColor);
    }

    public void setFontColor(Set<BaseColor> fontColor)
    {
        this.fontColors = fontColor;
    }

    public Set<BaseColor> getBackColors()
    {
        return backColors;
    }

    public void setBackColors(Set<BaseColor> backColors)
    {
        this.backColors = backColors;
    }

    public boolean containBackColor(BaseColor backColor)
    {
        return backColors.contains(backColor);
    }

    public Direction getDirection()
    {
        return direction;
    }

    public void setDirection(Direction direction)
    {
        this.direction = direction;
    }
}
