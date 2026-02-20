package com.academia;

import com.academia.view.MainFrame;
import com.academia.view.utils.UIStyle;

import javax.swing.*;

/**
 * Application entry point for the Academic Management System.
 *
 * Demonstrates the integration of:
 *  - BUILDER pattern  — step-by-step construction of complex StudyPlan objects.
 *  - PROTOTYPE pattern — deep cloning of existing plans for new academic periods.
 *  - Java Swing       — cross-platform desktop GUI.
 */
public class Main {

    public static void main(String[] args) {
        // Apply Look & Feel before any Swing component is created
        UIStyle.applyLookAndFeel();

        // Launch the GUI on the Swing Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                new MainFrame();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "Error al iniciar la aplicación:\n" + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}