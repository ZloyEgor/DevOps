export const login = async () => {
    try {
        return await fetch('http://localhost:8080/cvet-ochey/api/v1/catalog').then((r) => r.text());
    } catch (e) {
        console.log(e);
        throw Error;
    }
};
