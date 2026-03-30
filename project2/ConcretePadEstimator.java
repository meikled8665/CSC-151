import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.border.*;

public class ConcretePadEstimator extends JFrame {

    // Input fields
    private JTextField lengthField;
    private JTextField widthField;
    private JTextField thicknessField;
    private JTextField employeesField;
    private JTextField projectNameField;
    private JTextField locationField;

    // Output labels
    private JLabel areaLabel;
    private JLabel volumeCYLabel;
    private JLabel timeLabel;
    private JLabel matCostLabel;
    private JLabel laborCostLabel;
    private JLabel totalCostLabel;

    private static final String CSV_FILE = "Projects.csv";
    private static final String CSV_HEADER =
        "Project Name,Location,Length (ft),Width (ft),Thickness (in),Employees," +
        "Area (sq ft),Volume (CY+10%),Total Time (hr),Material Cost,Labor Cost,Total Cost";

    public ConcretePadEstimator() {
        setTitle("Concrete Pour Cost Estimator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(200, 100));
        setResizable(false);

        // ---- Color palette ----
        Color bg         = new Color(28, 32, 40);
        Color panel      = new Color(38, 43, 54);
        Color accent     = new Color(120, 60, 210);
        Color textLight  = new Color(175, 125, 255);
        Color textDim    = new Color(140, 150, 165);
        Color fieldBg    = new Color(22, 26, 33);
        Color saveGreen  = new Color(40, 167, 80);
        Color loadBlue   = new Color(30, 120, 200);

        getContentPane().setBackground(bg);

        // ===== HEADER =====
        JPanel header = new JPanel();
        header.setBackground(bg);
        header.setBorder(new EmptyBorder(18, 20, 8, 20));

        JLabel title = new JLabel("CONCRETE COST ESTIMATOR");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(accent);
        header.add(title);

        // ===== PROJECT INFO PANEL =====
        JPanel inputPanel2 = createCard(panel, accent, "PROJECT INFO");
        inputPanel2.setLayout(new BoxLayout(inputPanel2, BoxLayout.Y_AXIS));
        projectNameField = createField(fieldBg, textLight);
        locationField = createField(fieldBg, textLight);

        inputPanel2.add(Box.createVerticalStrut(6));
        addInputRow(inputPanel2, "Project Name:", projectNameField, textDim, textLight);
        addInputRow(inputPanel2, "Location:",     locationField,    textDim, textLight);

        // Save / Load buttons row inside PROJECT INFO
        JButton saveBtn = new JButton("SAVE PROJECT");
        styleSmallBtn(saveBtn, saveGreen, bg);
        saveBtn.addActionListener(e -> saveProject());

        JButton loadBtn = new JButton("LOAD PROJECT");
        styleSmallBtn(loadBtn, loadBlue, Color.WHITE);
        loadBtn.addActionListener(e -> loadProject());

        JPanel saveBtnRow = new JPanel(new GridLayout(1, 2, 8, 0));
        saveBtnRow.setOpaque(false);
        saveBtnRow.setBorder(new EmptyBorder(6, 14, 12, 14));
        saveBtnRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        saveBtnRow.add(saveBtn);
        saveBtnRow.add(loadBtn);
        inputPanel2.add(saveBtnRow);

        JPanel projectWrapper = new JPanel(new BorderLayout());
        projectWrapper.setBackground(bg);
        projectWrapper.setBorder(new EmptyBorder(6, 16, 6, 16));
        projectWrapper.add(inputPanel2, BorderLayout.CENTER);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(bg);
        topPanel.add(header, BorderLayout.NORTH);
        topPanel.add(projectWrapper, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // ===== MAIN BODY =====
        JPanel body = new JPanel(new GridLayout(1, 2, 12, 0));
        body.setBackground(bg);
        body.setBorder(new EmptyBorder(10, 16, 0, 16));

        // --- INPUT PANEL ---
        JPanel inputPanel = createCard(panel, accent, "INPUTS");
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));

        lengthField    = createField(fieldBg, textLight);
        widthField     = createField(fieldBg, textLight);
        thicknessField = createField(fieldBg, textLight);
        employeesField = createField(fieldBg, textLight);

        inputPanel.add(Box.createVerticalStrut(6));
        addInputRow(inputPanel, "Length (ft):",    lengthField,    textDim, textLight);
        addInputRow(inputPanel, "Width (ft):",     widthField,     textDim, textLight);
        addInputRow(inputPanel, "Thickness (in):", thicknessField, textDim, textLight);
        addInputRow(inputPanel, "# of Employees:", employeesField, textDim, textLight);
        inputPanel.add(Box.createVerticalStrut(14));

        // Calculate button
        JButton calcBtn = new JButton("CALCULATE");
        calcBtn.setFont(new Font("Arial", Font.BOLD, 13));
        calcBtn.setBackground(accent);
        calcBtn.setForeground(new Color(28, 32, 40));
        calcBtn.setFocusPainted(false);
        calcBtn.setBorder(new EmptyBorder(10, 0, 10, 0));
        calcBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        calcBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        calcBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        calcBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { calcBtn.setBackground(accent.brighter()); }
            public void mouseExited(MouseEvent e)  { calcBtn.setBackground(accent); }
        });
        calcBtn.addActionListener(e -> calculate());

        JPanel btnWrapper = new JPanel(new BorderLayout());
        btnWrapper.setOpaque(false);
        btnWrapper.setBorder(new EmptyBorder(0, 14, 14, 14));
        btnWrapper.add(calcBtn, BorderLayout.CENTER);
        inputPanel.add(btnWrapper);

        // --- OUTPUT PANEL ---
        JPanel outputPanel = createCard(panel, accent, "RESULTS");
        outputPanel.setLayout(new BoxLayout(outputPanel, BoxLayout.Y_AXIS));

        areaLabel     = createResultLabel(textDim);
        volumeCYLabel = createResultLabel(textDim);
        timeLabel     = createResultLabel(textDim);
        matCostLabel  = createResultLabel(textDim);
        laborCostLabel= createResultLabel(textDim);
        totalCostLabel= createResultLabel(accent);
        totalCostLabel.setFont(new Font("Arial", Font.BOLD, 13));

        outputPanel.add(Box.createVerticalStrut(6));
        addResultRow(outputPanel, "Area:",            areaLabel,      textDim, textLight);
        addResultRow(outputPanel, "Volume (CY+10%):", volumeCYLabel,  textDim, textLight);
        addResultRow(outputPanel, "Total Time (hr):", timeLabel,      textDim, textLight);
        addResultRow(outputPanel, "Material Cost:",   matCostLabel,   textDim, textLight);
        addResultRow(outputPanel, "Labor Cost:",      laborCostLabel, textDim, textLight);

        // Divider
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(60, 68, 82));
        sep.setBackground(new Color(60, 68, 82));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        JPanel sepWrap = new JPanel(new BorderLayout());
        sepWrap.setOpaque(false);
        sepWrap.setBorder(new EmptyBorder(6, 14, 6, 14));
        sepWrap.add(sep);
        outputPanel.add(sepWrap);

        addResultRow(outputPanel, "TOTAL COST:", totalCostLabel, accent, accent);
        outputPanel.add(Box.createVerticalGlue());

        // Reset button
        JButton resetBtn = new JButton("RESET");
        resetBtn.setFont(new Font("Arial", Font.PLAIN, 11));
        resetBtn.setBackground(fieldBg);
        resetBtn.setForeground(textDim);
        resetBtn.setFocusPainted(false);
        resetBtn.setBorder(new EmptyBorder(7, 0, 7, 0));
        resetBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        resetBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        resetBtn.addActionListener(e -> resetFields());

        JPanel resetWrapper = new JPanel(new BorderLayout());
        resetWrapper.setOpaque(false);
        resetWrapper.setBorder(new EmptyBorder(0, 14, 14, 14));
        resetWrapper.add(resetBtn, BorderLayout.CENTER);
        outputPanel.add(resetWrapper);

        body.add(inputPanel);
        body.add(outputPanel);
        add(body, BorderLayout.CENTER);

        // ===== FOOTER =====
        JPanel footer = new JPanel();
        footer.setBackground(bg);
        footer.setBorder(new EmptyBorder(3, 0, 8, 0));
        JLabel note = new JLabel("Material: $125/CY  ·  Labor: $21/hr/employee  ·  +10% volume buffer");
        note.setFont(new Font("Arial", Font.PLAIN, 10));
        note.setForeground(textDim);
        footer.add(note);
        add(footer, BorderLayout.SOUTH);

        pack();
        setMinimumSize(new Dimension(600, 400));
        setLocationRelativeTo(null);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private void styleSmallBtn(JButton btn, Color bg, Color fg) {
        btn.setFont(new Font("Arial", Font.BOLD, 11));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(7, 0, 7, 0));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private JPanel createCard(Color bg, Color accent, String title) {
        JPanel card = new JPanel();
        card.setBackground(bg);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(55, 62, 75), 1, true),
            new EmptyBorder(0, 0, 0, 0)
        ));

        JLabel lbl = new JLabel("  " + title);
        lbl.setFont(new Font("Arial", Font.BOLD, 11));
        lbl.setForeground(accent);
        lbl.setOpaque(true);
        lbl.setBackground(new Color(45, 51, 63));
        lbl.setBorder(new EmptyBorder(6, 0, 6, 0));
        lbl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        lbl.setPreferredSize(new Dimension(Integer.MAX_VALUE, 30));

        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(lbl);
        return card;
    }

    private JTextField createField(Color bg, Color fg) {
        JTextField f = new JTextField(10);
        f.setBackground(bg);
        f.setForeground(fg);
        f.setCaretColor(fg);
        f.setFont(new Font("Arial", Font.PLAIN, 13));
        f.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(60, 68, 82), 1),
            new EmptyBorder(4, 8, 4, 8)
        ));
        return f;
    }

    private JLabel createResultLabel(Color fg) {
        JLabel l = new JLabel("—");
        l.setFont(new Font("Arial", Font.PLAIN, 13));
        l.setForeground(fg);
        return l;
    }

    private void addInputRow(JPanel parent, String labelText,
                             JTextField field, Color labelColor, Color fieldColor) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(5, 14, 5, 14));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Arial", Font.PLAIN, 12));
        lbl.setForeground(labelColor);
        lbl.setPreferredSize(new Dimension(130, 24));

        row.add(lbl, BorderLayout.WEST);
        row.add(field, BorderLayout.CENTER);
        parent.add(row);
    }

    private void addResultRow(JPanel parent, String labelText,
                              JLabel valueLabel, Color labelColor, Color valueColor) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(5, 14, 5, 14));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Arial", Font.PLAIN, 12));
        lbl.setForeground(labelColor);
        lbl.setPreferredSize(new Dimension(150, 22));

        row.add(lbl, BorderLayout.WEST);
        row.add(valueLabel, BorderLayout.CENTER);
        parent.add(row);
    }

    // ── Core logic ────────────────────────────────────────────────────────────

    private void calculate() {
        try {
            double length    = Double.parseDouble(lengthField.getText().trim());
            double width     = Double.parseDouble(widthField.getText().trim());
            double thickness = Double.parseDouble(thicknessField.getText().trim());
            int    employees = Integer.parseInt(employeesField.getText().trim());

            if (length <= 0 || width <= 0 || thickness <= 0 || employees <= 0) {
                showError("All values must be greater than zero.");
                return;
            }

            double area   = length * width;
            double vol    = area * (thickness / 12.0);
            double volCY  = (vol * 1.10) / 27.0;

            double emplTimeEst = (volCY * 3.0) / employees;
            double pourTime    = (volCY * 5.0) / 60.0;
            double totTime     = emplTimeEst + pourTime + 1.0;

            double matCost   = volCY * 125.0;
            double laborCost = totTime * employees * 21.0;
            double total     = matCost + laborCost;

            areaLabel.setText(String.format("%.2f sq ft", area));
            volumeCYLabel.setText(String.format("%.3f CY", volCY));
            timeLabel.setText(String.format("%.2f hrs", totTime));
            matCostLabel.setText(String.format("$%.2f", matCost));
            laborCostLabel.setText(String.format("$%.2f", laborCost));
            totalCostLabel.setText(String.format("$%.2f", total));

        } catch (NumberFormatException ex) {
            showError("Please enter valid numbers in all fields.");
        }
    }

    // ── CSV helpers ──────────────────────────────────────────────────────────

    /** Creates Projects.csv with the header row if it does not already exist. */
    private void ensureCSVExists() throws IOException {
        File file = new File(CSV_FILE);
        if (!file.exists()) {
            try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
                pw.println(CSV_HEADER);
        }
    }

    // ── CSV Save ─────────────────────────────────────────────────────────────

    private void saveProject() {
        String projectName = projectNameField.getText().trim();
        String location    = locationField.getText().trim();
        String length      = lengthField.getText().trim();
        String width       = widthField.getText().trim();
        String thickness   = thicknessField.getText().trim();
        String employees   = employeesField.getText().trim();

        if (projectName.isEmpty() || length.isEmpty() || width.isEmpty()
                || thickness.isEmpty() || employees.isEmpty()) {
            showError("Please fill in all inputs and calculate before saving.");
            return;
        }

        if (totalCostLabel.getText().equals("—")) {
            showError("Please calculate results before saving.");
            return;
        }

        try {
            ensureCSVExists();                          // create file + header if needed
            File csv = new File(CSV_FILE);
            try (PrintWriter pw = new PrintWriter(new FileWriter(csv, true))) {
                pw.printf("\"%s\",\"%s\",%s,%s,%s,%s,%s,%s,%s,%s,%s,%s%n",
                    projectName, location,
                    length, width, thickness, employees,
                    areaLabel.getText(),
                    volumeCYLabel.getText(),
                    timeLabel.getText(),
                    matCostLabel.getText(),
                    laborCostLabel.getText(),
                    totalCostLabel.getText()
                );
            }
            JOptionPane.showMessageDialog(this,
                "Project saved to " + csv.getAbsolutePath(),
                "Saved", JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException ex) {
            showError("Could not write to Projects.csv:\n" + ex.getMessage());
        }
    }

    // ── CSV Load ─────────────────────────────────────────────────────────────

    private void loadProject() {
        try {
            ensureCSVExists();                          // create file + header if missing
        } catch (IOException ex) {
            showError("Could not create Projects.csv:\n" + ex.getMessage());
            return;
        }

        File csv = new File(CSV_FILE);

        List<String[]> projects = new ArrayList<>();
        List<String>   labels   = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(csv))) {
            String line = br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] cols = parseCSVLine(line);
                if (cols.length >= 12) {
                    projects.add(cols);
                    // Display: "Project Name — Location"
                    labels.add(cols[0] + (cols[1].isEmpty() ? "" : "  —  " + cols[1]));
                }
            }
        } catch (IOException ex) {
            showError("Could not read Projects.csv:\n" + ex.getMessage());
            return;
        }

        if (projects.isEmpty()) {
            showError("No saved projects found in Projects.csv.");
            return;
        }

        String chosen = (String) JOptionPane.showInputDialog(
            this,
            "Select a project to load:",
            "Load Project",
            JOptionPane.PLAIN_MESSAGE,
            null,
            labels.toArray(),
            labels.get(0)
        );

        if (chosen == null) return; // cancelled

        int idx = labels.indexOf(chosen);
        String[] row = projects.get(idx);

        // Populate inputs
        projectNameField.setText(row[0]);
        locationField.setText(row[1]);
        lengthField.setText(row[2]);
        widthField.setText(row[3]);
        thicknessField.setText(row[4]);
        employeesField.setText(row[5]);

        // Populate results
        areaLabel.setText(row[6]);
        volumeCYLabel.setText(row[7]);
        timeLabel.setText(row[8]);
        matCostLabel.setText(row[9]);
        laborCostLabel.setText(row[10]);
        totalCostLabel.setText(row[11]);
    }

    /** Minimal CSV parser that handles double-quoted fields. */
    private String[] parseCSVLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                fields.add(sb.toString());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        fields.add(sb.toString());
        return fields.toArray(new String[0]);
    }

    private void resetFields() {
        projectNameField.setText("");
        locationField.setText("");
        lengthField.setText("");
        widthField.setText("");
        thicknessField.setText("");
        employeesField.setText("");
        areaLabel.setText("—");
        volumeCYLabel.setText("—");
        timeLabel.setText("—");
        matCostLabel.setText("—");
        laborCostLabel.setText("—");
        totalCostLabel.setText("—");
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // ── Entry point ───────────────────────────────────────────────────────────

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ignored) {}
            new ConcretePadEstimator().setVisible(true);
        });
    }
}