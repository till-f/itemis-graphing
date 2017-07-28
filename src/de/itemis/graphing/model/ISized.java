package de.itemis.graphing.model;

public interface ISized
{
    /**
     * @return the original size of the object (as specified when created)
     */
    Size getInnerSize();

    /**
     * @return the extended size of the object after something was added (e.g. an attachment, padding)
     */
    Size getOuterSize();
}
