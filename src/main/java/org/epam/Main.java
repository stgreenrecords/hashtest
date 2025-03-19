package org.epam;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("File Hash Calculator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 250);
        frame.setLayout(new FlowLayout());

        JLabel fileLabel = new JLabel("No file selected");
        JButton browseButton = new JButton("Choose File");
        String[] algorithms = {"SHA-512", "SHA-256", "MD5" };
        JComboBox<String> algorithmBox = new JComboBox<>(algorithms);
        JLabel hashLabel = new JLabel("<html>Hash: <br> </html>");
        JButton calculateButton = new JButton("Calculate Hash");
        JButton copyButton = new JButton("Copy to Clipboard");
        copyButton.setEnabled(false); // Disabled until a hash is generated

        frame.add(browseButton);
        frame.add(fileLabel);
        frame.add(algorithmBox);
        frame.add(calculateButton);
        frame.add(copyButton);
        frame.add(hashLabel);

        final File[] selectedFile = {null};

        // File chooser action
        browseButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                selectedFile[0] = fileChooser.getSelectedFile();
                fileLabel.setText(selectedFile[0].getName());
            }
        });

        // Calculate hash action
        calculateButton.addActionListener(e -> {
            if (selectedFile[0] != null) {
                String algorithm = (String) algorithmBox.getSelectedItem();
                try {
                    String hash = FileHasher.calculateHash(selectedFile[0], algorithm);
                    hashLabel.setText("<html>Hash: <br>" + hash + "</html>");
                    copyButton.setEnabled(true); // Enable copy button
                    copyButton.putClientProperty("hashValue", hash); // Store hash for copy action
                } catch (IOException | NoSuchAlgorithmException ex) {
                    JOptionPane.showMessageDialog(frame, "Error calculating hash!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Please select a file first.", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        });

        // Copy hash to clipboard
        copyButton.addActionListener(e -> {
            String hash = (String) copyButton.getClientProperty("hashValue");
            if (hash != null && !hash.isEmpty()) {
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(new StringSelection(hash), null);
                JOptionPane.showMessageDialog(frame, "Hash copied to clipboard!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        frame.setVisible(true);
    }
}