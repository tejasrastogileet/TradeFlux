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
    'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJlcnlhc2hAZ21haWwuY29tIiwidXNlcklkIjo2LCJyb2xlIjoiVVNFUiIsImVtYWlsIjoiZXJ5YXNoQGdtYWlsLmNvbSIsImlhdCI6MTc4MDM3MDkwMiwiZXhwIjoxNzgwMzc0NTAyfQ.of8LK6fMNYKUfDAhpJ8ejlvDdlWAlkxZzn4s1bzhQxMdK1ZnFfLjpLkvcAXlRWOGU-P3tvDKvpsaWmLoy-V4mg';

export default function () {

    const params = {

        headers: {

            'Authorization': `Bearer ${token}`,
        },
    };

    const response = http.get(

        'http://host.docker.internal:9090/api/v1/portfolio/my?page=0&size=10',

        params
    );

    check(response, {

        'status is 200': (r) => r.status === 200,

        'holdings fetched': (r) =>
            r.json('holdings') !== null,
    });

}