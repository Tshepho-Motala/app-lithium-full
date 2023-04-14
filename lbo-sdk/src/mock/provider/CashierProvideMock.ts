import CashierProvideInterface from "@/core/interface/provider/CashierProvideInterface";

export default class CashierProvideMock implements CashierProvideInterface {
    loadLastXCashierTransactions(count: number): Promise<any> {
        return new Promise((res, rej) => {
            setTimeout(() => {
                let transactions = {
                    userId: 6,
                    domainName: 'livescore_nigeria',
                    lastXTransactions: [
                        {
                            createdOn: 1624353683879,
                            transactionType: 'WITHDRAWAL',
                            processor: 'PayPal Web',
                            descriptor: 'HB56VVDTT2FRU',
                            amount: 5,
                            status: 'WAITFORAPPROVAL',
                            currencyCode: 'NGN'
                        },
                        {
                            createdOn: 1624353660777,
                            transactionType: 'DEPOSIT',
                            processor: 'PayPal Web',
                            descriptor: 'HB56VVDTT2FRU',
                            amount: 14.01,
                            status: 'SUCCESS',
                            currencyCode: 'NGN'
                        },
                        {
                            createdOn: 1624353613595,
                            transactionType: 'WITHDRAWAL',
                            processor: 'PayPal Web',
                            descriptor: 'HB56VVDTT2FRU',
                            amount: 511111.011,
                            status: 'APPROVED',
                            currencyCode: 'NGN'
                        },
                        {
                            createdOn: 1624353599917,
                            transactionType: 'DEPOSIT',
                            processor: 'PayPal Web',
                            descriptor: 'HB56VVDTT2FRU',
                            amount: 14,
                            status: 'AUTO_APPROVED',
                            currencyCode: 'NGN'
                        },
                        {
                            createdOn: 1624353587552,
                            transactionType: 'WITHDRAWAL',
                            processor: 'PayPal Web',
                            descriptor: 'HB56VVDTT2FRU',
                            amount: 5,
                            status: 'PLAYER_CANCEL',
                            currencyCode: 'NGN'
                        },
                        {
                            createdOn: 1624353517262,
                            transactionType: 'DEPOSIT',
                            processor: 'PayPal Web',
                            descriptor: 'HB56VVDTT2FRU',
                            amount: 14,
                            status: 'DECLINED',
                            currencyCode: 'NGN'
                        },
                        {
                            createdOn: 1624353295324,
                            transactionType: 'WITHDRAWAL',
                            processor: 'PayPal Web',
                            descriptor: 'HB56VVDTT2FRU',
                            amount: 5,
                            status: 'FATALERROR',
                            currencyCode: 'NGN'
                        },
                        {
                            createdOn: 1624353112982,
                            transactionType: 'DEPOSIT',
                            processor: 'PayPal Web',
                            descriptor: '2NU49921VP483452U',
                            amount: 10,
                            status: 'EXPIRED',
                            currencyCode: 'NGN'
                        },
                        {
                            createdOn: 1624353082321,
                            transactionType: 'DEPOSIT',
                            processor: 'PayPal Web',
                            descriptor: 'HB56VVDTT2FRU',
                            amount: 10,
                            status: 'CANCEL',
                            currencyCode: 'NGN'
                        },
                        {
                            createdOn: 1624353049283,
                            transactionType: 'DEPOSIT',
                            processor: 'PayPal Web',
                            descriptor: 'HB56VVDTT2FRU',
                            amount: 10,
                            status: 'SUCCESS',
                            currencyCode: 'NGN'
                        }
                    ]
                }
                res(transactions)
            }, 1500)
        })
    }

    openUserTransactions(domain: string, userId: string) {
        console.log('More transactions for ' + domain + "/" + userId)
    }
    loadAutoWithdrawalsRulesets(): Promise<any> {
        return new Promise((res, rej) => {
            console.log(res)
        })
    }
    exportAutoWithdrawalsRulesets(data:any): Promise<any> {
        return new Promise((res, rej) => {
            console.log(res)
        })
    }
    importAutoWithdrawalsRulesets(data:any): Promise<any> {
        return new Promise((res, rej) => {
            console.log(res)
        })
    }
    submitAutoWithdrawalsRulesets(data:any): Promise<any> {
        return new Promise((res, rej) => {
            console.log(res)
        })
    }
    ruleVerificationStatusRest(): Promise<any> {
        return new Promise((res, rej) => {
            console.log(res)
        })
    }
    ruleCashierRest(): Promise<any> {
        return new Promise((res, rej) => {
            console.log(res)
        })
    }
    ruleFindAllTags(data:any): Promise<any> {
        return new Promise((res, rej) => {
            console.log(res)
        })
    }
    cloneRule(domain:string, data:any): Promise<any> {
        return new Promise((res, rej) => {
            console.log(res,rej)
        })
    }
    getRuleset(): Promise<any> {
        return new Promise((res, rej) => {
            console.log(res)
        })
    }
    openUserBalanceMovementTransactionTransactions(domain: string, userId: number) {
        console.log('More transactions Balance Movement for ' + domain + "/" + userId)
    }
    openTransactionAdd(domain: string) {
        console.log('Add transaction for ' + domain )
    }

    refreshTransaction() {
        console.log('transactions refresh')
    }
    rulesetID = 0
    transactionID = undefined
    transaction = undefined
    rulesOperators = []
    rulesFields = []
    domains = []

}