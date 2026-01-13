package edu.univ.erp.ui.panels.student;

import edu.univ.erp.auth.SessionManager;

import edu.univ.erp.ui.components.RoundedButton;
import edu.univ.erp.ui.components.TitledContainer;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class TranscriptPanel extends JPanel {
    private final edu.univ.erp.service.StudentService studentService = new edu.univ.erp.service.StudentService();

    public TranscriptPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        setBackground(new Color(245, 247, 250)); // Modern background

        TitledContainer container = new TitledContainer("Official Transcript");

        JPanel inner = new JPanel();
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setOpaque(false);

        JLabel text = new JLabel("<html>Your official academic transcript contains:<br>" +
                "• All completed courses<br>" +
                "• Grades and credits earned<br>" +
                "• CGPA calculation<br><br>" +
                "Click below to download your transcript as a PDF.</html>");
        text.setFont(new Font("SansSerif", Font.PLAIN, 16));
        text.setForeground(new Color(44, 62, 80));
        text.setAlignmentX(Component.LEFT_ALIGNMENT);

        RoundedButton downloadBtn = new RoundedButton("Download Transcript (PDF)", new Color(52, 152, 219));
        downloadBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        downloadBtn.addActionListener(e -> downloadPdf());

        RoundedButton downloadCsvBtn = new RoundedButton("Download Transcript (CSV)", new Color(46, 204, 113));
        downloadCsvBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        downloadCsvBtn.addActionListener(e -> downloadCsv());

        inner.add(text);
        inner.add(Box.createVerticalStrut(25));
        inner.add(downloadBtn);
        inner.add(Box.createVerticalStrut(10));
        inner.add(downloadCsvBtn);

        container.setInnerComponent(inner);
        add(container, BorderLayout.CENTER);
    }

    private void downloadPdf() {
        downloadFile("pdf");
    }

    private void downloadCsv() {
        downloadFile("csv");
    }

    private void downloadFile(String type) {
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            String filePath = null;
            String errorMsg = null;

            @Override
            protected Boolean doInBackground() {
                try {
                    int studentId = SessionManager.getCurrentUserId();

                    JFileChooser chooser = new JFileChooser();
                    chooser.setSelectedFile(new File("transcript_" + studentId + "." + type));

                    int result = chooser.showSaveDialog(TranscriptPanel.this);
                    if (result != JFileChooser.APPROVE_OPTION)
                        return false;

                    filePath = chooser.getSelectedFile().getAbsolutePath();

                    if ("pdf".equals(type)) {
                        return studentService.exportTranscriptPdf(studentId, filePath);
                    } else {
                        return studentService.exportTranscriptCsv(studentId, filePath);
                    }
                } catch (Exception ex) {
                    errorMsg = ex.getMessage();
                    return false;
                }
            }

            @Override
            protected void done() {
                try {
                    boolean success = get();
                    if (success && filePath != null) {
                        JOptionPane.showMessageDialog(TranscriptPanel.this,
                                "Transcript successfully saved to:\n" + filePath,
                                "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else if (errorMsg != null) {
                        JOptionPane.showMessageDialog(TranscriptPanel.this,
                                "Failed to generate transcript: " + errorMsg,
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(TranscriptPanel.this,
                            "Error: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
}
