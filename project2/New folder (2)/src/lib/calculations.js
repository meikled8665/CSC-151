export function calculateEstimate(form) {
  const length = parseFloat(form.length_ft) || 0;
  const width = parseFloat(form.width_ft) || 0;
  const thicknessIn = parseFloat(form.thickness_in) || 0;
  const wastePct = parseFloat(form.waste_percent) || 0;
  const concretePricePerYard = parseFloat(form.concrete_price_per_yard) || 0;
  const numEmployees = parseInt(form.num_employees) || 0;
  const laborHours = parseFloat(form.labor_hours) || 0;
  const laborRate = parseFloat(form.labor_rate_per_hour) || 0;
  const discountType = form.discount_type || "none";
  const discountValue = parseFloat(form.discount_value) || 0;

  // Area
  const total_area_sqft = length * width;

  // Volume: (area * thickness_ft) / 27 cubic yards
  const thicknessFt = thicknessIn / 12;
  const baseVolume = (total_area_sqft * thicknessFt) / 27;
  const concrete_volume_yards = baseVolume * (1 + wastePct / 100);

  // Costs
  const concrete_cost = concrete_volume_yards * concretePricePerYard;
  const labor_cost = numEmployees * laborHours * laborRate;
  const subtotal = concrete_cost + labor_cost;

  // Discount
  let discount_amount = 0;
  if (discountType === "percentage") {
    discount_amount = subtotal * (discountValue / 100);
  } else if (discountType === "fixed") {
    discount_amount = Math.min(discountValue, subtotal);
  }

  const total_cost = subtotal - discount_amount;

  return {
    total_area_sqft: round(total_area_sqft, 2),
    concrete_volume_yards: round(concrete_volume_yards, 2),
    concrete_cost: round(concrete_cost, 2),
    labor_cost: round(labor_cost, 2),
    subtotal: round(subtotal, 2),
    discount_amount: round(discount_amount, 2),
    total_cost: round(total_cost, 2),
  };
}

function round(val, decimals) {
  return Math.round(val * Math.pow(10, decimals)) / Math.pow(10, decimals);
}

export function exportToCSV(estimates) {
  const headers = [
    "Project Name", "Location", "Client", "Slab Type", "Length (ft)", "Width (ft)",
    "Thickness (in)", "Area (sqft)", "Concrete (cu yd)", "Waste %",
    "Concrete Cost ($)", "Employees", "Labor Hours", "Labor Rate ($/hr)",
    "Labor Cost ($)", "Subtotal ($)", "Discount ($)", "Total ($)", "Status", "Notes"
  ];

  const rows = estimates.map((e) => [
    e.project_name || "",
    e.location || "",
    e.client_name || "",
    (e.slab_type || "").replace(/_/g, " "),
    e.length_ft || "",
    e.width_ft || "",
    e.thickness_in || "",
    e.total_area_sqft || "",
    e.concrete_volume_yards || "",
    e.waste_percent || "",
    e.concrete_cost || "",
    e.num_employees || "",
    e.labor_hours || "",
    e.labor_rate_per_hour || "",
    e.labor_cost || "",
    e.subtotal || "",
    e.discount_amount || "",
    e.total_cost || "",
    e.status || "",
    (e.notes || "").replace(/,/g, ";"),
  ]);

  const csv = [headers, ...rows].map((r) => r.join(",")).join("\n");
  const blob = new Blob([csv], { type: "text/csv" });
  const url = URL.createObjectURL(blob);
  const a = document.createElement("a");
  a.href = url;
  a.download = "concrete_estimates.csv";
  a.click();
  URL.revokeObjectURL(url);
}