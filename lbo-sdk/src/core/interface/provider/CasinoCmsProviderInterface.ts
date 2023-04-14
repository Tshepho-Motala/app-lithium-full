import ChannelItem from "@/plugin/cms/models/ChannelItem";
import GameProvider from "@/plugin/cms/models/GameProvider";
import Lobby from "@/plugin/cms/models/Lobby";
import GameItemInterface from '@/plugin/cms/interfaces/GameItemInterface'
import JsonLobbyContainer from "@/plugin/cms/models/JsonLobbyContainer";
import {Banner} from "@/plugin/cms/models/Banner";
import { PageBanner } from "@/plugin/cms/models/PageBanner";

export default interface CasinoCmsProviderInterface {

  getChannels(): Promise<ChannelItem[]>

  getLobbies(domainName: string): Promise<Lobby[]>

  addLobby(lobby: Lobby): Promise<Lobby>

  removeLobby(lobbyId: number): Promise<void>;

  modifyAndSaveCurrentLobby(domainName: string, id: number, jsonLobbyContainer: JsonLobbyContainer);

  modifyLobby(domainName: string, id: number): Promise<void>;

  getGameProviders(domainName: string, channel: ChannelItem): Promise<GameProvider[]>

  add(title: string, jsonLobbyContainer: JsonLobbyContainer): Promise<void>;

  getBannersForPage(domainName: string, lobbyId: number, channel: string, primaryNavCode: string, secondaryNav: string): Promise<any>;

  getDomainBanners(domain: string): Promise<Banner[]>;

  getBanner(domainName: string, editBannerId: number): Promise<Banner>;

  updateBanner(domainName, id: number, banner: Banner): Promise<Banner>;

  deleteBanner(domainName: string, bannerId: number): Promise<void>;

  saveBanner(domainName, banner: Banner): Promise<Banner>;

  updatePageBannersPosition (domainName: string, lobbyId: number, pageBannerList: PageBanner[]): Promise<PageBanner[]>;

  removePageBanner(domainName: string, lobbyId: number, pageBannerId: number): Promise<void>;

  addPageBanner(domainName: string, lobbyId: number, bannerId: number, pageBanner: PageBanner): Promise<PageBanner>;

}
