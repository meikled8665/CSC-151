import { useState, useEffect } from "react";
import { base44 } from "@/api/base44Client";
import EstimatorForm from "@/components/estimator/EstimatorForm";
import EstimateSummary from "@/components/estimator/EstimateSummary";
import EstimatesList from "@/components/estimator/EstimatesList";
import AppHeader from "@/components/estimator/AppHeader";
import { calculateEstimate } from "@/lib/calculations";
import { useToast } from "@/components/ui/use-toast";

export default function Estimator() {
  const [activeTab, setActiveTab] = useState("new");
  const [formData, setFormData] = useState(getDefaultForm());
  const [results, setResults] = useState(null);
  const [savedEstimates, setSavedEstimates] = useState([]);
  const [loadingList, setLoadingList] = useState(false);
  const [saving, setSaving] = useState(false);
  const [editingId, setEditingId] = useState(null);
  const { toast } = useToast();

  function getDefaultForm() {
    return {
      project_name: "",
      location: "",
      client_name: "",
      slab_type: "residential_pad",
      length_ft: "",
      width_ft: "",
      thickness_in: 4,
      waste_percent: 10,
      concrete_price_per_yard: 150,
      num_employees: 2,
      labor_hours: 8,
      labor_rate_per_hour: 35,
      discount_type: "none",
      discount_value: 0,
      notes: "",
      status: "draft",
    };
  }

  useEffect(() => {
    if (activeTab === "saved") loadEstimates();
  }, [activeTab]);

  useEffect(() => {
    if (formData.length_ft && formData.width_ft && formData.thickness_in) {
      setResults(calculateEstimate(formData));
    } else {
      setResults(null);
    }
  }, [formData]);

  async function loadEstimates() {
    setLoadingList(true);
    try {
      const list = await base44.entities.Estimate.list("-created_date", 50);
      setSavedEstimates(list);
    } finally {
      setLoadingList(false);
    }
  }

  async function handleSave() {
    if (!formData.project_name) {
      toast({ title: "Project name required", variant: "destructive" });
      return;
    }
    setSaving(true);
    try {
      const payload = results ? { ...formData, ...results } : formData;
      if (editingId) {
        await base44.entities.Estimate.update(editingId, payload);
        toast({ title: "Estimate updated!" });
      } else {
        await base44.entities.Estimate.create(payload);
        toast({ title: "Estimate saved!" });
      }
      setEditingId(null);
    } finally {
      setSaving(false);
    }
  }

  function handleLoadEstimate(est) {
    const { id, created_date, updated_date, created_by, ...rest } = est;
    setFormData(rest);
    setEditingId(id);
    setActiveTab("new");
  }

  async function handleDeleteEstimate(id) {
    await base44.entities.Estimate.delete(id);
    setSavedEstimates((prev) => prev.filter((e) => e.id !== id));
    toast({ title: "Estimate deleted" });
  }

  function handleNewEstimate() {
    setFormData(getDefaultForm());
    setEditingId(null);
    setResults(null);
    setActiveTab("new");
  }

  return (
    <div className="min-h-screen bg-background">
      <AppHeader
        activeTab={activeTab}
        setActiveTab={setActiveTab}
        onNew={handleNewEstimate}
      />
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {activeTab === "new" ? (
          <div className="grid grid-cols-1 xl:grid-cols-2 gap-8">
            <EstimatorForm
              formData={formData}
              setFormData={setFormData}
              onSave={handleSave}
              saving={saving}
              editingId={editingId}
            />
            <EstimateSummary results={results} formData={formData} />
          </div>
        ) : (
          <EstimatesList
            estimates={savedEstimates}
            loading={loadingList}
            onLoad={handleLoadEstimate}
            onDelete={handleDeleteEstimate}
          />
        )}
      </main>
    </div>
  );
}