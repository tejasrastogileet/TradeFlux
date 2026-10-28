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
    'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJlcnlhc2hAZ21haWwuY29tIiwidXNlcklkIjo2LCJyb2xlIjoiVVNFUiIsImVtYWlsIjoiZXJ5YXNoQGdtYWlsLmNvbSIsImlhdCI6MTc3OTc3MjA4NSwiZXhwIjoxNzc5Nzc1Njg1fQ.t8UhzWnSSChFmDtzO2zZuRuL19ewTrLrhqnvNuQSJwMKWiuyIYWT9n926dLoRbHYvf9WEamDVZSqhMYFtX-9hQ';

export default function () {

    const params = {

        headers: {

            'Authorization': `Bearer ${token}`,
        },
    };

    const response = http.get(

        'http://host.docker.internal:9090/api/v1/users/me',

        params
    );

    check(response, {

        'status is 200': (r) => r.status === 200,

        'profile fetched': (r) =>
            r.json('email') !== null,
    });

    sleep(1);
}