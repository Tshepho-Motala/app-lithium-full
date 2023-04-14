import AuthenticationProviderInterface from './provider/AuthenticationProviderInterface'
import BankAccountLookupProviderInterface from './provider/BankAccountLookupProviderInterface'
import DocumentGenerationProviderInterface from './provider/DocumentGenerationProviderInterface'
// import LobbyProviderInterface from './provider/LobbyProviderInterface'
import CashierProvideInterface from './provider/CashierProvideInterface'
import DomainProviderInterface from './provider/DomainProviderInterface'
import CasinoCmsProviderInterface from './provider/CasinoCmsProviderInterface'
import CmsProviderInterface from './provider/CmsProviderInterface'
import PageHeaderProviderInterface from './provider/PageHeaderProviderInterface'
import CsvGeneratorProvider from './provider/CsvGeneratorProvider'
import QuickActionProviderInterface from './provider/QuickActionProviderInterface'
import DropDownMenuInterface from './provider/DropDownMenuInterface'
import GamesProviderMockInterface from '@/core/interface/provider/GamesProviderMockInterface'
import BannerImageProviderMockInterface from '@/core/interface/provider/BannerImageProviderMockInterface'
import CashierConfigTempProviderInterface from './provider/CashierConfigTempProviderInterface'
import PlayerKYCProviderInterface from './provider/PlayerKYCProviderInterface'
import BulkTransactionProviderInterface from "@/core/interface/provider/BulkTransactionProviderInterface"
import DataProviderInterface from './provider/DataProviderInterface'
import ProgressiveFeedsProviderInterface from "@/core/interface/provider/ProgressiveFeedsProviderInterface"

export interface RootScopeInterface {
  provide: RootScopeProvideInterface
}

export interface RootScopeProvideInterface {
  data?: DataProviderInterface
  authentication: AuthenticationProviderInterface
  // lobbyGeneration: LobbyProviderInterface
  bankAccountLookupGeneration: BankAccountLookupProviderInterface
  documentGeneration: DocumentGenerationProviderInterface
  cashierProvider: CashierProvideInterface
  cashierConfigProvider: CashierConfigTempProviderInterface
  domainProvider: DomainProviderInterface
  casinoCmsProvider: CasinoCmsProviderInterface
  cmsProvider: CmsProviderInterface
  pageHeaderProvider: PageHeaderProviderInterface
  csvGeneratorProvider: CsvGeneratorProvider
  quickActionProvider: QuickActionProviderInterface
  playerKYCProvider: PlayerKYCProviderInterface
  dropDownMenuProvider: DropDownMenuInterface
  gamesProvider: GamesProviderMockInterface
  promotionsGamesProvider: GamesProviderMockInterface // TODO : Figure out why gamesProvider is not the same on LBO as it is here.
  bannerImagesProvider: BannerImageProviderMockInterface
  bulkTransactionProvider: BulkTransactionProviderInterface
  progressiveFeedsProvider: ProgressiveFeedsProviderInterface
}
