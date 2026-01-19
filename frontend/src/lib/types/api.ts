export interface PaginatedResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface ApiError {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
  details?: {
    field: string;
    rejectedValue: any;
    code: string;
  };
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  fullName: string;
}

export interface LoginResponse {
  accessToken: string;
  tokenType: string;
  expiresIn: number;
  user: import('./user').User;
}

export interface RegisterResponse {
  id: string;
  email: string;
  fullName: string;
  status: string;
  roles: any[];
  message: string;
}
