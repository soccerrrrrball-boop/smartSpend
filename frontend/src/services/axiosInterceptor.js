import axios from 'axios';
import AuthService from './auth.service';

// Response interceptor - handle 401 errors globally
axios.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    if (error.response && error.response.status === 401) {
      // Token is invalid or expired
      AuthService.logout_req();
      // Redirect to login page
      if (window.location.pathname !== '/auth/login' && window.location.pathname !== '/auth/register') {
        window.location.href = '/auth/login';
      }
    }
    return Promise.reject(error);
  }
);

// This file sets up global axios interceptors
// Import this file in App.js to activate the interceptors

