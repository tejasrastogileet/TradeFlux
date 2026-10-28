import http from 'k6/http';

import { check, sleep } from 'k6';

export const options = {

    vus: 250,

    duration: '30s',

    thresholds: {

        http_req_failed: ['rate<0.01'],

        http_req_duration: ['p(95)<500'],
    },
};

const token =
    'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJlcnlhc2hAZ21haWwuY29tIiwidXNlcklkIjo2LCJyb2xlIjoiVVNFUiIsImVtYWlsIjoiZXJ5YXNoQGdtYWlsLmNvbSIsImlhdCI6MTc3OTc3MjY3MiwiZXhwIjoxNzc5Nzc2MjcyfQ.CnIBK-kRL8FXku5U7G1HZdit-FuQa_PMcg_WYQ4Ghu4SKs_Em7-C6C4e-vFW66BiZ0VVq6xb0AB5sAKVAZTS1w';

export default function () {

    const params = {

        headers: {

            'Authorization': `Bearer ${token}`,
        },
    };

    const response = http.get(

        'http://host.docker.internal:9090/api/v1/users/search?keyword=yash&page=0&size=10',

        params
    );

    check(response, {

        'status is 200': (r) => r.status === 200,

        'search returned': (r) =>
            r.json('content') !== null,
    });

    sleep(1);
}