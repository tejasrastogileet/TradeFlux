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
    'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJlcnlhc2hAZ21haWwuY29tIiwidXNlcklkIjo2LCJyb2xlIjoiVVNFUiIsImVtYWlsIjoiZXJ5YXNoQGdtYWlsLmNvbSIsImlhdCI6MTc4MDU5NzQ2MSwiZXhwIjoxNzgwNjAxMDYxfQ.tpRkdo5nRlOxs3jPhhYqrXuK7qFWmDGoYHulOmr7rkGzh8SjvH8zYc0rUw5EHyewWwUgEau-OpfLlZP5qRO6HA';

export default function () {

    const params = {

        headers: {

            Authorization: `Bearer ${token}`,
        },
    };

    const response = http.get(
        'http://host.docker.internal:9090/api/v1/wallet/my',
        params
    );

    check(response, {

        'status is 200': (r) => r.status === 200,

        'wallet returned': (r) =>
            r.json('walletId') !== null,
    });

}