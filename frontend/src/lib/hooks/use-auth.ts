import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { authApi } from '@/lib/api';
import { useAuthStore } from '@/stores/auth-store';
import { useRouter } from 'next/navigation';
import { toast } from 'sonner';
import type { LoginRequest, RegisterRequest } from '@/lib/types';

export function useAuth() {
  const { user, accessToken, setAuth, clearAuth } = useAuthStore();
  const router = useRouter();

  return {
    user,
    accessToken,
    isAuthenticated: !!accessToken && !!user,
    setAuth,
    clearAuth,
  };
}

export function useLogin() {
  const { setAuth } = useAuthStore();
  const router = useRouter();

  return useMutation({
    mutationFn: (data: LoginRequest) => authApi.login(data),
    onSuccess: (response) => {
      setAuth(response.data.user, response.data.accessToken);
      toast.success('Đăng nhập thành công!');
      router.push('/members');
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Đăng nhập thất bại');
    },
  });
}

export function useRegister() {
  const router = useRouter();

  return useMutation({
    mutationFn: (data: RegisterRequest) => authApi.register(data),
    onSuccess: (response) => {
      toast.success(response.data.message);
      router.push('/login');
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Đăng ký thất bại');
    },
  });
}

export function useLogout() {
  const { clearAuth } = useAuthStore();
  const router = useRouter();
  const queryClient = useQueryClient();

  return () => {
    authApi.logout();
    clearAuth();
    queryClient.clear();
    router.push('/login');
    toast.success('Đã đăng xuất');
  };
}

export function useCurrentUser() {
  const { user, accessToken } = useAuthStore();

  return useQuery({
    queryKey: ['auth', 'me'],
    queryFn: () => authApi.me().then((res) => res.data),
    enabled: !!accessToken,
    initialData: user || undefined,
    staleTime: 5 * 60 * 1000, // 5 minutes
  });
}
