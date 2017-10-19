package de.itemis.graphing.view;

import de.itemis.graphing.model.GraphElement;

public interface IHoverHandler extends IHandler
{

    public class HoverParameters
    {
        private final boolean _isCtrlPressed;
        private final boolean _isShiftPressed;
        private final boolean _isAltPressed;

        public HoverParameters(boolean isCtrlPressed, boolean isShiftPressed, boolean isAltPressed)
        {
            _isCtrlPressed =isCtrlPressed;
            _isShiftPressed = isShiftPressed;
            _isAltPressed = isAltPressed;
        }

        public boolean isCtrlPressed() {
            return _isCtrlPressed;
        }

        public boolean isShiftPressed() {
            return _isShiftPressed;
        }

        public boolean isAltPressed() {
            return _isAltPressed;
        }
    }

    void mouseHover(GraphElement enterElement, GraphElement exitElement, HoverParameters params);

}
