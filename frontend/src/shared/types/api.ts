export interface ErrorResponse {
  status: number;
  error: string;
  message: string;
  timestamp: string;
}

export interface ApiError {
  status: number;
  code: string;
  message: string;
  timestamp?: string;
}
