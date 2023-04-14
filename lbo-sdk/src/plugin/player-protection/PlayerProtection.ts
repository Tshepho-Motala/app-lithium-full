interface LoginFieldInformation {
  date: Date
  userId: string
}

export class SessionHistory {
  successfulLogins: LoginFieldInformation[] = []
  unsuccessfulLogins: LoginFieldInformation[] = []

  get amountOfSuccessfulLogins(): number {
    return this.successfulLogins.length
  }

  get amountOfUnsuccessfulLogins(): number {
    return this.successfulLogins.length
  }

  getSuccessfulLogins(userId: string): Promise<LoginFieldInformation[]> {
    return new Promise((res) => {
      setTimeout(() => {
        const mockData: LoginFieldInformation[] = [
          {
            userId: 'livescore_uk/728193721',
            date: new Date()
          }
        ]

        this.successfulLogins = mockData

        res(mockData)
      }, 1500)
    })
  }

  getUnsuccessfulLogins(userId: string): Promise<LoginFieldInformation[]> {
    return new Promise((res) => {
      setTimeout(() => {
        const mockData: LoginFieldInformation[] = [
          {
            userId: 'livescore_uk/728193721',
            date: new Date()
          }
        ]

        this.unsuccessfulLogins = mockData

        res(mockData)
      }, 1500)
    })
  }
}

export class BigWins {
  dateOfLastBigWin: Date | null = null
  bigWinAmount: string | null = null

  get lblDateOfLastBigWin(): string {
    if (this.dateOfLastBigWin === null) {
      return '-'
    }
    return this.dateOfLastBigWin.toDateString()
  }

  getDetails(): Promise<void> {
    return new Promise((res) => {
      setTimeout(() => {
        // This is where we query the API
        // Which we expect to return this data
        this.dateOfLastBigWin = new Date()
        this.bigWinAmount = '$500,000.85'
      }, 1500)
    })
  }
}

export class PaymentMethodsAndTransactions {
  count: number | null = null
  failedTransactions: number | null = null

  getDetails(): Promise<void> {
    return new Promise((res) => {
      setTimeout(() => {
        // This is where we query the API
        // Which we expect to return this data
        this.count = 30
        this.failedTransactions = 100
      }, 1500)
    })
  }
}

export class WagerHistoryClass {
  countPlacedLastHours: number | null = null
  countPlacedLastDays: string | null = null

  getDetails(): Promise<void> {
    return new Promise((res) => {
      setTimeout(() => {
        // This is where we query the API
        // Which we expect to return this data
        this.countPlacedLastHours = 25
        this.countPlacedLastDays = '85'
      }, 1500)
    })
  }
}

export class ResponsibleGamblingTools {
  currentDepositLimit: number | null = null
  monthlyLossLimit: number | null = null
  monthlyDepositLimit: number | null = null

  getDetails(): Promise<void> {
    return new Promise((res) => {
      setTimeout(() => {
        // This is where we query the API
        // Which we expect to return this data
        this.currentDepositLimit = 27
        this.monthlyLossLimit = 7400
        this.monthlyDepositLimit = 6000
      }, 1500)
    })
  }
}


export class BehaviourChanges {
  wagersPlaced: number | null = null
  averageStakeSize: number | null = null
  lowestStakeSize: number | null = null

  getDetails(): Promise<void> {
    return new Promise((res) => {
      setTimeout(() => {
        // This is where we query the API
        // Which we expect to return this data
        this.wagersPlaced = 6
        this.averageStakeSize = 2
        this.lowestStakeSize = 1
      }, 1500)
    })
  }
}
