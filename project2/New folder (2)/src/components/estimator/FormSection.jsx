import { Clipboard, Layers, Ruler, Building2, Users, Tag, Pencil } from "lucide-react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";

const icons = {
  clipboard: Clipboard,
  layers: Layers,
  ruler: Ruler,
  building: Building2,
  users: Users,
  tag: Tag,
  pencil: Pencil,
};

export default function FormSection({ title, icon, children }) {
  const Icon = icons[icon] || Clipboard;
  return (
    <Card className="border-border shadow-sm">
      <CardHeader className="pb-3 pt-4 px-5">
        <CardTitle className="flex items-center gap-2 text-sm font-semibold text-foreground">
          <div className="w-6 h-6 rounded-md bg-primary/10 flex items-center justify-center">
            <Icon className="w-3.5 h-3.5 text-primary" />
          </div>
          {title}
        </CardTitle>
      </CardHeader>
      <CardContent className="px-5 pb-5">{children}</CardContent>
    </Card>
  );
}