import http from 'k6/http';
import { check } from 'k6';

export const options = {

    vus: 250,

    duration: '30s',

    thresholds: {

        http_req_failed: ['rate<0.01'],

        http_req_duration: ['p(95)<500'],
    },
};

const token =
    'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJlcnlhc2hAZ21haWwuY29tIiwidXNlcklkIjo2LCJyb2xlIjoiVVNFUiIsImVtYWlsIjoiZXJ5YXNoQGdtYWlsLmNvbSIsImlhdCI6MTc4MDAyNzg0MywiZXhwIjoxNzgwMDMxNDQzfQ.VNYurXCbjY9x4aGGaf6ozYLDQvYdZxHPgaUhuUlmMqTi27ue_NZgdA9Faf7_28im2x4vm2GLQvYrJ25iPpKbrw';

export default function () {

    const payload = JSON.stringify({

        symbol: 'AIRTEL',
        assetType: 'STOCK',
        orderType: 'MARKET',
        orderSide: 'BUY',
        quantity: 10,
        price: 2500
    });

    const params = {

        headers: {

            'Content-Type': 'application/json',

            'Authorization': `Bearer ${token}`,
        },
    };

    const response = http.post(

        'http://host.docker.internal:9090/api/v1/orders',

        payload,

        params
    );

    check(response, {
        'status is 201': (r) => {
            return r.status === 201;
        },

        'order created': (r) =>
            r.json('id') !== null,
    });
}