package de.itemis.graphing.listeners;

import de.itemis.graphing.model.BaseGraphElement;
import de.itemis.graphing.model.style.Style;

import java.util.HashMap;

public class StyleStorage
{
    private HashMap<BaseGraphElement, Style> _storedStyles = new HashMap<>();

    public void restoreStyle(BaseGraphElement element)
    {
        if (!_storedStyles.containsKey(element))
            return;

        element.setStyle(_storedStyles.get(element));

        _storedStyles.remove(element);
    }

    public void storeStyle(BaseGraphElement element)
    {
        if (_storedStyles.containsKey(element))
            return;

        _storedStyles.put(element, element.getStyle().getCopy());
    }
}
