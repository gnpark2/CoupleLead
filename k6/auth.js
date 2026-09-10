import http from 'k6/http';
import { check, fail } from 'k6';

import {
  BASE_URL,
  TEST_EMAIL,
  TEST_PASSWORD,
} from './config.js';

export function login() {
  if (!TEST_EMAIL || !TEST_PASSWORD) {
    fail(
      'TEST_EMAIL and TEST_PASSWORD are required',
    );
  }

  const response = http.post(
    `${BASE_URL}/api/auth/login`,
    JSON.stringify({
      email: TEST_EMAIL,
      password: TEST_PASSWORD,
    }),
    {
      headers: {
        'Content-Type': 'application/json',
      },
      tags: {
        name: 'POST /api/auth/login',
      },
    },
  );

  const success = check(response, {
    'login status is 200': (r) =>
      r.status === 200,

    'login response success=true': (r) => {
      try {
        return r.json('success') === true;
      } catch (_) {
        return false;
      }
    },
  });

  if (!success) {
    console.error(
      `Login failed: ${response.status} ${response.body}`,
    );

    return null;
  }

  /*
   * 네 API 응답 구조에 맞춰야 함.
   *
   * accessToken이 data.accessToken에 있다면
   * 아래 그대로 사용.
   */
  const accessToken =
    response.json('data.accessToken');

  if (!accessToken) {
    console.error(
      'accessToken not found in login response',
    );

    return null;
  }

  return accessToken;
}