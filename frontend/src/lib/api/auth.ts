import apiClient from './client';
import type { 
  LoginRequest, 
  RegisterRequest, 
  LoginResponse, 
  RegisterResponse,
  User
} from '@/lib/types';

export const authApi = {
  login: (data: LoginRequest) =>
    apiClient.post<LoginResponse>('/auth/login', data),

  register: (data: RegisterRequest) =>
    apiClient.post<RegisterResponse>('/auth/register', data),

  me: () =>
    apiClient.get<User>('/auth/me'),

  logout: () => {
    localStorage.removeItem('accessToken');
  },
};
