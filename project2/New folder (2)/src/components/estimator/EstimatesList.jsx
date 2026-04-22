import { Trash2, FolderOpen, Download, MapPin, Calendar, DollarSign } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Card, CardContent } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { exportToCSV } from "@/lib/calculations";
import { format } from "date-fns";

const fmt = (n) =>
  n !== undefined && n !== null
    ? n.toLocaleString("en-US", { minimumFractionDigits: 2, maximumFractionDigits: 2 })
    : "0.00";

export default function EstimatesList({ estimates, loading, onLoad, onDelete }) {
  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <div>
          <h2 className="text-xl font-space font-semibold">Saved Estimates</h2>
          <p className="text-sm text-muted-foreground mt-0.5">{estimates.length} estimate{estimates.length !== 1 ? "s" : ""} saved</p>
        </div>
        {estimates.length > 0 && (
          <Button variant="outline" size="sm" className="gap-2" onClick={() => exportToCSV(estimates)}>
            <Download className="w-4 h-4" />
            Export All CSV
          </Button>
        )}
      </div>

      {loading ? (
        <div className="space-y-3">
          {[1, 2, 3].map((i) => (
            <Skeleton key={i} className="h-28 w-full rounded-xl" />
          ))}
        </div>
      ) : estimates.length === 0 ? (
        <Card className="border-dashed">
          <CardContent className="flex flex-col items-center justify-center py-16 text-center">
            <FolderOpen className="w-10 h-10 text-muted-foreground mb-3" />
            <p className="text-muted-foreground font-medium">No estimates saved yet</p>
            <p className="text-sm text-muted-foreground mt-1">Create a new estimate and save it to see it here.</p>
          </CardContent>
        </Card>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-4">
          {estimates.map((est) => (
            <Card key={est.id} className="border-border hover:border-primary/40 hover:shadow-md transition-all cursor-pointer group">
              <CardContent className="p-5">
                <div className="flex items-start justify-between mb-3">
                  <div className="flex-1 min-w-0">
                    <h3 className="font-semibold text-foreground truncate group-hover:text-primary transition-colors">
                      {est.project_name}
                    </h3>
                    {est.client_name && (
                      <p className="text-xs text-muted-foreground mt-0.5">{est.client_name}</p>
                    )}
                  </div>
                  <Badge
                    variant={est.status === "finalized" ? "default" : "outline"}
                    className="ml-2 shrink-0 text-xs"
                  >
                    {est.status || "draft"}
                  </Badge>
                </div>

                <div className="space-y-1.5 mb-4">
                  {est.location && (
                    <div className="flex items-center gap-1.5 text-xs text-muted-foreground">
                      <MapPin className="w-3 h-3" />
                      <span className="truncate">{est.location}</span>
                    </div>
                  )}
                  <div className="flex items-center gap-1.5 text-xs text-muted-foreground">
                    <Calendar className="w-3 h-3" />
                    <span>{est.created_date ? format(new Date(est.created_date), "MMM d, yyyy") : "—"}</span>
                  </div>
                  <div className="flex items-center gap-1.5 text-xs text-muted-foreground">
                    <DollarSign className="w-3 h-3" />
                    <span>{est.total_area_sqft ? `${est.total_area_sqft} sq ft` : "—"} · {est.concrete_volume_yards ? `${est.concrete_volume_yards} cu yd` : "—"}</span>
                  </div>
                </div>

                <div className="flex items-center justify-between pt-3 border-t border-border">
                  <p className="text-lg font-space font-bold text-foreground">
                    ${fmt(est.total_cost)}
                  </p>
                  <div className="flex gap-2">
                    <Button
                      size="sm"
                      variant="outline"
                      className="h-7 px-3 text-xs"
                      onClick={() => onLoad(est)}
                    >
                      Edit
                    </Button>
                    <Button
                      size="sm"
                      variant="ghost"
                      className="h-7 w-7 p-0 text-muted-foreground hover:text-destructive"
                      onClick={() => onDelete(est.id)}
                    >
                      <Trash2 className="w-3.5 h-3.5" />
                    </Button>
                  </div>
                </div>
              </CardContent>
            </Card>
          ))}
        </div>
      )}
    </div>
  );
}