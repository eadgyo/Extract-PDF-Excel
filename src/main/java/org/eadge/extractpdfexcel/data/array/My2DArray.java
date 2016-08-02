package org.eadge.extractpdfexcel.data.array;

import java.util.ArrayList;

/**
 * Created by eadgyo on 21/07/16.
 *
 * Sort blocks in a 2D array.
 * The array is always a rectangle. Columns have the same number of lines, and lines have the same number of columns.
 */
public class My2DArray<T>
{
    private ArrayList<ArrayList<T>> columns;

    public My2DArray()
    {
        columns = new ArrayList<>();
    }

    /**
     * Get an element at the specified position
     *
     * @param col column index
     * @param line line index
     *
     * @return element at the position or null if there is nothing.
     */
    public T get(int col, int line)
    {
        assert (col >= 0 && line >= 0);

        if (col >= numberOfColumns() ||
                line > numberOfLines())
        {
            return null;
        }

        return columns.get(col).get(line);
    }

    /**
     * Change the element at the given position. If index are out of the array, the array is extended.
     *
     * @param col column index
     * @param line line index
     * @param t set element
     */
    public void set(int col, int line, T t)
    {
        assert (col >= 0 && line >= 0);

        // Extend the array if needed
        fillColumn(col);
        fillLine(line);

        // Change the element
        columns.get(col).set(line, t);
    }

    /**
     * Add column at the end of the 2D array
     *
     * @param column added column, the array is resize if the column size exceed the max column size, and add if the
     *               added column size is lower than the maximum column size, null object are added at the end of the
     *               added column.
     */
    public void addColumn(ArrayList<T> column)
    {
        insertColumn(numberOfColumns(), column);
    }

    /**
     * Add column at the insert index of the 2D array
     *
     * @param insertIndex column insert index
     * @param column      added column, the array is resize if the column size exceed the max column size, and add if
     *                    the added column size is lower than the maximum column size, null object are added at the end
     *                    of the added column.
     */
    public void insertColumn(int insertIndex, ArrayList<T> column)
    {
        // Extend array limits if inserted line exceeds them
        fillColumn(insertIndex - 1);
        fillLine(column.size());

        // Fill the rest of the line if the line is under limit of the array
        fillNullRestOfLane(numberOfLines(), column);

        columns.add(insertIndex, column);
    }

    /**
     * Get the number of columns in the array.
     *
     * @return number of columns
     */
    public int numberOfColumns()
    {
        return columns.size();
    }

    /**
     * Get the number of lines in the array.
     *
     * @return number of lines
     */
    public int numberOfLines()
    {
        if (numberOfColumns() == 0)
            return 0;
        return columns.get(0).size();
    }

    /**
     * Add a column at the end of the array.
     */
    public void addColumn()
    {
        insertColumn(numberOfColumns());
    }

    /**
     * Add a column in array at specified index.
     *
     * @param insertIndex column insert index
     */
    public void insertColumn(int insertIndex)
    {
        ArrayList<T> line = new ArrayList<>();
        insertColumn(insertIndex, line);
    }
    
    /**
     * Add line at the end of the 2D array
     *
     * @param line added line, the array is resize if the line size exceed the max line size, and add if the
     *               added line size is lower than the maximum line size, null object are added at the end of the
     *               added line.
     */
    public void addLine(ArrayList<T> line)
    {
        insertLine(numberOfLines(), line);
    }

    /**
     * Add line at the insert index of the 2D array
     *
     * @param insertIndex line insert index
     * @param line      added line, the array is resize if the line size exceed the max line size, and add if
     *                    the added line size is lower than the maximum line size, null object are added at the end
     *                    of the added line.
     */
    public void insertLine(int insertIndex, ArrayList<T> line)
    {
        assert (insertIndex > 0);

        // Extend array limits if inserted line exceeds them
        fillLine(insertIndex - 1);
        fillColumn(line.size());

        // Fill the rest of the line if the line is under limit of the array
        fillNullRestOfLane(numberOfColumns(), line);

        for (int i = 0; i < line.size(); i++)
        {
            T t = line.get(i);
            columns.get(i).add(insertIndex, t);
        }
    }

    /**
     * Add a line at the end of the array.
     */
    public void addLine()
    {
        insertLine(numberOfLines());
    }

    /**
     * Add a line in array at specified index.
     *
     * @param insertIndex line insert index
     */
    public void insertLine(int insertIndex)
    {
        ArrayList<T> column = new ArrayList<>();
        insertLine(insertIndex, column);
    }

    private void fillColumn(int finalSize)
    {
        for (int i = numberOfColumns(); i < finalSize; i++)
        {
            addColumn();
        }
    }

    private void fillLine(int finalSize)
    {
        for (int i = numberOfLines(); i < finalSize; i++)
        {
            addLine();
        }
    }

    private void fillNullRestOfLane(int max, ArrayList<T> lane)
    {
        for (int i = lane.size(); i < max; i++)
        {
            lane.add(null);
        }
    }
}
