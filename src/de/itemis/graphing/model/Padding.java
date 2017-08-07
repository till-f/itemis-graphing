package de.itemis.graphing.model;

public class Padding
{
    private final double _north;
    private final double _east;
    private final double _south;
    private final double _west;

    public Padding(double north, double east, double south, double west)
    {
        _north = north;
        _east = east;
        _south = south;
        _west = west;
    }

    public Padding(double allSides)
    {
        _north = allSides;
        _east = allSides;
        _south = allSides;
        _west = allSides;
    }

    public Padding(double horizontal, double vertical)
    {
        _east = horizontal;
        _west = horizontal;
        _north = vertical;
        _south = vertical;
    }

    public double getNorth() {
        return _north;
    }

    public double getEast() {
        return _east;
    }

    public double getSouth() {
        return _south;
    }

    public double getWest() {
        return _west;
    }
}
