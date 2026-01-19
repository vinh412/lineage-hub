import { AuthGuard } from '@/components/layout/auth-guard';
import { Sidebar } from '@/components/layout/sidebar';

export default function DashboardLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <AuthGuard>
      <div className="flex h-screen">
        <Sidebar />
        <main className="flex-1 overflow-auto">
          <div className="container mx-auto p-6">{children}</div>
        </main>
      </div>
    </AuthGuard>
  );
}
