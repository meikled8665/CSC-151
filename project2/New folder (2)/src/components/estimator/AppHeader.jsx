import { HardHat, Plus, FolderOpen } from "lucide-react";
import { Button } from "@/components/ui/button";

export default function AppHeader({ activeTab, setActiveTab, onNew }) {
  return (
    <header className="border-b border-border bg-card/80 backdrop-blur-sm sticky top-0 z-10">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          <div className="flex items-center gap-3">
            <div className="w-9 h-9 rounded-xl bg-primary flex items-center justify-center">
              <HardHat className="w-5 h-5 text-primary-foreground" />
            </div>
            <div>
              <h1 className="font-space font-700 text-lg text-foreground leading-none">
                ConcretePro
              </h1>
              <p className="text-xs text-muted-foreground">Pad Estimator</p>
            </div>
          </div>

          <nav className="flex items-center gap-2">
            <Button
              variant={activeTab === "new" ? "default" : "ghost"}
              size="sm"
              onClick={() => { onNew(); setActiveTab("new"); }}
              className="gap-2"
            >
              <Plus className="w-4 h-4" />
              <span className="hidden sm:inline">New Estimate</span>
            </Button>
            <Button
              variant={activeTab === "saved" ? "default" : "ghost"}
              size="sm"
              onClick={() => setActiveTab("saved")}
              className="gap-2"
            >
              <FolderOpen className="w-4 h-4" />
              <span className="hidden sm:inline">Saved Estimates</span>
            </Button>
          </nav>
        </div>
      </div>
    </header>
  );
}