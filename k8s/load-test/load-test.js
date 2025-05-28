import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    stages: [
        // Плавный рост нагрузки
        { duration: '15s', target: 3 },
        // Пиковая нагрузка
        { duration: '20s', target: 10 },
        // Плавное снижение
        { duration: '15s', target: 0 },
    ],
};

export default function () {
    const res = http.get('http://backend:8080/cvet-ochey/api/v1/catalog');
    check(res, {
        'status was 200': (r) => r.status == 200,
        'response time < 500ms': (r) => r.timings.duration < 500,
    });
    sleep(1);
}
