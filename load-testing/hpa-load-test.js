import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate } from 'k6/metrics';

// Custom metrics
const errorRate = new Rate('errors');

// Load test configuration
export const options = {
  stages: [
    // Warm up
    { duration: '30s', target: 5 },   // Ramp up to 5 users
    
    // Load increase to trigger HPA
    { duration: '60s', target: 20 },  // Ramp up to 20 users
    { duration: '60s', target: 50 },  // Ramp up to 50 users  
    { duration: '60s', target: 100 }, // Ramp up to 100 users (should trigger HPA)
    
    // Sustain high load
    { duration: '180s', target: 100 }, // Maintain 100 users for 3 minutes
    
    // Scale down test
    { duration: '60s', target: 20 },   // Scale back down
    { duration: '30s', target: 0 },    // Cool down
  ],
  
  thresholds: {
    http_req_duration: ['p(95)<2000'], // 95% of requests should be below 2s
    http_req_failed: ['rate<0.1'],     // Error rate should be below 10%
    errors: ['rate<0.1'],
  },
};

// Backend endpoints to test
const BASE_URL = 'http://51.250.66.103:8080';

const endpoints = [
  '/actuator/health',
  '/actuator/prometheus', 
  '/api/v1/catalog',
  '/api/v1/products',
];

export default function() {
  // Random endpoint selection
  const endpoint = endpoints[Math.floor(Math.random() * endpoints.length)];
  const url = `${BASE_URL}${endpoint}`;
  
  // Make HTTP request
  const response = http.get(url, {
    timeout: '10s',
    headers: {
      'User-Agent': 'K6-Load-Test/1.0',
      'Accept': 'application/json',
    },
  });
  
  // Check response
  const isSuccess = check(response, {
    'status is 200': (r) => r.status === 200,
    'response time < 2000ms': (r) => r.timings.duration < 2000,
    'has body': (r) => r.body && r.body.length > 0,
  });
  
  // Track errors
  errorRate.add(!isSuccess);
  
  // Log some responses for debugging
  if (__ITER < 5) {
    console.log(`Request to ${endpoint}: ${response.status} (${response.timings.duration}ms)`);
  }
  
  // Small delay between requests
  sleep(Math.random() * 2 + 0.5); // 0.5-2.5s random delay
}
