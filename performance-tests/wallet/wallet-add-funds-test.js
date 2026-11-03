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
    'YOUR_JWT_TOKEN';

export default function () {

    const payload = JSON.stringify({

        amount: 100
    });

    const params = {

        headers: {

            Authorization: `Bearer ${token}`,
            'Content-Type': 'application/json'
        },
    };

    const response = http.post(
        'http://host.docker.internal:9090/api/v1/wallet/add-funds',
        payload,
        params
    );

    check(response, {

        'status is 200': (r) => r.status === 200,
    });

    sleep(1);
}