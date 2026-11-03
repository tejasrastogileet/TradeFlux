import http from 'k6/http';

import { check, sleep } from 'k6';

export const options = {

    vus: 250,

    duration: '30s',
};

export default function () {

    const payload = JSON.stringify({

        email: 'abcd@gmail.com',

        password: 'password123'
    });

    const params = {

        headers: {

            'Content-Type': 'application/json',
        },
    };

    const response = http.post(

        'http://host.docker.internal:9090/api/v1/auth/login',

        payload,
        params
    );
    check(response, {
        'status is 200': (r) => r.status === 200,
        'access token exists': (r) =>
            r.json('accessToken') !== null,
    });

    sleep(1);
}