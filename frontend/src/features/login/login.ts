export const login = async (...args) => {
    try {
        const result = await fetch('http://localhost:8080/cvet-ochey/api/v1/catalog').then((r) =>
            r.text()
        );
        console.log({ result });
        console.log('Hello, world!', args);
        return result;
    } catch (e) {
        console.log(e);
        throw Error;
    }
};
