import GameItemInterface from '@/plugin/cms/interfaces/GameItemInterface'
import ChannelItem from '@/plugin/cms/models/ChannelItem'
import GameProvider from '@/plugin/cms/models/GameProvider'

export default interface GamesProviderMockInterface {
  getGameProviders(domain: string, channel: ChannelItem): Promise<GameProvider[]>

  getGamesByDomainAndEnabled(domainName: string, enabled: boolean, visible: boolean, channel: string): Promise<GameItemInterface[]>

  getProvidersForDomain(domainName: string): Promise<GameProvider[]>

  getChannels(): Promise<ChannelItem[]>
}
