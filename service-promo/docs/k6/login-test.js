import http from 'k6/http';
import { sleep, check, group } from 'k6';
import { SharedArray } from "k6/data";

import { htmlReport } from "https://raw.githubusercontent.com/benc-uk/k6-reporter/main/dist/bundle.js";
import { textSummary } from 'https://jslib.k6.io/k6-summary/0.0.1/index.js';

let env = new SharedArray("env", function () {
  const file = __ENV.CONFIG_FILE || 'env-default.json';
  return [JSON.parse(open(file))];
})[0];


export let options = {
  thresholds: {
    http_req_failed: ["rate<0.000001"], // 0.01 = 1%
    http_req_duration: ["p(95)<3000"]   // 95% of requests should be below 1000ms
  },
  stages: [
    { duration: '30s', target: 50 },
    // { duration: '10s', target: 100 },
    // { duration: '10s', target: 150 },
    // { duration: '10s', target: 200 },
    // { duration: '10s', target: 250 },
    // { duration: '10s', target: 300 },
    // { duration: '10s', target: 350 },
    // { duration: '10s', target: 400 },
    // { duration: '10s', target: 0 },
  ],
  // httpDebug: "full"
}

const GATEWAY = env.GATEWAY;
const PLAYER_DOMAIN = env.PLAYER_DOMAIN;
const PLAYER_PREFIX = env.PLAYER_PREFIX;
const PLAYER_PASSWORD = env.PLAYER_PASSWORD;

const PLAYER_GUID = PLAYER_DOMAIN + "/" + PLAYER_PREFIX + __VU;
const X_FORWARDED_FOR = '10.0.0.' + (Math.floor(Math.random() * 253 + 1));

// if (__VU === 1) {
console.log("GATEWAY = " + GATEWAY);
console.log("PLAYER_DOMAIN = " + PLAYER_DOMAIN);
console.log("PLAYER_GUID = " + PLAYER_GUID);
console.log("PLAYER_PASSWORD = " + PLAYER_PASSWORD);
console.log("X_FORWARDED_FOR = " + X_FORWARDED_FOR);
// }

export default function () {

  let PLAYER_ACCESS_TOKEN = '';

  group('login', function (context) {
    var payload = {
      grant_type: 'password',
      username: PLAYER_GUID,
      password: PLAYER_PASSWORD
    };
    var params = {
      headers: {
        'Authorization': 'Basic YWNtZTphY21lc2VjcmV0',
        'X-Forwarded-For': X_FORWARDED_FOR
      },
    };
    let res = http.post(GATEWAY + '/server-oauth2/oauth/token', payload, params);
    check(res, {
      'is status 200': (r) => r.status === 200,
    });
    console.log('Status: {}', res.status);
    // PLAYER_ACCESS_TOKEN = res.json().access_token;
  });

  sleep(3);

}

export function handleSummary(data) {
  return {
    'stdout': textSummary(data, { enableColors: true }) + '\n\n',
    'output/report.html': htmlReport(data),
    'output/report.json': JSON.stringify(data),
  }
}
