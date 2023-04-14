import AuthenticationProviderInterface from '@/core/interface/provider/AuthenticationProviderInterface'
import BankAccountLookupProviderInterface from '@/core/interface/provider/BankAccountLookupProviderInterface'
import DocumentGenerationProviderInterface from '@/core/interface/provider/DocumentGenerationProviderInterface'
import CashierProvideInterface from '@/core/interface/provider/CashierProvideInterface'
import { RootScopeInterface, RootScopeProvideInterface } from '@/core/interface/ScopeInterface'
import AuthenticationProviderMock from './provider/AuthenticationProviderMock'
import BankAccountLookupProviderMock from './provider/BankAccountLookupProviderMock'
import DocumentGenerationMock from './provider/DocumentGenerationMock'
import CashierProvideMock from './provider/CashierProvideMock'
import DomainProviderInterface from '@/core/interface/provider/DomainProviderInterface'
import DomainProviderMock from './provider/DomainProviderMock'
import CasinoCmsProviderInterface from '@/core/interface/provider/CasinoCmsProviderInterface'
import CasinoCmsProviderMock from './provider/CasinoCmsProviderMock'
import CmsProviderInterface from '@/core/interface/provider/CmsProviderInterface'
import CmsProviderMock from './provider/CmsProviderMock'
import PageHeaderProviderInterface from '@/core/interface/provider/PageHeaderProviderInterface'
import PageHeaderProviderMock from './provider/PageHeaderProviderMock'
import CsvGeneratorProvider from '@/core/interface/provider/CsvGeneratorProvider'
import CsvGeneratorProviderMock from './provider/CSVGeneratorProviderMock'
import CashierConfigTempProviderInterface from '@/core/interface/provider/CashierConfigTempProviderInterface'
import CashierConfigTempProviderMock from './provider/CashierConfigTempProviderMock'
import DropDownMenuProviderMock from './provider/DropDownMenuProviderMock'
import DropDownMenuInterface from '@/core/interface/provider/DropDownMenuInterface'
import QuickActionProviderInterface from '@/core/interface/provider/QuickActionProviderInterface'
import QuickActionProviderMock from './provider/QuickActionProviderMock'
import GamesProviderMockInterface from '@/core/interface/provider/GamesProviderMockInterface'
import GamesProviderMock from '@/mock/provider/GamesProviderMock'
import BannerImageProviderMockInterface from '@/core/interface/provider/BannerImageProviderMockInterface'
import BannerImageProviderMock from '@/mock/provider/BannerImageProviderMock'
import PlayerKYCProviderInterface from '@/core/interface/provider/PlayerKYCProviderInterface'
import PlayerKYCProviderMockInterface from '@/mock/provider/PlayerKYCProviderMockInterface'
import BulkTransactionProviderInterface from "@/core/interface/provider/BulkTransactionProviderInterface"
import BulKTransactionProviderMock from "@/mock/provider/BulKTransactionProviderMock"
import ProgressiveFeedsProviderInterface from "@/core/interface/provider/ProgressiveFeedsProviderInterface"
import ProgressiveFeedsProviderMock from "@/mock/provider/ProgressiveFeedsProviderMock"

export class RootScopeMock implements RootScopeInterface {
  provide: RootScopeProvideInterface

  constructor() {
    this.provide = new RootScopeProvide()
  }
}

class RootScopeProvide implements RootScopeProvideInterface {
  // lobbyGeneration: LobbyProviderInterface
  authentication: AuthenticationProviderInterface = new AuthenticationProviderMock()
  bankAccountLookupGeneration: BankAccountLookupProviderInterface = new BankAccountLookupProviderMock()
  documentGeneration: DocumentGenerationProviderInterface = new DocumentGenerationMock()
  cashierProvider: CashierProvideInterface = new CashierProvideMock()
  domainProvider: DomainProviderInterface = new DomainProviderMock()
  casinoCmsProvider: CasinoCmsProviderInterface = new CasinoCmsProviderMock()
  cmsProvider: CmsProviderInterface = new CmsProviderMock()
  pageHeaderProvider: PageHeaderProviderInterface = new PageHeaderProviderMock()
  cashierConfigProvider: CashierConfigTempProviderInterface = new CashierConfigTempProviderMock()
  csvGeneratorProvider: CsvGeneratorProvider = new CsvGeneratorProviderMock()
  quickActionProvider: QuickActionProviderInterface = new QuickActionProviderMock()
  dropDownMenuProvider: DropDownMenuInterface = new DropDownMenuProviderMock()
  gamesProvider: GamesProviderMockInterface = new GamesProviderMock()
  promotionsGamesProvider: GamesProviderMockInterface = new GamesProviderMock() // TODO: Figure out why this is not the same on LBO as it is here (it should be gamesprovider)
  bannerImagesProvider: BannerImageProviderMockInterface = new BannerImageProviderMock()
  playerKYCProvider: PlayerKYCProviderInterface = new PlayerKYCProviderMockInterface()
  bulkTransactionProvider: BulkTransactionProviderInterface = new BulKTransactionProviderMock()
  progressiveFeedsProvider: ProgressiveFeedsProviderInterface = new ProgressiveFeedsProviderMock()
}
