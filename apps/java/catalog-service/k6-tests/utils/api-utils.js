// K6 Utility Functions for API Testing
import http from 'k6/http';
import { check } from 'k6';

export const BASE_URL = 'http://localhost:8090';

export const API_ENDPOINTS = {
  kitchens: {
    base: '/v1/kitchens',
    byId: (id) => `/v1/kitchens/${id}`,
    create: '/v1/kitchens',
    update: (id) => `/v1/kitchens/${id}`,
    delete: (id) => `/v1/kitchens/${id}`
  },
  restaurants: {
    base: '/v1/restaurants',
    byId: (id) => `/v1/restaurants/${id}`,
    create: '/v1/restaurants',
    update: (id) => `/v1/restaurants/${id}`,
    delete: (id) => `/v1/restaurants/${id}`
  }
};

export const HTTP_HEADERS = {
  'Content-Type': 'application/json',
  'Accept': 'application/json'
};

// Kitchen API functions
export function createKitchen(kitchenData) {
  const response = http.post(
    `${BASE_URL}${API_ENDPOINTS.kitchens.create}`,
    JSON.stringify(kitchenData),
    { headers: HTTP_HEADERS }
  );
  
  return check(response, {
    'Kitchen created successfully': (r) => r.status === 201,
    'Kitchen response has ID': (r) => JSON.parse(r.body).id !== undefined,
    'Kitchen response time < 1s': (r) => r.timings.duration < 1000,
  });
}

export function getKitchen(id) {
  const response = http.get(`${BASE_URL}${API_ENDPOINTS.kitchens.byId(id)}`);
  
  return check(response, {
    'Kitchen retrieved successfully': (r) => r.status === 200,
    'Kitchen response has data': (r) => JSON.parse(r.body).name !== undefined,
    'Kitchen response time < 500ms': (r) => r.timings.duration < 500,
  });
}

export function getAllKitchens() {
  const response = http.get(`${BASE_URL}${API_ENDPOINTS.kitchens.base}`);
  
  return check(response, {
    'Kitchens list retrieved successfully': (r) => r.status === 200,
    'Kitchens list is array': (r) => Array.isArray(JSON.parse(r.body)),
    'Kitchens response time < 1s': (r) => r.timings.duration < 1000,
  });
}

export function updateKitchen(id, kitchenData) {
  const response = http.put(
    `${BASE_URL}${API_ENDPOINTS.kitchens.update(id)}`,
    JSON.stringify(kitchenData),
    { headers: HTTP_HEADERS }
  );
  
  return check(response, {
    'Kitchen updated successfully': (r) => r.status === 200,
    'Kitchen update response time < 1s': (r) => r.timings.duration < 1000,
  });
}

export function deleteKitchen(id) {
  const response = http.del(`${BASE_URL}${API_ENDPOINTS.kitchens.delete(id)}`);
  
  return check(response, {
    'Kitchen deleted successfully': (r) => r.status === 204,
    'Kitchen delete response time < 500ms': (r) => r.timings.duration < 500,
  });
}

// Restaurant API functions
export function createRestaurant(restaurantData) {
  const response = http.post(
    `${BASE_URL}${API_ENDPOINTS.restaurants.create}`,
    JSON.stringify(restaurantData),
    { headers: HTTP_HEADERS }
  );
  
  return check(response, {
    'Restaurant created successfully': (r) => r.status === 201,
    'Restaurant response has ID': (r) => JSON.parse(r.body).id !== undefined,
    'Restaurant response time < 1s': (r) => r.timings.duration < 1000,
  });
}

export function getRestaurant(id) {
  const response = http.get(`${BASE_URL}${API_ENDPOINTS.restaurants.byId(id)}`);
  
  return check(response, {
    'Restaurant retrieved successfully': (r) => r.status === 200,
    'Restaurant response has data': (r) => JSON.parse(r.body).name !== undefined,
    'Restaurant response time < 500ms': (r) => r.timings.duration < 500,
  });
}

export function getAllRestaurants() {
  const response = http.get(`${BASE_URL}${API_ENDPOINTS.restaurants.base}`);
  
  return check(response, {
    'Restaurants list retrieved successfully': (r) => r.status === 200,
    'Restaurants list is array': (r) => Array.isArray(JSON.parse(r.body)),
    'Restaurants response time < 1s': (r) => r.timings.duration < 1000,
  });
}

export function updateRestaurant(id, restaurantData) {
  const response = http.put(
    `${BASE_URL}${API_ENDPOINTS.restaurants.update(id)}`,
    JSON.stringify(restaurantData),
    { headers: HTTP_HEADERS }
  );
  
  return check(response, {
    'Restaurant updated successfully': (r) => r.status === 200,
    'Restaurant update response time < 1s': (r) => r.timings.duration < 1000,
  });
}

export function deleteRestaurant(id) {
  const response = http.del(`${BASE_URL}${API_ENDPOINTS.restaurants.delete(id)}`);
  
  return check(response, {
    'Restaurant deleted successfully': (r) => r.status === 204,
    'Restaurant delete response time < 500ms': (r) => r.timings.duration < 500,
  });
}

// Performance thresholds
export const PERFORMANCE_THRESHOLDS = {
  load: {
    http_req_duration: ['p(95)<1000'], // 95% of requests under 1s
    http_req_failed: ['rate<0.1'],     // Error rate under 10%
  },
  stress: {
    http_req_duration: ['p(95)<2000'], // 95% of requests under 2s
    http_req_failed: ['rate<0.2'],     // Error rate under 20%
  },
  spike: {
    http_req_duration: ['p(95)<3000'], // 95% of requests under 3s
    http_req_failed: ['rate<0.3'],     // Error rate under 30%
  }
};

// Common scenarios
export const LOAD_PROFILES = {
  light: {
    stages: [
      { duration: '2m', target: 10 },  // Ramp up
      { duration: '5m', target: 10 },  // Stay at 10 users
      { duration: '2m', target: 0 },   // Ramp down
    ]
  },
  moderate: {
    stages: [
      { duration: '3m', target: 50 },  // Ramp up
      { duration: '10m', target: 50 }, // Stay at 50 users
      { duration: '3m', target: 0 },   // Ramp down
    ]
  },
  heavy: {
    stages: [
      { duration: '5m', target: 100 }, // Ramp up
      { duration: '15m', target: 100 },// Stay at 100 users
      { duration: '5m', target: 0 },   // Ramp down
    ]
  },
  stress: {
    stages: [
      { duration: '2m', target: 50 },  // Ramp up
      { duration: '5m', target: 100 }, // Increase load
      { duration: '5m', target: 200 }, // High load
      { duration: '5m', target: 300 }, // Very high load
      { duration: '2m', target: 0 },   // Ramp down
    ]
  },
  spike: {
    stages: [
      { duration: '10s', target: 100 }, // Quick spike
      { duration: '1m', target: 100 },  // Maintain spike
      { duration: '10s', target: 0 },   // Quick drop
    ]
  }
};