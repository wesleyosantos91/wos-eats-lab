import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
  vus: 5,
  duration: '30s',
};

const BASE_URL = 'http://localhost:8090';

export default function() {
  // Teste Kitchen API
  let kitchenResponse = http.get(`${BASE_URL}/v1/kitchens`);
  check(kitchenResponse, {
    'Kitchen API status is 200': (r) => r.status === 200,
    'Kitchen API response time < 1000ms': (r) => r.timings.duration < 1000,
  });

  // Teste Restaurant API
  let restaurantResponse = http.get(`${BASE_URL}/v1/restaurants`);
  check(restaurantResponse, {
    'Restaurant API status is 200': (r) => r.status === 200,
    'Restaurant API response time < 1000ms': (r) => r.timings.duration < 1000,
  });

  sleep(1);
}