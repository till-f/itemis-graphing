package de.itemis.graphing.model;

import de.itemis.graphing.model.style.Style;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Stack;

public abstract class GraphElement<T> implements IStyled
{
    public enum ELabelPriority { Normal, Low, AlwaysShown }

    protected final Graph<T> _graph;
    protected final String _id;

    private T _userObject = null;
    private String _label = null;
    private ELabelPriority _labelPrio = ELabelPriority.Normal;

    private Stack<Style> _highlightStyles = new Stack<>();
    private boolean _isSelectable = true;
    private boolean _isSelected = false;
    private boolean _isClickable = true;
    private boolean _isClicked = false;

    // must be set in constructors of concrete graph elements
    protected Style _styleRegular;
    protected Style _styleClicked;
    protected Style _styleSelected;

    public GraphElement(Graph<T> g, String id)
    {
        _graph = g;
        _id = id;
        setInitialStyles();
    }

    protected abstract void setInitialStyles();

    public Graph<T> getGraph()
    {
        return _graph;
    }

    public String getId()
    {
        return _id;
    }

    public T getUserObject()
    {
        return _userObject;
    }

    public void setUserObject(T userObject)
    {
        _userObject = userObject;
    }

    public String getLabel()
    {
        return _label;
    }

    public void setLabel(String label)
    {
        _label = label;
        _graph.labelChanged(this);

    }

    public ELabelPriority getLabelPrio()
    {
        return _labelPrio;
    }

    public void setLabelPrio(ELabelPriority labelPrio)
    {
        _labelPrio = labelPrio;
        _graph.labelPriorityChanged(this);
    }

    // -----------------------------------------------------------------------------------------------------------------
    // click and selection state handling

    public boolean isSelectable()
    {
        return _isSelectable;
    }

    public void setIsSelectable(boolean selectable)
    {
        _isSelectable = selectable;
    }

    public boolean isSelected()
    {
        return _isSelected;
    }

    public void setSelected(boolean selected)
    {
        if (selected && !_isSelectable)
            throw new RuntimeException("element is not selectable");

        if (_isSelected != selected)
        {
            _isSelected = selected;
            styleChanged();
        }
    }

    public boolean isClickable()
    {
        return _isClickable;
    }

    public void setIsClickable(boolean clickable)
    {
        _isClickable = clickable;
    }

    public void setClicked(boolean clicked)
    {
        if (clicked && !_isClickable)
            throw new RuntimeException("element is not clickable");

        if (_isClicked != clicked)
        {
            _isClicked = clicked;
            styleChanged();
        }
    }

    protected void styleChanged()
    {
        _graph.styleChanged(this);
    }

    // -----------------------------------------------------------------------------------------------------------------
    // IStyled

    @Override
    public Style getStyleRegular()
    {
        return _styleRegular.getCopy();
    }

    @Override
    public Style getStyleClicked()
    {
        return _styleClicked.getCopy();
    }

    @Override
    public Style getStyleSelected()
    {
        return _styleSelected.getCopy();
    }

    @Override
    public void setStyleRegular(Style newStyle)
    {
        _styleRegular = newStyle.getCopy();
        styleChanged();
    }

    @Override
    public void setStyleClicked(Style newStyle)
    {
        _styleClicked = newStyle.getCopy();
        styleChanged();
    }

    @Override
    public void setStyleSelected(Style newStyle)
    {
        _styleSelected = newStyle.getCopy();
        styleChanged();
    }

    @Override
    public void setAllStyles(Style newStyle)
    {
        _styleRegular = newStyle.getCopy();
        _styleClicked = newStyle.getCopy();
        _styleSelected = newStyle.getCopy();
        styleChanged();
    }

    @Override
    public void pushHighlighting(Style hightlightingStyle)
    {
        _highlightStyles.push(hightlightingStyle);
        styleChanged();
    }

    @Override
    public void popHighlighting()
    {
        _highlightStyles.pop();
        styleChanged();
    }

    @Override
    public void clearHighlighting()
    {
        _highlightStyles.clear();
        styleChanged();
    }

    @Override
    public Style getActiveStyle()
    {
        if (_isClicked)
            return mergeStyles(_styleRegular, _styleClicked);
        else if (_isSelected)
            return mergeStyles(_styleRegular, _styleSelected);
        else if (_highlightStyles.size() > 0)
            return mergeStyles(_styleRegular, _highlightStyles.peek());
        else
            return _styleRegular;
    }

    @Override
    public String toString()
    {
        return getId();
    }

    private static Style mergeStyles(Style primary, Style secondary)
    {
        try {
            Style result = primary.getCopy();
            copyAllFieldsNotNull(secondary, result);
            return result;
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Could not merge styles", e);
        }
    }

    private static <T1 extends Object>  void copyAllFieldsNotNull(T1 srcObject, T1 dstObject) throws IllegalAccessException
    {
        Class<?> clazz = srcObject.getClass();
        while (clazz != null && Style.class.isAssignableFrom(clazz))
        {
            for (Field field : clazz.getDeclaredFields())
            {
                if ((field.getModifiers() & (Modifier.FINAL | Modifier.STATIC)) != 0)
                    continue;

                field.setAccessible(true);
                Object value = field.get(srcObject);
                if (value != null)
                {
                    field.set(dstObject, value);
                }
            }

            clazz = clazz.getSuperclass();
        }

    }

}
