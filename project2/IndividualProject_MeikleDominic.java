import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;

public class IndividualProject_MeikleDominic extends JFrame {

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
    private JLabel discountBadgeLabel;   // e.g. "Large Volume  –15%"
    private JLabel discountedTotalLabel; // final price after discount
    private JLabel reinforcementLabel;
    private JLabel manpowerLabel;  // Number of employees
    private JLabel workHoursLabel; // Total work hours

    private static final String CSV_FILE = "Projects.csv";
    private static final String HOURS_CSV_FILE = "Hours.csv";
    private static final String CSV_HEADER =
        "Project Name,Location,Length (ft),Width (ft),Thickness (in),Employees,Area,Volume (CY),Manpower,Work Hours,Material Cost,Labor Cost,Reinforcement,Total Cost,Discount Badge,Discounted Total";
    private static final String HOURS_CSV_HEADER = "Project Name,Manpower,Work Hours";

    // ── Discount rules (name, threshold, unit, discountPct) ──────────────────
    // Evaluated in order; only the single highest-pct match is applied.
    private static final Object[][] DISCOUNT_RULES = {
        // { display name,  threshold,  "cy"/"sqft"/"emp"/"in",  pct }
        { "Large Volume (≥50 CY)",     50.0,  "cy",    15.0 },
        { "Large Area (≥2000 sq ft)", 2000.0, "sqft",  10.0 },
        { "Medium Volume (≥20 CY)",    20.0,  "cy",     8.0 },
        { "Thick Slab (≥8 in)",         8.0,  "in",     7.0 },
        { "Big Crew (≥6 employees)",    6.0,  "emp",    5.0 },
    };

    // ── Color palette (shared across the whole app) ───────────────────────────
    private static final Color BG         = new Color(28, 32, 40);
    private static final Color PANEL      = new Color(38, 43, 54);
    private static final Color ACCENT     = new Color(224,142,22);
    private static final Color TEXT_LIGHT = new Color(232, 158, 44);
    private static final Color TEXT_DIM   = new Color(209, 126, 0);
    private static final Color FIELD_BG   = new Color(22, 26, 33);
    private static final Color SAVE_GREEN = new Color(40, 167, 80);
    private static final Color LOAD_BLUE  = new Color(30, 120, 200);
    private static final Color DEL_RED    = new Color(190, 50, 50);
    private static final Color BORDER_COL = new Color(55, 62, 75);
    private static final Color DISC_GOLD  = new Color(220, 170, 40);   // discount highlight

    public IndividualProject_MeikleDominic() {
        setTitle("Concrete Pour Cost Estimator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(200, 100));
        setResizable(false);

        getContentPane().setBackground(BG);

        // ===== HEADER =====
        JPanel header = new JPanel();
        header.setBackground(BG);
        header.setBorder(new EmptyBorder(10, 28, 8, 28));

        JLabel title = new JLabel("CONCRETE COST ESTIMATOR");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(ACCENT);
        header.add(title);

        // ===== PROJECT INFO PANEL =====
        JPanel inputPanel2 = createCard(PANEL, ACCENT, "PROJECT INFO");
        inputPanel2.setLayout(new BoxLayout(inputPanel2, BoxLayout.Y_AXIS));
        projectNameField = createField(FIELD_BG, TEXT_LIGHT);
        locationField = createField(FIELD_BG, TEXT_LIGHT);

        inputPanel2.add(Box.createVerticalStrut(6));
        addInputRow(inputPanel2, "Project Name:", projectNameField, TEXT_DIM, TEXT_LIGHT);
        addInputRow(inputPanel2, "Location:",     locationField,    TEXT_DIM, TEXT_LIGHT);

        // Save / Load / Delete buttons row inside PROJECT INFO
        JButton saveBtn = new JButton("SAVE PROJECT");
        styleSmallBtn(saveBtn, SAVE_GREEN, BG);
        saveBtn.addActionListener(e -> saveProject());

        JButton loadBtn = new JButton("LOAD PROJECT");
        styleSmallBtn(loadBtn, LOAD_BLUE, Color.WHITE);
        loadBtn.addActionListener(e -> loadProject());

        JButton deleteBtn = new JButton("DELETE PROJECT");
        styleSmallBtn(deleteBtn, DEL_RED, Color.WHITE);
        deleteBtn.addActionListener(e -> deleteProject());

        JPanel saveBtnRow = new JPanel(new GridLayout(1, 3, 8, 0));
        saveBtnRow.setOpaque(false);
        saveBtnRow.setBorder(new EmptyBorder(6, 14, 12, 14));
        saveBtnRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        saveBtnRow.add(saveBtn);
        saveBtnRow.add(loadBtn);
        saveBtnRow.add(deleteBtn);
        inputPanel2.add(saveBtnRow);

        JPanel projectWrapper = new JPanel(new BorderLayout());
        projectWrapper.setBackground(BG);
        projectWrapper.setBorder(new EmptyBorder(6, 16, 6, 16));
        projectWrapper.add(inputPanel2, BorderLayout.CENTER);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BG);
        topPanel.add(header, BorderLayout.NORTH);
        topPanel.add(projectWrapper, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // ===== MAIN BODY =====
        JPanel body = new JPanel(new GridLayout(1, 2, 12, 0));
        body.setBackground(BG);
        body.setBorder(new EmptyBorder(10, 16, 0, 16));

        // --- INPUT PANEL ---
        JPanel inputPanel = createCard(PANEL, ACCENT, "INPUTS");
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));

        lengthField    = createField(FIELD_BG, TEXT_LIGHT);
        widthField     = createField(FIELD_BG, TEXT_LIGHT);
        thicknessField = createField(FIELD_BG, TEXT_LIGHT);
        employeesField = createField(FIELD_BG, TEXT_LIGHT);

        inputPanel.add(Box.createVerticalStrut(6));
        addInputRow(inputPanel, "Length (ft):",    lengthField,    TEXT_DIM, TEXT_LIGHT);
        addInputRow(inputPanel, "Width (ft):",     widthField,     TEXT_DIM, TEXT_LIGHT);
        addInputRow(inputPanel, "Thickness (in):", thicknessField, TEXT_DIM, TEXT_LIGHT);
        addInputRow(inputPanel, "# of Employees:", employeesField, TEXT_DIM, TEXT_LIGHT);
        inputPanel.add(Box.createVerticalStrut(14));

        // Calculate button
        JButton calcBtn = new JButton("CALCULATE");
        calcBtn.setFont(new Font("Arial", Font.BOLD, 13));
        calcBtn.setBackground(ACCENT);
        calcBtn.setForeground(new Color(28, 32, 40));
        calcBtn.setFocusPainted(false);
        calcBtn.setBorder(new EmptyBorder(10, 0, 10, 0));
        calcBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        calcBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        calcBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        calcBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { calcBtn.setBackground(ACCENT.brighter()); }
            public void mouseExited(MouseEvent e)  { calcBtn.setBackground(ACCENT); }
        });
        calcBtn.addActionListener(e -> calculate());

        JPanel btnWrapper = new JPanel(new BorderLayout());
        btnWrapper.setOpaque(false);
        btnWrapper.setBorder(new EmptyBorder(0, 14, 14, 14));
        btnWrapper.add(calcBtn, BorderLayout.CENTER);
        inputPanel.add(btnWrapper);

        // --- OUTPUT PANEL ---
        JPanel outputPanel = createCard(PANEL, ACCENT, "RESULTS");
        outputPanel.setLayout(new BoxLayout(outputPanel, BoxLayout.Y_AXIS));

        areaLabel     = createResultLabel(TEXT_DIM);
        volumeCYLabel = createResultLabel(TEXT_DIM);
        manpowerLabel = createResultLabel(TEXT_DIM);
        workHoursLabel = createResultLabel(TEXT_DIM);
        timeLabel     = createResultLabel(TEXT_DIM);
        matCostLabel  = createResultLabel(TEXT_DIM);
        laborCostLabel= createResultLabel(TEXT_DIM);
        reinforcementLabel = createResultLabel(TEXT_DIM);
        totalCostLabel= createResultLabel(ACCENT);
        totalCostLabel.setFont(new Font("Arial", Font.BOLD, 13));

        outputPanel.add(Box.createVerticalStrut(6));
        addResultRow(outputPanel, "Area:",            areaLabel,      TEXT_DIM, TEXT_LIGHT);
        addResultRow(outputPanel, "Volume (CY+10%):", volumeCYLabel,  TEXT_DIM, TEXT_LIGHT);
        addResultRow(outputPanel, "Manpower:",        manpowerLabel,  TEXT_DIM, TEXT_LIGHT);
        addResultRow(outputPanel, "Work Hours:",      workHoursLabel, TEXT_DIM, TEXT_LIGHT);
        addResultRow(outputPanel, "Material Cost:",   matCostLabel,   TEXT_DIM, TEXT_LIGHT);
        addResultRow(outputPanel, "Labor Cost:",      laborCostLabel, TEXT_DIM, TEXT_LIGHT);
        addResultRow(outputPanel, "Reinforcement:",      reinforcementLabel, TEXT_DIM, TEXT_LIGHT);


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

        addResultRow(outputPanel, "TOTAL COST:", totalCostLabel, ACCENT, ACCENT);

        // ── Discount section ─────────────────────────────────────────────────
        JSeparator sep2 = new JSeparator();
        sep2.setForeground(new Color(60, 68, 82));
        sep2.setBackground(new Color(60, 68, 82));
        sep2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        JPanel sepWrap2 = new JPanel(new BorderLayout());
        sepWrap2.setOpaque(false);
        sepWrap2.setBorder(new EmptyBorder(4, 14, 4, 14));
        sepWrap2.add(sep2);
        outputPanel.add(sepWrap2);

        discountBadgeLabel   = createResultLabel(DISC_GOLD);
        discountBadgeLabel.setFont(new Font("Arial", Font.BOLD, 11));
        discountedTotalLabel = createResultLabel(DISC_GOLD);
        discountedTotalLabel.setFont(new Font("Arial", Font.BOLD, 14));

        addResultRow(outputPanel, "Discount:",       discountBadgeLabel,   TEXT_DIM,  DISC_GOLD);
        addResultRow(outputPanel, "AFTER DISCOUNT:", discountedTotalLabel, DISC_GOLD, DISC_GOLD);
        // ─────────────────────────────────────────────────────────────────────

        outputPanel.add(Box.createVerticalGlue());

        // Reset button
        JButton resetBtn = new JButton("RESET");
        resetBtn.setFont(new Font("Arial", Font.PLAIN, 11));
        resetBtn.setBackground(FIELD_BG);
        resetBtn.setForeground(TEXT_DIM);
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
        footer.setBackground(BG);
        footer.setBorder(new EmptyBorder(3, 0, 8, 0));
        JLabel note = new JLabel("Material: $130/CY  ·  Labor: $21/hr/employee  ·  Reinforcement + Leveling  ·  +10% volume buffer  ·  Auto-discount applied");
        note.setFont(new Font("Arial", Font.PLAIN, 10));
        note.setForeground(TEXT_DIM);
        footer.add(note);
        add(footer, BorderLayout.SOUTH);

        pack();
        setMinimumSize(new Dimension(680, 360));
        setLocationRelativeTo(null);

        // ── Create CSV with presets immediately on startup ────────────────────
        try {
            ensureCSVExists();
        } catch (IOException ex) {
            showError("Could not initialize Projects.csv:\n" + ex.getMessage());
        }
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
            new LineBorder(BORDER_COL, 1, true),
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

    // ── Themed dialog helpers ────────────────────────────────────────────────

    /**
     * Show a themed modal dialog with a message and optional title.
     * type: "info", "error", "confirm"
     * Returns true if the user clicked OK/Yes.
     */
    private boolean showThemedDialog(String message, String title, String type) {
        JDialog dialog = new JDialog(this, title, true);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(BG);
        dialog.setResizable(false);

        // Icon area
        String iconText;
        Color  iconColor;
        switch (type) {
            case "error":   iconText = "✕"; iconColor = DEL_RED;    break;
            case "confirm": iconText = "?"; iconColor = ACCENT;     break;
            default:        iconText = "✓"; iconColor = SAVE_GREEN; break;
        }

        JLabel icon = new JLabel(iconText, SwingConstants.CENTER);
        icon.setFont(new Font("Arial", Font.BOLD, 22));
        icon.setForeground(iconColor);
        icon.setBorder(new EmptyBorder(18, 24, 0, 16));

        JLabel msg = new JLabel("<html><body style='width:220px'>" +
                                message.replace("\n", "<br>") + "</body></html>");
        msg.setForeground(TEXT_LIGHT);
        msg.setFont(new Font("Arial", Font.PLAIN, 13));
        msg.setBorder(new EmptyBorder(18, 8, 18, 24));

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(BG);
        top.add(icon, BorderLayout.WEST);
        top.add(msg,  BorderLayout.CENTER);
        dialog.add(top, BorderLayout.CENTER);

        // Buttons
        final boolean[] result = {false};
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnRow.setBackground(new Color(38, 43, 54));
        btnRow.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COL));

        if ("confirm".equals(type)) {
            JButton yes = themedDialogBtn("YES", ACCENT, BG);
            JButton no  = themedDialogBtn("NO",  FIELD_BG, TEXT_DIM);
            yes.addActionListener(e -> { result[0] = true;  dialog.dispose(); });
            no .addActionListener(e -> { result[0] = false; dialog.dispose(); });
            btnRow.add(no);
            btnRow.add(yes);
        } else {
            JButton ok = themedDialogBtn("OK", ACCENT, BG);
            ok.addActionListener(e -> { result[0] = true; dialog.dispose(); });
            btnRow.add(ok);
        }

        dialog.add(btnRow, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        return result[0];
    }

    private JButton themedDialogBtn(String text, Color bg, Color fg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Arial", Font.BOLD, 11));
        b.setBackground(bg);
        b.setForeground(fg);
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(7, 18, 7, 18));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    /**
     * Show a themed list-selection dialog. Returns the selected index, or -1.
     */
    private int showThemedListDialog(String message, String title,
                                    String[] items, Color actionColor, String actionLabel) {
        JDialog dialog = new JDialog(this, title, true);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(BG);
        dialog.setResizable(false);

        JLabel msg = new JLabel(message);
        msg.setForeground(TEXT_DIM);
        msg.setFont(new Font("Arial", Font.PLAIN, 12));
        msg.setBorder(new EmptyBorder(14, 16, 8, 16));
        dialog.add(msg, BorderLayout.NORTH);

        // List
        DefaultListModel<String> model = new DefaultListModel<>();
        for (String item : items) model.addElement(item);
        JList<String> list = new JList<>(model);
        list.setBackground(FIELD_BG);
        list.setForeground(TEXT_LIGHT);
        list.setSelectionBackground(ACCENT);
        list.setSelectionForeground(BG);
        list.setFont(new Font("Arial", Font.PLAIN, 13));
        list.setBorder(new EmptyBorder(4, 6, 4, 6));
        list.setSelectedIndex(0);

        JScrollPane scroll = new JScrollPane(list);
        scroll.setBackground(FIELD_BG);
        scroll.setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(0, 16, 10, 16),
            new LineBorder(BORDER_COL, 1)
        ));
        scroll.getViewport().setBackground(FIELD_BG);
        dialog.add(scroll, BorderLayout.CENTER);

        final int[] result = {-1};
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnRow.setBackground(new Color(38, 43, 54));
        btnRow.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COL));

        JButton cancel = themedDialogBtn("CANCEL", FIELD_BG, TEXT_DIM);
        JButton action = themedDialogBtn(actionLabel, actionColor, Color.WHITE);

        cancel.addActionListener(e -> dialog.dispose());
        action.addActionListener(e -> {
            result[0] = list.getSelectedIndex();
            dialog.dispose();
        });

        btnRow.add(cancel);
        btnRow.add(action);
        dialog.add(btnRow, BorderLayout.SOUTH);

        dialog.setSize(380, 280);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        return result[0];
    }

    private void showError(String msg) {
        showThemedDialog(msg, "Error", "error");
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

            double rebar         = 1.30 * volCY;
            double mesh          = 0.20 * volCY;
            double reinforcement = rebar + mesh;


            double leveling    = 2 * area;
            double emplTimeEst = (volCY * 3.0) / employees;
            double pourTime    = (volCY * 5.0) / 60.0;
            double totTime     = emplTimeEst + pourTime + 1.0;

            double matCost   = volCY * 130.0;
            double laborCost = totTime * employees * 21.0;
            double total     = matCost + laborCost + reinforcement+ leveling;

            areaLabel.setText(String.format("%.2f sq ft", area));
            volumeCYLabel.setText(String.format("%.3f CY", volCY));
            manpowerLabel.setText(String.format("%d people", employees));
            workHoursLabel.setText(String.format("%.2f hrs", totTime));
            timeLabel.setText(String.format("%.2f hrs", totTime));
            matCostLabel.setText(String.format("$%.2f", matCost));
            laborCostLabel.setText(String.format("$%.2f", laborCost));
            reinforcementLabel.setText(String.format("$%.2f", reinforcement));
            totalCostLabel.setText(String.format("$%.2f", total));

            // ── Evaluate discount rules; pick highest matching pct ────────────
            String bestName = null;
            double bestPct  = 0.0;

            for (Object[] rule : DISCOUNT_RULES) {
                String ruleName  = (String) rule[0];
                double threshold = (Double) rule[1];
                String unit      = (String) rule[2];
                double pct       = (Double) rule[3];

                double value;
                switch (unit) {
                    case "cy":    value = volCY;     break;
                    case "sqft":  value = area;      break;
                    case "emp":   value = employees; break;
                    case "in":    value = thickness; break;
                    default:      value = 0;         break;
                }

                if (value >= threshold && pct > bestPct) {
                    bestPct  = pct;
                    bestName = ruleName;
                }
            }

            if (bestName != null) {
                double savings        = total * (bestPct / 100.0);
                double discountedTotal = total - savings;
                discountBadgeLabel.setText(
                    String.format("%s  (–%.0f%%)  saves $%.2f", bestName, bestPct, savings));
                discountedTotalLabel.setText(String.format("$%.2f", discountedTotal));
            } else {
                discountBadgeLabel.setText("No discount applies");
                discountedTotalLabel.setText("—");
            }
            // ─────────────────────────────────────────────────────────────────

        } catch (NumberFormatException ex) {
            showError("Please enter valid numbers in all fields.");
        }
    }

    /**
     * Returns a two-element array: [discountLabel, discountedTotal].
     * Used when saving so the CSV captures the discount info.
     */
    private String[] currentDiscountStrings() {
        return new String[]{ discountBadgeLabel.getText(), discountedTotalLabel.getText() };
    }

    // ── CSV helpers ──────────────────────────────────────────────────────────

    /**
     * Creates Projects.csv with the header row and preset sample projects
     * if it does not already exist.
     */
    private void ensureCSVExists() throws IOException {
        File file = new File(CSV_FILE);

        if (!file.exists()) {
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }

            try (PrintWriter pw = new PrintWriter(new FileWriter(file));
                PrintWriter manpowerPw = new PrintWriter(new FileWriter(new File(HOURS_CSV_FILE)))) {
                pw.println(CSV_HEADER);
                manpowerPw.println(HOURS_CSV_HEADER);

                // ── Preset sample projects ──────────────────────────────────
                // Each row: Name, Location, L, W, T(in), Emp,
                //           Area, VolCY, Time, MatCost, LaborCost, Total
                writePreset(pw, manpowerPw, "Small Garage Slab",   "Residential",  20, 20,  4, 2);
                writePreset(pw, manpowerPw, "Backyard Patio",       "Residential",  16, 12,  4, 2);
                writePreset(pw, manpowerPw, "Driveway",             "Residential",  40, 12,  6, 3);
                writePreset(pw, manpowerPw, "Warehouse Floor",      "Commercial",  100, 80,  6, 8);
                writePreset(pw, manpowerPw, "Parking Lot Section",  "Commercial",   60, 40,  5, 5);
                writePreset(pw, manpowerPw, "Loading Dock Apron",   "Industrial",   30, 20,  8, 4);
                writePreset(pw, manpowerPw, "Shop Floor",           "Industrial",   50, 40,  6, 6);
                writePreset(pw, manpowerPw, "Sidewalk Strip",       "Municipal",    80,  5,  4, 2);
                writePreset(pw, manpowerPw, "Pool Deck",            "Residential",  40, 15,  4, 3);
                writePreset(pw, manpowerPw, "Basketball Court",     "Recreational", 84, 50,  4, 6);
            }
        }

        // Ensure hours.csv exists (in case it was deleted or something)
        File hoursFile = new File(HOURS_CSV_FILE);
        if (!hoursFile.exists()) {
            try (PrintWriter pw = new PrintWriter(new FileWriter(hoursFile))) {
                pw.println(HOURS_CSV_HEADER);
            }
        }
    }

    /** Calculates derived fields and writes one preset CSV row. */
    private void writePreset(PrintWriter pw, PrintWriter manpowerPw,
                            String name, String location,
                            double length, double width,
                            double thicknessIn, int employees) {
        double area   = length * width;
            double vol    = area * (thicknessIn / 12.0);
            double volCY  = (vol * 1.10) / 27.0;

            double rebar         = 1.30 * volCY;
            double mesh          = 0.20 * volCY;
            double reinforcement = rebar + mesh;


            double leveling    = 2 * area;
            double emplTimeEst = (volCY * 3.0) / employees;
            double pourTime    = (volCY * 5.0) / 60.0;
            double totTime     = emplTimeEst + pourTime + 1.0;

            double matCost   = volCY * 130.0;
            double laborCost = totTime * employees * 21.0;
            double total     = matCost + laborCost + reinforcement+ leveling;

        // Evaluate discount rules
        String bestName = "No discount applies";
        double bestPct  = 0.0;
        for (Object[] rule : DISCOUNT_RULES) {
            String ruleName  = (String) rule[0];
            double threshold = (Double) rule[1];
            String unit      = (String) rule[2];
            double pct       = (Double) rule[3];
            double value;
            switch (unit) {
                case "cy":   value = volCY;     break;
                case "sqft": value = area;      break;
                case "emp":  value = employees; break;
                case "in":   value = thicknessIn; break;
                default:     value = 0;         break;
            }
            if (value >= threshold && pct > bestPct) {
                bestPct  = pct;
                bestName = ruleName;
            }
        }
        String discLabel;
        String discTotal;
        if (bestPct > 0) {
            double savings = total * (bestPct / 100.0);
            discLabel = String.format("%s  (--%.0f%%)  saves $%.2f", bestName, bestPct, savings);
            discTotal = String.format("$%.2f", total - savings);
        } else {
            discLabel = bestName;
            discTotal = "--";
        }

        pw.printf("\"%s\",\"%s\",%.0f,%.0f,%.0f,%d,%.2f sq ft,%.3f CY,%d,%.2f hrs,$%.2f,$%.2f,$%.2f,$%.2f,\"%s\",%s%n",
            name, location,
            length, width, thicknessIn, employees,
            area, volCY, employees, totTime,
            matCost, laborCost, reinforcement, total,
            discLabel, discTotal);

        // Also write to manpower CSV
        manpowerPw.printf("\"%s\",%d people,%.2f hrs%n", name, employees, totTime);
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
            ensureCSVExists();
            File csv = new File(CSV_FILE);
            String[] disc = currentDiscountStrings();
            try (PrintWriter pw = new PrintWriter(new FileWriter(csv, true))) {
                pw.printf("\"%s\",\"%s\",%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,\"%s\",%s%n",
                    projectName, location,
                    length, width, thickness, employees,
                    areaLabel.getText(),
                    volumeCYLabel.getText(),
                    manpowerLabel.getText(),
                    workHoursLabel.getText(),
                    matCostLabel.getText(),
                    laborCostLabel.getText(),
                    reinforcementLabel.getText(),
                    totalCostLabel.getText(),
                    disc[0], disc[1]
                );
            }

            // Also save to hours.csv
            File hoursCsv = new File(HOURS_CSV_FILE);
            try (PrintWriter pw = new PrintWriter(new FileWriter(hoursCsv, true))) {
                pw.printf("\"%s\",%s,%s%n",
                    projectName,
                    manpowerLabel.getText(),
                    workHoursLabel.getText()
                );
            }

            showThemedDialog("Project saved to:\n" + csv.getAbsolutePath() + "\nand\n" + hoursCsv.getAbsolutePath(),
                            "Saved", "info");

        } catch (IOException ex) {
            showError("Could not write to Projects.csv:\n" + ex.getMessage());
        }
    }

    // ── CSV Load ─────────────────────────────────────────────────────────────

    private void loadProject() {
        List<String[]> projects = readAllProjects();
        if (projects == null) return;

        if (projects.isEmpty()) {
            showError("No saved projects found in Projects.csv.");
            return;
        }

        String[] labels = buildLabels(projects);
        int idx = showThemedListDialog("Select a project to load:",
                                    "Load Project", labels, LOAD_BLUE, "LOAD");
        if (idx < 0) return;

        String[] row = projects.get(idx);
        projectNameField.setText(row[0]);
        locationField.setText(row[1]);
        lengthField.setText(row[2]);
        widthField.setText(row[3]);
        thicknessField.setText(row[4]);
        employeesField.setText(row[5]);
        areaLabel.setText(row[6]);
        volumeCYLabel.setText(row[7]);
        manpowerLabel.setText(row[8]);
        workHoursLabel.setText(row[9]);
        matCostLabel.setText(row[10]);
        laborCostLabel.setText(row[11]);
        
        // Determine if this is old format (no manpower/hours cols) or new format
        // Old: cols 0-7 are data, 8 is time/total info
        // New: cols 0-11 are data, 12 is reinforcement, 13 is total, 14-15 are discount
        if (row.length >= 16) {
            // New format with manpower and work hours columns
            reinforcementLabel.setText(row[12]);
            totalCostLabel.setText(row[13]);
            discountBadgeLabel.setText(row[14]);
            discountedTotalLabel.setText(row[15]);
        } else {
            // Old format - read total at end
            reinforcementLabel.setText(row.length > 11 ? row[11] : "—");
            totalCostLabel.setText(row.length > 12 ? row[12] : "—");
            discountBadgeLabel.setText(row.length > 13 ? row[13] : "—");
            discountedTotalLabel.setText(row.length > 14 ? row[14] : "—");
            // Calculate reinforcement from the loaded data
            calculateReinforcementFromLoaded();
        }
    }
    
    private void calculateReinforcementFromLoaded() {
        try {
            double volCY = Double.parseDouble(volumeCYLabel.getText().replace(" CY", "").trim());
            double rebar = 1.30 * volCY;
            double mesh = 0.20 * volCY;
            double reinforcement = rebar + mesh;
            reinforcementLabel.setText(String.format("$%.2f", reinforcement));
        } catch (Exception e) {
            reinforcementLabel.setText("—");
        }
    }

    // ── CSV Delete ────────────────────────────────────────────────────────────

    private void deleteProject() {
        List<String[]> projects = readAllProjects();
        if (projects == null) return;

        if (projects.isEmpty()) {
            showError("No saved projects found to delete.");
            return;
        }

        String[] labels = buildLabels(projects);
        int idx = showThemedListDialog("Select a project to delete:",
                                    "Delete Project", labels, DEL_RED, "DELETE");
        if (idx < 0) return;

        String name = projects.get(idx)[0];
        boolean confirmed = showThemedDialog(
            "Permanently delete \"" + name + "\"?\nThis cannot be undone.",
            "Confirm Delete", "confirm");
        if (!confirmed) return;

        projects.remove(idx);

        // Rewrite the file
        try {
            File csv = new File(CSV_FILE);
            try (PrintWriter pw = new PrintWriter(new FileWriter(csv, false))) {
                pw.println(CSV_HEADER);
                for (String[] row : projects) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < row.length; i++) {
                        if (i > 0) sb.append(',');
                        // Re-quote fields that were originally quoted (name and location)
                        if (i == 0 || i == 1) {
                            sb.append('"').append(row[i]).append('"');
                        } else {
                            sb.append(row[i]);
                        }
                    }
                    pw.println(sb.toString());
                }
            }
            showThemedDialog("\"" + name + "\" has been deleted.", "Deleted", "info");
        } catch (IOException ex) {
            showError("Could not update Projects.csv:\n" + ex.getMessage());
        }
    }

    // ── Shared CSV read ───────────────────────────────────────────────────────

    /** Reads all project rows. Returns null on I/O error (already shows dialog). */
    private List<String[]> readAllProjects() {
        try {
            ensureCSVExists();
        } catch (IOException ex) {
            showError("Could not create Projects.csv:\n" + ex.getMessage());
            return null;
        }

        File csv = new File(CSV_FILE);
        List<String[]> projects = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(csv))) {
            String line = br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] cols = parseCSVLine(line);
                if (cols.length >= 12) {
                    projects.add(cols);
                }
            }
        } catch (IOException ex) {
            showError("Could not read Projects.csv:\n" + ex.getMessage());
            return null;
        }
        return projects;
    }

    private String[] buildLabels(List<String[]> projects) {
        String[] labels = new String[projects.size()];
        for (int i = 0; i < projects.size(); i++) {
            String[] p = projects.get(i);
            labels[i] = p[0] + (p[1].isEmpty() ? "" : "  —  " + p[1]);
        }
        return labels;
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
        manpowerLabel.setText("—");
        workHoursLabel.setText("—");
        timeLabel.setText("—");
        matCostLabel.setText("—");
        laborCostLabel.setText("—");
        reinforcementLabel.setText("—");
        totalCostLabel.setText("—");
        discountBadgeLabel.setText("—");
        discountedTotalLabel.setText("—");
    }

    // ── Entry point ───────────────────────────────────────────────────────────

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ignored) {}
            new IndividualProject_MeikleDominic().setVisible(true);
        });
    }
}