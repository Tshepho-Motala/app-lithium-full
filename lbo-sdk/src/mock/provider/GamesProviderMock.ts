import GamesProviderMockInterface from '@/core/interface/provider/GamesProviderMockInterface'
import GameItemInterface from '@/plugin/cms/interfaces/GameItemInterface'
import ChannelItem from '@/plugin/cms/models/ChannelItem'
import GameProvider from '@/plugin/cms/models/GameProvider'
import LayoutBannerItem from '@/plugin/cms/models/LayoutBannerItem'
import Category from '@/plugin/components/Category'
import DomainProviderMock from './DomainProviderMock'

export default class GamesProviderMock implements GamesProviderMockInterface {
  gameProviders: GameProvider[] = []

  channels: ChannelItem[] = [
    new ChannelItem(1, 'Desktop Web'),
    new ChannelItem(2, 'Mobile Web'),
    new ChannelItem(3, 'iOS Native'),
    new ChannelItem(4, 'Android Native')
  ]

  constructor() {
    this.applyMockData()
  }

  private async applyMockData() {
    const dp = new DomainProviderMock()

    const domains = await dp.getDomains()
    const domainUk = domains[0]

    const userCategory = new Category('user', domainUk)
    const casinoCategory = new Category('casino', domainUk)

    this.gameProviders = [
      new GameProvider(
        'Roxor',
        [
          new LayoutBannerItem('10p Roulette', 'https://lbo.lithium-develop.ls-g.net/images/logo_wide.png'),
          new LayoutBannerItem('20p Roulette', 'https://lbo.lithium-develop.ls-g.net/images/logo_wide.png'),
          new LayoutBannerItem('Action Bank', 'https://lbo.lithium-develop.ls-g.net/images/logo_wide.png'),
          new LayoutBannerItem('Around The Reels', 'https://lbo.lithium-develop.ls-g.net/images/logo_wide.png')
        ],
        'livescore_uk',
        'svc-reward-pr-casino-roxor'
      ),
      new GameProvider(
        'Microgaming',
        [
          new LayoutBannerItem('Microgaming Book Of Oz', 'https://lbo.lithium-develop.ls-g.net/images/logo_wide.png'),
          new LayoutBannerItem('On The House Roulette', 'https://lbo.lithium-develop.ls-g.net/images/logo_wide.png'),
          new LayoutBannerItem('Phoenix Jackpot', 'https://lbo.lithium-develop.ls-g.net/images/logo_wide.png'),
          new LayoutBannerItem('Lightning Roulette', 'https://lbo.lithium-develop.ls-g.net/images/logo_wide.png')
        ],
        'livescore_uk',
        'svc-reward-pr-casino-microgaming'
      ),
      new GameProvider('User', [], 'livescore_uk', 'svc-promo-provider-user'),
      new GameProvider('Sportsbook', [], 'livescore_uk', 'svc-reward-pr-sportsbook-sbt'),
      new GameProvider('iForium', [], 'livescore_uk', 'svc-reward-pr-casino-iforium')
    ]
  }

  getGamesByDomainAndEnabled(domainName: string, enabled: boolean = true, visible: boolean = true): Promise<GameItemInterface[]> {
    // Shamelessly dumped from the API
    return new Promise((res) => {
      Promise.resolve(
        JSON.parse(
          `[{"id":11, "commercialName": "10p Roulette", "name":"10p Roulette'''","domain":{"id":10,"version":0,"name":"livescore_uk"},"providerGameId":"play-10p-roulette","enabled":false,"visible":true,"locked":false,"lockedMessage":null,"hasLockImage":false,"guid":"service-casino-provider-roxor_play-10p-roulette","description":"10p Roulette description","rtp":97.3,"providerGuid":"service-casino-provider-roxor","freeSpinEnabled":true,"freeSpinValueRequired":true,"freeSpinPlayThroughEnabled":false,"casinoChipEnabled":true,"gameCurrency":null,"gameSupplier":{"id":3,"version":1,"domain":{"id":10,"version":0,"name":"livescore_uk"},"name":"Roxor Gaming","deleted":false},"gameType":{"id":3,"version":0,"domain":{"id":10,"version":0,"name":"livescore_uk"},"name":"DeezusTest","deleted":false},"labels":{"os":{"name":"os","value":"Desktop,Mobile","domainName":"livescore_uk","enabled":false,"deleted":false},"TAG":{"name":"TAG","value":"GAMETESTER","domainName":"livescore_uk","enabled":false,"deleted":false}},"progressiveJackpot":false,"networkedJackpotPool":false,"localJackpotPool":false,"freeGame":false,"cdnImageUrl":"https://www.livescorebet.com/casino-images/play-10p-roulette-240.png"},{"id":126, "commercialName": "10s or Better","name":"10s or Better","domain":{"id":10,"version":0,"name":"livescore_uk"},"providerGameId":"5044","enabled":true,"visible":false,"locked":false,"lockedMessage":null,"hasLockImage":false,"guid":"service-casino-provider-iforium_5044","description":"Iforium test page","rtp":null,"providerGuid":"service-casino-provider-iforium","freeSpinEnabled":false,"freeSpinValueRequired":false,"freeSpinPlayThroughEnabled":false,"casinoFreeBetEnabled":false,"gameCurrency":{"currencyCode":"GBP","minimumAmountCents":1},"gameSupplier":{"id":21,"version":0,"domain":{"id":10,"version":0,"name":"livescore_uk"},"name":"iForium","deleted":false},"gameType":null,"labels":{},"progressiveJackpot":false,"networkedJackpotPool":false,"localJackpotPool":false,"freeGame":false,"cdnImageUrl":null},{"id":125,"name":"11588","domain":{"id":10,"version":0,"name":"livescore_uk"},"providerGameId":"11588","enabled":true,"visible":true,"locked":false,"lockedMessage":null,"hasLockImage":false,"guid":"service-casino-provider-iforium_11588","description":"E2E Tests game","rtp":null,"providerGuid":"service-casino-provider-iforium","freeSpinEnabled":false,"freeSpinValueRequired":false,"freeSpinPlayThroughEnabled":false,"casinoFreeBetEnabled":false,"gameCurrency":{"currencyCode":"GBP","minimumAmountCents":1},"gameSupplier":{"id":21,"version":0,"domain":{"id":10,"version":0,"name":"livescore_uk"},"name":"iForium","deleted":false},"gameType":null,"labels":{},"progressiveJackpot":false,"networkedJackpotPool":false,"localJackpotPool":false,"freeGame":false,"cdnImageUrl":null},{"id":630, "commercialName":"1234gamer","name":"1234gamer","domain":{"id":10,"version":0,"name":"livescore_uk"},"providerGameId":"897465","enabled":true,"visible":false,"locked":false,"lockedMessage":null,"hasLockImage":false,"guid":"service-casino-provider-slotapi_897465","description":"hdfstgstdhsfgh","rtp":null,"providerGuid":"service-casino-provider-slotapi","freeSpinEnabled":true,"freeSpinValueRequired":false,"freeSpinPlayThroughEnabled":false,"casinoFreeBetEnabled":false,"gameCurrency":null,"gameSupplier":{"id":6,"version":0,"domain":{"id":10,"version":0,"name":"livescore_uk"},"name":"SlotAPI","deleted":false},"gameType":null,"labels":{},"progressiveJackpot":false,"networkedJackpotPool":false,"localJackpotPool":false,"freeGame":false,"cdnImageUrl":null},{"id":135,"name":"20234","domain":{"id":10,"version":0,"name":"livescore_uk"},"providerGameId":"20234","enabled":true,"visible":true,"locked":false,"lockedMessage":null,"hasLockImage":false,"guid":"service-casino-provider-iforium_20234","description":"20234","rtp":null,"providerGuid":"service-casino-provider-iforium","freeSpinEnabled":false,"freeSpinValueRequired":false,"freeSpinPlayThroughEnabled":false,"casinoFreeBetEnabled":false,"gameCurrency":{"currencyCode":"GBP","minimumAmountCents":1},"gameSupplier":{"id":21,"version":0,"domain":{"id":10,"version":0,"name":"livescore_uk"},"name":"iForium","deleted":false},"gameType":null,"labels":{"null":{"name":"null","value":"null","domainName":"livescore_uk","enabled":false,"deleted":false}},"progressiveJackpot":false,"networkedJackpotPool":false,"localJackpotPool":false,"freeGame":false,"cdnImageUrl":null}]`
        )
      ).then((games) => {
        games = games.filter((game) => game.enabled == enabled && game.visible == visible && game.domain.name === domainName)
        res(games)
      })
    })
  }

  // TODO: Base this off domain and channel
  getGameProviders(domain: string, channel: ChannelItem): Promise<GameProvider[]> {
    return new Promise((res) => {
      setTimeout(() => {
        res(this.gameProviders)
      }, 1000)
    })
  }

  getProvidersForDomain(domainName: string): Promise<GameProvider[]> {
    return Promise.resolve(this.gameProviders.filter((x) => x.domain === domainName))
  }

  getChannels(): Promise<ChannelItem[]> {
    return new Promise((res) => {
      setTimeout(() => {
        res(this.channels)
      }, 1000)
    })
  }
}

(window as any).VueGameProvider = new GamesProviderMock()
