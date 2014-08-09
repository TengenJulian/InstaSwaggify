package zwaggerboyz.instaswaggify.filters;


/*
 * APP:     InstaSwaggify
 * DATE:    June 2014
 * NAMES:   Mathijs Molenaar, Tristan van Vaalen, David Veenstra, Peter Verkade, Matthijs de Wit,
 *          Arne Zismer
 *
 * FILE:    IFILTER.java
 * This file contains interface for the filter-classes.
 */


import zwaggerboyz.instaswaggify.TexturedSquare;

public interface IFilter {

    public String getName();

    public AbstractFilterClass.FilterID getID();

    public String getLabel(int i);

    public int getValue(int i);

    public void setValue(int i, int value);

    public int getNumValues();

    public void setArray(int[] array);

    public void draw(float[] mvpMatrix, int fboTexture);

    public void setTextureDataHandle(int texture);

    public TexturedSquare getTextureSquare();

    public IFilter clone();

    public void close();
}