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
    'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJlcnlhc2hAZ21haWwuY29tIiwidXNlcklkIjo2LCJyb2xlIjoiVVNFUiIsImVtYWlsIjoiZXJ5YXNoQGdtYWlsLmNvbSIsImlhdCI6MTc4MDM4MDMxNywiZXhwIjoxNzgwMzgzOTE3fQ.NYSMG3Q3K4-MrDYR4fg-vZbYvqBkwBpm_HMtJHe_9G46yw249EgH9evGt_FYTnR3t8Ey9qtCbTwjYZNmVripwg';

export default function () {

    const params = {

        headers: {

            Authorization: `Bearer ${token}`,
        },
    };

    const response = http.get(
        'http://host.docker.internal:9090/api/v1/wallet/transactions?page=0&size=10',
        params
    );

    check(response, {

        'status is 200': (r) => r.status === 200,

        'transactions returned': (r) =>
            r.json('transactions') !== null,
    });

}