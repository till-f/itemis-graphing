package de.itemis.graphing.view.handlers;

import de.itemis.graphing.view.IViewManager;

public interface IHandler
{

    /**
     * called by ViewManager after the handler has been registered
     * */
    default void setViewManager(IViewManager viewManager)
    {
    }

}
