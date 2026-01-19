'use client';

import { useState } from 'react';
import { useMembers } from '@/lib/hooks';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { UserCircle, Plus, Search } from 'lucide-react';
import Link from 'next/link';

export default function MembersPage() {
  const [search, setSearch] = useState('');
  const [page, setPage] = useState(0);
  const { data, isLoading, error } = useMembers({ page, size: 20, search: search || undefined });

  if (error) {
    return (
      <div className="flex items-center justify-center py-12">
        <Card className="max-w-md">
          <CardHeader>
            <CardTitle>Lỗi</CardTitle>
            <CardDescription>
              Không thể tải danh sách thành viên
            </CardDescription>
          </CardHeader>
          <CardContent>
            <p className="text-sm text-muted-foreground">
              {(error as any)?.response?.data?.message || 'Có lỗi xảy ra'}
            </p>
          </CardContent>
        </Card>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold">Thành viên</h1>
          <p className="text-muted-foreground">
            Quản lý danh sách thành viên trong gia phả
          </p>
        </div>
        <Button asChild>
          <Link href="/members/new">
            <Plus className="mr-2 h-4 w-4" />
            Thêm thành viên
          </Link>
        </Button>
      </div>

      <Card>
        <CardHeader>
          <div className="flex items-center gap-4">
            <div className="relative flex-1">
              <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
              <Input
                placeholder="Tìm kiếm theo tên..."
                value={search}
                onChange={(e) => setSearch(e.target.value)}
                className="pl-9"
              />
            </div>
          </div>
        </CardHeader>
        <CardContent>
          {isLoading ? (
            <div className="space-y-4">
              {[...Array(5)].map((_, i) => (
                <div key={i} className="h-20 animate-pulse rounded-lg bg-muted" />
              ))}
            </div>
          ) : data?.content && data.content.length > 0 ? (
            <div className="space-y-2">
              {data.content.map((member) => (
                <Link
                  key={member.id}
                  href={`/members/${member.id}`}
                  className="flex items-center gap-4 rounded-lg border p-4 transition-colors hover:bg-accent"
                >
                  <div className="flex h-12 w-12 items-center justify-center rounded-full bg-primary/10">
                    {member.avatarUrl ? (
                      <img
                        src={member.avatarUrl}
                        alt={member.fullName}
                        className="h-12 w-12 rounded-full object-cover"
                      />
                    ) : (
                      <UserCircle className="h-8 w-8 text-primary" />
                    )}
                  </div>
                  <div className="flex-1">
                    <h3 className="font-medium">{member.fullName}</h3>
                    <div className="flex gap-4 text-sm text-muted-foreground">
                      <span>Đời {member.generation}</span>
                      <span>{member.gender === 'MALE' ? 'Nam' : 'Nữ'}</span>
                      {member.birthDate && (
                        <span>Sinh {new Date(member.birthDate).getFullYear()}</span>
                      )}
                      {member.isDeceased && (
                        <span className="text-gray-500">Đã mất</span>
                      )}
                    </div>
                  </div>
                </Link>
              ))}
            </div>
          ) : (
            <div className="py-12 text-center">
              <UserCircle className="mx-auto h-12 w-12 text-muted-foreground" />
              <h3 className="mt-4 font-medium">Chưa có thành viên</h3>
              <p className="text-sm text-muted-foreground">
                Bắt đầu bằng cách thêm thành viên đầu tiên
              </p>
            </div>
          )}

          {data && data.totalPages > 1 && (
            <div className="mt-4 flex items-center justify-between">
              <Button
                variant="outline"
                onClick={() => setPage((p) => Math.max(0, p - 1))}
                disabled={page === 0}
              >
                Trang trước
              </Button>
              <span className="text-sm text-muted-foreground">
                Trang {page + 1} / {data.totalPages}
              </span>
              <Button
                variant="outline"
                onClick={() => setPage((p) => p + 1)}
                disabled={page >= data.totalPages - 1}
              >
                Trang sau
              </Button>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
}
