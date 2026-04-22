import { TrendingUp, Layers, Users, DollarSign, Tag, AlertCircle } from "lucide-react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Separator } from "@/components/ui/separator";
import { Badge } from "@/components/ui/badge";

const fmt = (n) =>
  n !== undefined && n !== null
    ? n.toLocaleString("en-US", { minimumFractionDigits: 2, maximumFractionDigits: 2 })
    : "—";

function StatRow({ icon: IconComponent, label, value, unit = "", highlight = false }) {
  const Icon = IconComponent;
  return (
    <div className={`flex items-center justify-between py-2 ${highlight ? "font-semibold" : ""}`}>
      <div className="flex items-center gap-2 text-sm text-muted-foreground">
        <Icon className="w-4 h-4 shrink-0" />
        <span>{label}</span>
      </div>
      <span className={`text-sm tabular-nums ${highlight ? "text-foreground text-base" : "text-foreground"}`}>
        {value}{unit && value !== "—" ? ` ${unit}` : ""}
      </span>
    </div>
  );
}

export default function EstimateSummary({ results, formData }) {
  const hasData = results && (results.total_area_sqft > 0);

  return (
    <div className="space-y-6 sticky top-24">
      {/* Area & Concrete */}
      <Card className="border-border shadow-sm">
        <CardHeader className="pb-3 pt-4 px-5">
          <CardTitle className="flex items-center gap-2 text-sm font-semibold">
            <div className="w-6 h-6 rounded-md bg-primary/10 flex items-center justify-center">
              <Layers className="w-3.5 h-3.5 text-primary" />
            </div>
            Concrete Requirements
          </CardTitle>
        </CardHeader>
        <CardContent className="px-5 pb-4 space-y-1">
          {!hasData ? (
            <div className="flex items-center gap-2 text-muted-foreground text-sm py-3">
              <AlertCircle className="w-4 h-4" />
              Enter dimensions to see calculations
            </div>
          ) : (
            <>
              <StatRow icon={Layers} label="Total Area" value={fmt(results.total_area_sqft)} unit="sq ft" />
              <StatRow icon={Layers} label="Slab Thickness" value={formData.thickness_in} unit="in" />
              <StatRow icon={Layers} label="Base Volume" value={fmt(
                results.concrete_volume_yards / (1 + (parseFloat(formData.waste_percent) || 0) / 100)
              )} unit="cu yd" />
              <StatRow icon={Layers} label={`+ ${formData.waste_percent}% Waste`} value={fmt(
                results.concrete_volume_yards - results.concrete_volume_yards / (1 + (parseFloat(formData.waste_percent) || 0) / 100)
              )} unit="cu yd" />
              <Separator className="my-2" />
              <StatRow icon={Layers} label="Total Concrete Needed" value={fmt(results.concrete_volume_yards)} unit="cu yd" highlight />
            </>
          )}
        </CardContent>
      </Card>

      {/* Cost Breakdown */}
      <Card className="border-border shadow-sm">
        <CardHeader className="pb-3 pt-4 px-5">
          <CardTitle className="flex items-center gap-2 text-sm font-semibold">
            <div className="w-6 h-6 rounded-md bg-primary/10 flex items-center justify-center">
              <DollarSign className="w-3.5 h-3.5 text-primary" />
            </div>
            Cost Breakdown
          </CardTitle>
        </CardHeader>
        <CardContent className="px-5 pb-4 space-y-1">
          {!hasData ? (
            <div className="flex items-center gap-2 text-muted-foreground text-sm py-3">
              <AlertCircle className="w-4 h-4" />
              Enter dimensions to see costs
            </div>
          ) : (
            <>
              <StatRow icon={Layers} label="Concrete Material" value={`$${fmt(results.concrete_cost)}`} />
              <StatRow icon={Users} label="Labor Cost" value={`$${fmt(results.labor_cost)}`} />
              <Separator className="my-2" />
              <StatRow icon={TrendingUp} label="Subtotal" value={`$${fmt(results.subtotal)}`} highlight />
              {results.discount_amount > 0 && (
                <StatRow icon={Tag} label="Discount" value={`-$${fmt(results.discount_amount)}`} />
              )}
            </>
          )}
        </CardContent>
      </Card>

      {/* Total */}
      <Card className="border-2 border-primary bg-primary/5 shadow-md">
        <CardContent className="px-5 py-5">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-muted-foreground font-medium">Total Project Cost</p>
              {results?.discount_amount > 0 && (
                <Badge variant="outline" className="mt-1 text-xs border-primary text-primary">
                  Discount Applied
                </Badge>
              )}
            </div>
            <div className="text-right">
              <p className="text-3xl font-space font-bold text-foreground tabular-nums">
                {hasData ? `$${fmt(results.total_cost)}` : "$—"}
              </p>
              {results?.discount_amount > 0 && (
                <p className="text-xs text-muted-foreground line-through">
                  ${fmt(results.subtotal)}
                </p>
              )}
            </div>
          </div>

          {hasData && (
            <div className="mt-4 grid grid-cols-3 gap-3 text-center">
              <div className="bg-background rounded-lg p-2">
                <p className="text-xs text-muted-foreground">Area</p>
                <p className="text-sm font-semibold">{fmt(results.total_area_sqft)} ft²</p>
              </div>
              <div className="bg-background rounded-lg p-2">
                <p className="text-xs text-muted-foreground">Concrete</p>
                <p className="text-sm font-semibold">{fmt(results.concrete_volume_yards)} yd³</p>
              </div>
              <div className="bg-background rounded-lg p-2">
                <p className="text-xs text-muted-foreground">$/ft²</p>
                <p className="text-sm font-semibold">${results.total_area_sqft > 0 ? fmt(results.total_cost / results.total_area_sqft) : "—"}</p>
              </div>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
}