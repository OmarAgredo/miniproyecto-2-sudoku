package com.example.demo.view;

/**
 * Defines the basic contract for visual components in the application.
 *
 * @author Omar Esteban Agredo
 */
public interface IView {
    /**
     * Displays the view.
     */
    void showView();

    /**
     * Removes or closes the view.
     */
    void deleteView();
}
