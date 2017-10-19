package de.itemis.graphing.view;

public interface IHandler
{

    /**
     * called by ViewManager after the handler has been registered
     * */
    default void setViewManager(IViewManager viewManager)
    {
    }

}
