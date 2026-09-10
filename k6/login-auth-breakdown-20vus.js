import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  vus: 20,
  duration: '2m',

  discardResponseBodies: true,

  thresholds: {
    http_req_failed: [
      'rate<0.01',
    ],

    http_req_duration: [
      'p(95)<500',
    ],
  },

  summaryTrendStats: [
    'avg',
    'min',
    'med',
    'max',
    'p(90)',
    'p(95)',
    'p(99)',
  ],
};

const BASE_URL =
  __ENV.BASE_URL || 'https://api.couplead.app';

const TEST_EMAIL = __ENV.TEST_EMAIL;
const TEST_PASSWORD = __ENV.TEST_PASSWORD;

export function setup() {
  if (!TEST_EMAIL || !TEST_PASSWORD) {
    throw new Error(
      'TEST_EMAIL과 TEST_PASSWORD를 설정해야 합니다.',
    );
  }
}

export default function () {
  const payload = JSON.stringify({
    email: TEST_EMAIL,
    password: TEST_PASSWORD,
  });

  const response = http.post(
    `${BASE_URL}/api/auth/login`,
    payload,
    {
      headers: {
        'Content-Type': 'application/json',
      },

      tags: {
        name: 'POST /api/auth/login',
        test_type: 'auth_breakdown',
      },
    },
  );

  check(response, {
    'login status is 200': (res) =>
      res.status === 200,

    'response time < 500ms': (res) =>
      res.timings.duration < 500,
  });

  sleep(1);
}