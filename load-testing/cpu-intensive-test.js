import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate } from 'k6/metrics';

// Custom metrics
const errorRate = new Rate('errors');

// Aggressive load test to trigger CPU usage
export const options = {
  stages: [
    // Quick ramp up to trigger HPA faster
    { duration: '10s', target: 10 },   
    { duration: '20s', target: 50 },   
    { duration: '30s', target: 150 },  // High concurrent users
    { duration: '120s', target: 200 }, // Very high load for 2 minutes
    { duration: '60s', target: 50 },   // Scale down
    { duration: '10s', target: 0 },    // Stop
  ],
  
  thresholds: {
    http_req_duration: ['p(95)<5000'], // More lenient during high load
    http_req_failed: ['rate<0.2'],     // Allow some failures under extreme load
  },
};

const BASE_URL = 'http://51.250.66.103:8080';

// Focus on endpoints that might cause more CPU usage
const endpoints = [
  '/api/v1/catalog',      // Likely database queries
  '/api/v1/products',     // Likely database queries  
  '/actuator/prometheus', // Metrics collection (CPU intensive)
  '/actuator/health',     // Health checks
];

export default function() {
  // Make multiple rapid requests per iteration
  for (let i = 0; i < 3; i++) {
    const endpoint = endpoints[Math.floor(Math.random() * endpoints.length)];
    const url = `${BASE_URL}${endpoint}`;
    
    const response = http.get(url, {
      timeout: '5s',
      headers: {
        'User-Agent': 'K6-CPU-Intensive-Test/1.0',
        'Accept': 'application/json',
        'Connection': 'keep-alive',
      },
    });
    
    const isSuccess = check(response, {
      'status is 2xx or 3xx': (r) => r.status >= 200 && r.status < 400,
      'response time < 5000ms': (r) => r.timings.duration < 5000,
    });
    
    errorRate.add(!isSuccess);
    
    // Very short delay between requests in same iteration
    sleep(0.1);
  }
  
  // Short delay between iterations
  sleep(Math.random() * 0.5 + 0.1); // 0.1-0.6s delay
}
