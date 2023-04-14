export default class ApiConnection {
    async login(username: string, passwrd: string) {
        const result = await fetch('', {
            body: JSON.stringify({
                username, passwrd
            })
        })

        return await result.json()
    }
}