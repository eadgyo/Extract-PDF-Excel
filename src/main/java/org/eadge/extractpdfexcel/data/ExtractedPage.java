package org.eadge.extractpdfexcel.data;

import org.eadge.extractpdfexcel.data.block.Block;
import org.eadge.extractpdfexcel.data.block.Direction;
import org.eadge.extractpdfexcel.data.geom.Rectangle2;
import org.eadge.extractpdfexcel.tools.DefaultBlockMerger;

import java.util.*;

/**
 * Created by eadgyo on 16/07/16.
 * <p/>
 * Holds blocks in page and pdf length
 */
public class ExtractedPage
{
    /**
     * Length of page
     */
    private float width;
    private float height;

    /**
     * Data in page
     */
    private List<Block> blocks;

    public ExtractedPage(float width, float height, List<Block> blocks)
    {
        this.width = width;
        this.height = height;

        this.blocks = blocks;
    }

    public ExtractedPage(float width, float height)
    {
        this.width = width;
        this.height = height;

        this.blocks = new ArrayList<Block>();
    }

    public float getWidth()
    {
        return width;
    }

    public void setWidth(float width)
    {
        this.width = width;
    }

    public float getHeight()
    {
        return height;
    }

    public void setHeight(float height)
    {
        this.height = height;
    }

    public Collection<Block> getBlocks()
    {
        return blocks;
    }

    public void setBlocks(ArrayList<Block> blocks)
    {
        this.blocks = blocks;
    }

    public int numberOfBlocks()
    {
        return blocks.size();
    }

    /**
     * Add block in page
     *
     * @param block added block
     */
    public void addBlock(Block block)
    {
        blocks.add(block);
    }

    public void addAllBlocks(Collection<Block> blocks)
    {
        this.blocks.addAll(blocks);
    }

    public void cleanDuplicatedBlocks()
    {
        Map<String, ArrayList<Block>> blocksMap = new HashMap<>();

        // Start adding each block using contained text as key
        for (Iterator<Block> iterator = blocks.iterator(); iterator.hasNext(); )
        {
            Block block = iterator.next();

            // If the block is not an empty block
            if (block.getOriginalText().equals(""))
            {
                iterator.remove();
            }
            else
            {
                String           key    = block.getOriginalText();
                ArrayList<Block> blocks = blocksMap.get(key);

                if (blocks == null)
                {
                    blocks = new ArrayList<>();
                    blocksMap.put(key, blocks);
                }

                blocks.add(block);
            }
        }

        // Compare and remove duplicated blocks
        for (Iterator<Block> iterator = blocks.iterator(); iterator.hasNext(); )
        {
            Block block = iterator.next();

            String key = block.getOriginalText();
            ArrayList<Block> blocks = blocksMap.get(key);

            // Try to find a duplicated block with the same key and same position
            for (Block comparedBlock : blocks)
            {
                if (comparedBlock != block &&
                        block.getPos(0) == comparedBlock.getPos(0) &&
                        block.getPos(1) == comparedBlock.getPos(1))
                {
                    // The block is duplicated
                    // Remove it from the map and the source collection
                    blocks.remove(block);
                    iterator.remove();
                    break;
                }
            }
        }
    }

    public void mergeNearBlocks(double mergeFactor)
    {
        double thresholdY;
        for (int blockIndex = 0; blockIndex < blocks.size() - 1; blockIndex++)
        {
            Block a = blocks.get(blockIndex);
            Block b = blocks.get(blockIndex + 1);

            if (doesNotMatchType(a, b))
                continue;

            // Same orientation
            if (a.getBlockOrientation().equals(b.getBlockOrientation())
                    && a.getTextOrientation().equals(b.getTextOrientation()))
            {
                Rectangle2 aBound = a.getBound();
                Rectangle2 bBound  = b.getBound();
                if (a.getBlockOrientation().equals(Direction.TOP) || a.getBlockOrientation().equals(Direction.BOTTOM))
                {
                    double dist = bBound.getY() - aBound.getY();
                    thresholdY = Math.min(aBound.getHeight(), bBound.getHeight()) * mergeFactor;
                    if (Math.abs(dist) < thresholdY)
                    {
                        /*
                         * On sait que les éléments proche et on regarde si l'un est au dessus de l'autre
                         * En plus on s'assure que le second texte soit au milieu du premier, au début ou à la fin.
                         * Sinon on considère que les deux textes ne sont pas liés.
                         */
                        if (dist < 0) // On échange
                        {
                            Block tmp = a;
                            a = b;
                            b = tmp;
                        }

                        if (startOrMiddleOrEnd(aBound.getX(), aBound.getWidth(), bBound.getX(), bBound.getWidth())
                                && b.getPrev() == null && a.getNext() == null)
                        {
                            a.setNext(b);
                            b.setPrev(a);
                        }
                    }
                }
                else
                {
                    double dist = bBound.getX() - aBound.getX();
                    thresholdY = Math.min(aBound.getWidth(), bBound.getWidth()) * 2;
                    if (Math.abs(dist) < thresholdY)
                    {
                        if (dist < 0) // On échange
                        {
                            Block tmp = a;
                            a = b;
                            b = tmp;
                        }

                        if (startOrMiddleOrEnd(aBound.getY(), aBound.getHeight(), bBound.getY(), bBound.getHeight())
                                && b.getPrev() == null && a.getNext() == null)
                        {
                            a.setNext(b);
                            b.setPrev(a);
                        }
                    }
                }
            }
        }

        finaliseMerging();
    }

    private void finaliseMerging()
    {
        // Remove block
        for (Iterator<Block> iterator = blocks.iterator(); iterator.hasNext(); )
        {
            Block block = iterator.next();
            if (block.getPrev() != null)
            {
                iterator.remove();
            }
        }

        for (Iterator<Block> iterator = blocks.iterator(); iterator.hasNext(); )
        {
            Block block = iterator.next();

            // If need change
            if (block.getNext() != null)
            {
                Rectangle2 rec = new Rectangle2();
                rec.setX(Double.MAX_VALUE);
                rec.setY(Double.MAX_VALUE);
                rec.setWidth(0);
                rec.setHeight(0);
                StringBuilder text = new StringBuilder();
                StringBuilder originalText = new StringBuilder();
                Block next = block;
                while (next != null)
                {
                    text.append(next.getOriginalText());
                    text.append(' ');

                    originalText.append(next.getFormattedText());
                    originalText.append(' ');

                    Rectangle2 bound = next.getBound();
                    if (bound.getWidth() > bound.getHeight())
                    {
                        rec.setX(Math.min(rec.getX(), bound.getX()));
                        rec.setY(Math.min(rec.getY(), bound.getY()));
                        rec.setWidth(Math.max(rec.getWidth(), bound.getWidth()));
                        rec.setHeight(rec.getHeight() + bound.getHeight());
                    }
                    else
                    {
                        rec.setX(Math.min(rec.getX(), bound.getX()));
                        rec.setY(Math.min(rec.getY(), bound.getY()));
                        rec.setWidth(rec.getWidth() + bound.getWidth());
                        rec.setHeight(Math.max(rec.getHeight(), bound.getHeight()));
                    }
                    next = next.getNext();
                }

                block.setFormattedText(text.toString());
                block.setOriginalText(originalText.toString());
                block.setBound(rec);
            }
        }
    }

    /**
     * On regarde si l'élément au dessus commence au début, au milieu ou à la fin de l'élément au dessus. On sait déjà
     * que les deux éléments sont l'un au dessus de l'autre.
     *
     * @param top    pos element au dessus
     * @param lenTop taille de l'élémént au dessus
     * @param under  pos element au dessous
     * @param lenU   taille de l'élément au dessous
     *
     * @return test result
     */
    public static boolean startOrMiddleOrEnd(double top, double lenTop, double under, double lenU)
    {
        double add = Math.min(Math.max(lenTop, lenU) * 0.1f, 40);

        double a = under - top;
        double b = Math.abs(under + lenU / 2 - top - lenTop / 2);
        double c = top + lenTop - under - lenU;


        return (a >= -add/4 && a < add/2 || // Left
                b < add || // Middle
                c >= -add/4 && c < add/2); // Right
    }

    public static boolean doesNotMatchType(Block A, Block B)
    {
        // Voici les règles :
        // Mêmes couleurs (Font, back)
        // Le texte n'est pas seulement que des nombres ou des caractères pour les opérations
        return A.getOriginalText().length() == 0 ||
                B.getOriginalText().length() == 0 ||
                notContainsOnce(A.getBackColors(), B.getBackColors()) ||
                notContainsOnce(A.getFontColors(), B.getFontColors()) ||
                A.getFonts().size() != B.getFonts().size();
    }

    public static boolean notContainsOnce(Set e1, Set e2)
    {
        for (Iterator iterator = e1.iterator(); iterator.hasNext(); )
        {
            Object next = iterator.next();
            if (e2.contains(next))
                return false;
        }
        return true;
    }

    public void mergeBlocks()
    {
        DefaultBlockMerger blockMerger = new DefaultBlockMerger();
        blockMerger.mergeIfNecessaryBlocks(blocks);
    }
}
