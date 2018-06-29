package de.itemis.graphing.util;

import java.awt.*;

public class Screen
{
    public static double getScalingFactor()
    {
        return Toolkit.getDefaultToolkit().getScreenResolution() / 96.0;

        // if toolkit was running while resolution is changed, DPI is not correctly updated. following code could work
        // as workaround, but is not needed, as AWT is scaled by windows in this case... scaling works really crappy for
        // windows + java awt...

        /*
        double virtualWidth = Toolkit.getDefaultToolkit().getScreenSize().getWidth();

        double physicalWidth = virtualWidth;
        for (GraphicsDevice gd : GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices())
        {
            DisplayMode dm = gd.getDisplayMode();
            if (dm.getWidth() > physicalWidth) physicalWidth = dm.getWidth();
        }

        return physicalWidth / virtualWidth;
        */
    }
}
