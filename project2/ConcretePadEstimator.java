import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
 
public class ConcretePadEstimator extends JFrame {
 
    // Input fields
    private JTextField lengthField;
    private JTextField widthField;
    private JTextField thicknessField;
    private JTextField employeesField;
 
    // Output labels
    private JLabel areaLabel;
    private JLabel volumeCYLabel;
    private JLabel timeLabel;
    private JLabel matCostLabel;
    private JLabel laborCostLabel;
    private JLabel totalCostLabel;
 
    public ConcretePadEstimator() {
        setTitle("Concrete Pour Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setResizable(false);
 
        // ---- Color palette ----
        Color bg         = new Color(28, 32, 40);
        Color panel      = new Color(38, 43, 54);
        Color accent     = new Color(255, 160, 50);
        Color textLight  = new Color(220, 225, 235);
        Color textDim    = new Color(140, 150, 165);
        Color fieldBg    = new Color(22, 26, 33);
 
        getContentPane().setBackground(bg);
 
        // ===== HEADER =====
        JPanel header = new JPanel();
        header.setBackground(bg);
        header.setBorder(new EmptyBorder(18, 20, 8, 20));
 
        JLabel title = new JLabel("CONCRETE POUR CALCULATOR");
        title.setFont(new Font("Courier New", Font.BOLD, 18));
        title.setForeground(accent);
        header.add(title);
        add(header, BorderLayout.NORTH);
 
        // ===== MAIN BODY =====
        JPanel body = new JPanel(new GridLayout(1, 2, 12, 0));
        body.setBackground(bg);
        body.setBorder(new EmptyBorder(0, 16, 0, 16));
 
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
        calcBtn.setFont(new Font("Courier New", Font.BOLD, 13));
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
        totalCostLabel.setFont(new Font("Courier New", Font.BOLD, 13));
 
        outputPanel.add(Box.createVerticalStrut(6));
        addResultRow(outputPanel, "Area:",          areaLabel,      textDim, textLight);
        addResultRow(outputPanel, "Volume (CY+10%):", volumeCYLabel, textDim, textLight);
        addResultRow(outputPanel, "Total Time (hr):", timeLabel,     textDim, textLight);
        addResultRow(outputPanel, "Material Cost:", matCostLabel,   textDim, textLight);
        addResultRow(outputPanel, "Labor Cost:",    laborCostLabel, textDim, textLight);
 
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
        resetBtn.setFont(new Font("Courier New", Font.PLAIN, 11));
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
        footer.setBorder(new EmptyBorder(6, 0, 14, 0));
        JLabel note = new JLabel("Material: $125/CY  ·  Labor: $21/hr/employee  ·  +10% volume buffer");
        note.setFont(new Font("Courier New", Font.PLAIN, 10));
        note.setForeground(textDim);
        footer.add(note);
        add(footer, BorderLayout.SOUTH);
 
        pack();
        setMinimumSize(new Dimension(600, 400));
        setLocationRelativeTo(null);
    }
 
    // ── Helpers ──────────────────────────────────────────────────────────────
 
    private JPanel createCard(Color bg, Color accent, String title) {
        JPanel card = new JPanel();
        card.setBackground(bg);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(55, 62, 75), 1, true),
            new EmptyBorder(0, 0, 0, 0)
        ));
 
        // Section header
        JLabel lbl = new JLabel("  " + title);
        lbl.setFont(new Font("Courier New", Font.BOLD, 11));
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
        f.setFont(new Font("Courier New", Font.PLAIN, 13));
        f.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(60, 68, 82), 1),
            new EmptyBorder(4, 8, 4, 8)
        ));
        return f;
    }
 
    private JLabel createResultLabel(Color fg) {
        JLabel l = new JLabel("—");
        l.setFont(new Font("Courier New", Font.PLAIN, 13));
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
        lbl.setFont(new Font("Courier New", Font.PLAIN, 12));
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
        lbl.setFont(new Font("Courier New", Font.PLAIN, 12));
        lbl.setForeground(labelColor);
        lbl.setPreferredSize(new Dimension(150, 22));
 
        row.add(lbl, BorderLayout.WEST);
        row.add(valueLabel, BorderLayout.CENTER);
        parent.add(row);
    }
 
    // ── Core logic (your formulas) ────────────────────────────────────────────
 
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
 
            // Size calculations
            double area   = length * width;
            double vol    = area * (thickness / 12.0);
 
            // Volume in cubic yards + 10% extra
            double volCY  = (vol + 0.10 * vol) / 27.0;
 
            // Time calculations
            double emplTimeEst = (volCY * 3.0) / employees;
            double pourTime    = (volCY * 5.0) / 60.0;
            double totTime     = emplTimeEst + pourTime + 1.0;
 
            // Cost calculations
            double matCost   = volCY * 125.0;
            double laborCost = totTime * employees * 21.0;
            double total     = matCost + laborCost;
 
            // Update result labels
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
 
    private void resetFields() {
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
        JOptionPane.showMessageDialog(this, msg, "Input Error", JOptionPane.ERROR_MESSAGE);
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