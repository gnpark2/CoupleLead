import http from 'k6/http';
import {
  check,
  sleep,
} from 'k6';

import { login } from './auth.js';
import { BASE_URL } from './config.js';


/*
 * ==========================================
 * 부하 패턴
 * ==========================================
 *
 * 0 → 10명
 * 10명 유지
 * 10 → 20명
 * 20명 유지
 * 20 → 50명
 * 50명 유지
 * 다시 0
 */
export const options = {

  scenarios: {
    couplead_api_load: {
      executor: 'ramping-vus',

      startVUs: 0,

      stages: [
        {
          duration: '30s',
          target: 10,
        },
        {
          duration: '1m',
          target: 10,
        },

        {
          duration: '30s',
          target: 20,
        },
        {
          duration: '1m',
          target: 20,
        },

        {
          duration: '30s',
          target: 50,
        },
        {
          duration: '1m',
          target: 50,
        },

        {
          duration: '30s',
          target: 0,
        },
      ],

      gracefulRampDown: '10s',
    },
  },


  /*
   * ==========================================
   * 성능 기준
   * ==========================================
   */
  thresholds: {

    /*
     * 전체 요청 실패율 1% 미만
     */
    http_req_failed: [
      'rate<0.01',
    ],

    /*
     * 전체 API p95 500ms 미만
     */
    http_req_duration: [
      'p(95)<500',
    ],

    /*
     * 로그인은 상대적으로 무거울 수 있으므로
     * 우선 1초 기준
     */
    'http_req_duration{name:POST /api/auth/login}': [
      'p(95)<1000',
    ],

    /*
     * users/me p95
     */
    'http_req_duration{name:GET /api/users/me}': [
      'p(95)<500',
    ],

    /*
     * Media token
     */
    'http_req_duration{name:POST /api/media/token}': [
      'p(95)<500',
    ],
  },
};


export default function () {

  /*
   * ==========================================
   * 1. 로그인
   * ==========================================
   */
  const accessToken = login();

  if (!accessToken) {
    sleep(1);
    return;
  }


  const authHeaders = {
    headers: {
      Authorization:
        `Bearer ${accessToken}`,
    },
  };


  /*
   * ==========================================
   * 2. 현재 사용자 조회
   * ==========================================
   */
  const meResponse = http.get(
    `${BASE_URL}/api/users/me`,
    {
      ...authHeaders,

      tags: {
        name: 'GET /api/users/me',
      },
    },
  );


  check(meResponse, {
    'users/me status is 200': (r) =>
      r.status === 200,
  });


  sleep(0.5);


  /*
   * ==========================================
   * 3. LiveKit token 발급
   * ==========================================
   */
  const mediaResponse = http.post(
    `${BASE_URL}/api/media/token`,
    null,
    {
      ...authHeaders,

      tags: {
        name: 'POST /api/media/token',
      },
    },
  );


  check(mediaResponse, {
    'media token status is 200': (r) =>
      r.status === 200,

    'media token success': (r) => {
      try {
        return r.json('success') === true;
      } catch (_) {
        return false;
      }
    },
  });


  /*
   * 실제 사용자 행동 간격 흉내
   */
  sleep(1);
}