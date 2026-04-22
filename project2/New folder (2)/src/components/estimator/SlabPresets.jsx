import { Button } from "@/components/ui/button";

const TYPE_PRESETS = [
  { label: "Residential", slab_type: "residential_pad", thickness_in: 4, waste_percent: 10, concrete_price_per_yard: 150, num_employees: 2, labor_hours: 8, labor_rate_per_hour: 35 },
  { label: "Driveway", slab_type: "driveway", thickness_in: 5, waste_percent: 10, concrete_price_per_yard: 150, num_employees: 3, labor_hours: 10, labor_rate_per_hour: 38 },
  { label: "Warehouse", slab_type: "warehouse_slab", thickness_in: 6, waste_percent: 12, concrete_price_per_yard: 145, num_employees: 5, labor_hours: 16, labor_rate_per_hour: 40 },
  { label: "Sidewalk", slab_type: "sidewalk", thickness_in: 4, waste_percent: 8, concrete_price_per_yard: 150, num_employees: 2, labor_hours: 6, labor_rate_per_hour: 32 },
  { label: "Commercial", slab_type: "commercial_floor", thickness_in: 6, waste_percent: 12, concrete_price_per_yard: 155, num_employees: 4, labor_hours: 12, labor_rate_per_hour: 42 },
];

const SIZE_PRESETS = [
  { label: "10×10", length_ft: 10, width_ft: 10 },
  { label: "12×12", length_ft: 12, width_ft: 12 },
  { label: "20×20", length_ft: 20, width_ft: 20 },
  { label: "20×30", length_ft: 20, width_ft: 30 },
  { label: "24×24", length_ft: 24, width_ft: 24 },
  { label: "30×40", length_ft: 30, width_ft: 40 },
  { label: "40×60", length_ft: 40, width_ft: 60 },
  { label: "50×100", length_ft: 50, width_ft: 100 },
  { label: "100×100", length_ft: 100, width_ft: 100 },
];

export default function SlabPresets({ onSelect }) {
  return (
    <div className="space-y-3">
      <div>
        <p className="text-xs text-muted-foreground mb-2">Slab type presets:</p>
        <div className="flex flex-wrap gap-2">
          {TYPE_PRESETS.map((p) => (
            <Button
              key={p.label}
              type="button"
              variant="outline"
              size="sm"
              className="text-xs h-7 px-3 border-border hover:border-primary hover:text-primary transition-colors"
              onClick={() => onSelect(p)}
            >
              {p.label}
            </Button>
          ))}
        </div>
      </div>

      <div>
        <p className="text-xs text-muted-foreground mb-2">Common sizes (ft):</p>
        <div className="flex flex-wrap gap-2">
          {SIZE_PRESETS.map((p) => (
            <Button
              key={p.label}
              type="button"
              variant="outline"
              size="sm"
              className="text-xs h-7 px-3 border-border hover:border-primary hover:text-primary transition-colors"
              onClick={() => onSelect(p)}
            >
              {p.label}
            </Button>
          ))}
        </div>
      </div>
    </div>
  );
}