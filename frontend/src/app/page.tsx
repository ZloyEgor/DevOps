import React from 'react';
import Link from 'next/link';

export default function Home() {
    return (
        <div>
            <Link href="/catalog">Перейти в каталог цветов</Link>
        </div>
    );
}
