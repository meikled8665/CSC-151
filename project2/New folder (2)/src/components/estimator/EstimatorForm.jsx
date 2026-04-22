import { Save } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Separator } from "@/components/ui/separator";
import FormSection from "./FormSection";
import SlabPresets from "./SlabPresets";

const SLAB_TYPES = [
  { value: "residential_pad", label: "Residential Pad" },
  { value: "driveway", label: "Driveway" },
  { value: "warehouse_slab", label: "Warehouse Slab" },
  { value: "commercial_floor", label: "Commercial Floor" },
  { value: "sidewalk", label: "Sidewalk" },
  { value: "custom", label: "Custom" },
];

export default function EstimatorForm({ formData, setFormData, onSave, saving, editingId }) {
  const set = (field) => (e) =>
    setFormData((p) => ({ ...p, [field]: e.target ? e.target.value : e }));

  return (
    <div className="space-y-6">
      {/* Project Info */}
      <FormSection title="Project Information" icon="clipboard">
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div className="space-y-2">
            <Label>Project Name *</Label>
            <Input placeholder="e.g. Smith Residential Pad" value={formData.project_name} onChange={set("project_name")} />
          </div>
          <div className="space-y-2">
            <Label>Client Name</Label>
            <Input placeholder="e.g. John Smith" value={formData.client_name} onChange={set("client_name")} />
          </div>
          <div className="sm:col-span-2 space-y-2">
            <Label>Location / Address</Label>
            <Input placeholder="e.g. 123 Main St, Austin TX" value={formData.location} onChange={set("location")} />
          </div>
        </div>
      </FormSection>

      {/* Slab Type */}
      <FormSection title="Slab Configuration" icon="layers">
        <SlabPresets onSelect={(preset) => setFormData((p) => ({ ...p, ...preset }))} />
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 mt-4">
          <div className="space-y-2">
            <Label>Slab Type</Label>
            <Select value={formData.slab_type} onValueChange={set("slab_type")}>
              <SelectTrigger><SelectValue /></SelectTrigger>
              <SelectContent>
                {SLAB_TYPES.map((t) => (
                  <SelectItem key={t.value} value={t.value}>{t.label}</SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>
          <div className="space-y-2">
            <Label>Thickness (inches)</Label>
            <Input type="number" min="1" max="24" step="0.5" value={formData.thickness_in} onChange={set("thickness_in")} />
          </div>
        </div>
      </FormSection>

      {/* Dimensions */}
      <FormSection title="Work Area Dimensions" icon="ruler">
        <div className="grid grid-cols-2 gap-4">
          <div className="space-y-2">
            <Label>Length (ft)</Label>
            <Input type="number" min="0" step="0.1" placeholder="0.00" value={formData.length_ft} onChange={set("length_ft")} />
          </div>
          <div className="space-y-2">
            <Label>Width (ft)</Label>
            <Input type="number" min="0" step="0.1" placeholder="0.00" value={formData.width_ft} onChange={set("width_ft")} />
          </div>
        </div>
      </FormSection>

      {/* Concrete */}
      <FormSection title="Concrete Material" icon="building">
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div className="space-y-2">
            <Label>Price per Cubic Yard ($)</Label>
            <Input type="number" min="0" step="0.01" value={formData.concrete_price_per_yard} onChange={set("concrete_price_per_yard")} />
          </div>
          <div className="space-y-2">
            <Label>Waste Percentage (%)</Label>
            <Input type="number" min="0" max="50" step="1" value={formData.waste_percent} onChange={set("waste_percent")} />
          </div>
        </div>
      </FormSection>

      {/* Labor */}
      <FormSection title="Labor Estimation" icon="users">
        <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
          <div className="space-y-2">
            <Label>No. of Employees</Label>
            <Input type="number" min="1" step="1" value={formData.num_employees} onChange={set("num_employees")} />
          </div>
          <div className="space-y-2">
            <Label>Total Labor Hours</Label>
            <Input type="number" min="0" step="0.5" value={formData.labor_hours} onChange={set("labor_hours")} />
          </div>
          <div className="space-y-2">
            <Label>Rate ($/hr/employee)</Label>
            <Input type="number" min="0" step="0.01" value={formData.labor_rate_per_hour} onChange={set("labor_rate_per_hour")} />
          </div>
        </div>
      </FormSection>

      {/* Discount */}
      <FormSection title="Discount" icon="tag">
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div className="space-y-2">
            <Label>Discount Type</Label>
            <Select value={formData.discount_type} onValueChange={set("discount_type")}>
              <SelectTrigger><SelectValue /></SelectTrigger>
              <SelectContent>
                <SelectItem value="none">No Discount</SelectItem>
                <SelectItem value="percentage">Percentage (%)</SelectItem>
                <SelectItem value="fixed">Fixed Amount ($)</SelectItem>
              </SelectContent>
            </Select>
          </div>
          {formData.discount_type !== "none" && (
            <div className="space-y-2">
              <Label>{formData.discount_type === "percentage" ? "Discount %" : "Discount Amount ($)"}</Label>
              <Input type="number" min="0" step="0.01" value={formData.discount_value} onChange={set("discount_value")} />
            </div>
          )}
        </div>
      </FormSection>

      {/* Notes */}
      <FormSection title="Notes" icon="pencil">
        <Textarea
          placeholder="Add any project notes or special requirements..."
          className="min-h-[80px]"
          value={formData.notes}
          onChange={set("notes")}
        />
      </FormSection>

      <Separator />

      <div className="flex items-center justify-between">
        <div className="flex items-center gap-3">
          <Label className="text-sm text-muted-foreground">Status</Label>
          <Select value={formData.status} onValueChange={set("status")}>
            <SelectTrigger className="w-32"><SelectValue /></SelectTrigger>
            <SelectContent>
              <SelectItem value="draft">Draft</SelectItem>
              <SelectItem value="finalized">Finalized</SelectItem>
            </SelectContent>
          </Select>
        </div>
        <Button onClick={onSave} disabled={saving} className="gap-2 font-semibold px-6">
          <Save className="w-4 h-4" />
          {saving ? "Saving..." : editingId ? "Update Estimate" : "Save Estimate"}
        </Button>
      </div>
    </div>
  );
}