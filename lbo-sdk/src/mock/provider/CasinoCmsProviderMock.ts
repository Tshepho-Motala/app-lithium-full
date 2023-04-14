import CasinoCmsProviderInterface from '@/core/interface/provider/CasinoCmsProviderInterface'
import ChannelItem from '@/plugin/cms/models/ChannelItem'
import DomainItem from '@/plugin/cms/models/DomainItem'
import GameProvider from '@/plugin/cms/models/GameProvider'
import LayoutBannerItem from '@/plugin/cms/models/LayoutBannerItem'
import Lobby from '@/plugin/cms/models/Lobby'
import JsonLobbyContainer from '@/plugin/cms/models/JsonLobbyContainer'
import LobbyItem from '@/plugin/cms/models/LobbyItem'
import {Banner} from "@/plugin/cms/models/Banner";
import {PageBanner} from "@/plugin/cms/models/PageBanner";
import { customAlphabet } from 'nanoid'

export default class CasinoCmsProviderMock implements CasinoCmsProviderInterface {
  lobbyIdCount: number = 1

  // TODO: This is a in-memory replacement for a real database
  lobbiesMap = new Map<string, Lobby[]>()

  channels: ChannelItem[] = [
    new ChannelItem(1, 'Desktop Web'),
    new ChannelItem(2, 'Mobile Web'),
    new ChannelItem(3, 'iOS Native'),
    new ChannelItem(4, 'Android Native')
  ]

  gameProviders: GameProvider[] = [
    new GameProvider(
      'Roxor',
      [
        new LayoutBannerItem('10p Roulette', 'https://lbo.lithium-develop.ls-g.net/images/logo_wide.png'),
        new LayoutBannerItem('20p Roulette', 'https://lbo.lithium-develop.ls-g.net/images/logo_wide.png'),
        new LayoutBannerItem('Action Bank', 'https://lbo.lithium-develop.ls-g.net/images/logo_wide.png'),
        new LayoutBannerItem('Around The Reels', 'https://lbo.lithium-develop.ls-g.net/images/logo_wide.png')
      ],
      'livescore_uk',
      ''
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
      ''
    )
  ]

  banners: Banner[] = [
    {
      "id": 1,
      "version": 0,
      "name": "Hilpert - Satterfield",
      "startDate": new Date(1660524054000),
      "timeFrom": "00:00:00",
      "timeTo": '',
      "link": "http://www.google.com",
      "imageUrl": "https://www.livescorebet.com/uk/about-us/SpotKick-24945.jpg",
      "recurrencePattern": 'FREQ=WEEKLY;BYDAY=SU,TU,TH;INTERVAL=1',
      "loggedIn": true,
      "termsUrl": "http://www.google.com",
      "singleDay": false,
      "lengthInDays": 2,
      "displayText": "Display Text",
      "deleted": false
    }
  ]

  pageBanners: PageBanner[] = [new PageBanner(1, "casino", "for_you", 1, "Mobile Web", this.banners[0])]

  getChannels(): Promise<ChannelItem[]> {
    return new Promise((res) => {
      setTimeout(() => {
        res(this.channels)
      }, 1000)
    })
  }

  getLobbies(domain: string): Promise<Lobby[]> {
    return new Promise((res) => {
      setTimeout(() => {
        const nullableLobbies = this.lobbiesMap.get(domain)
        const lobbies: Lobby[] = nullableLobbies ? nullableLobbies : []
        res(lobbies)
      }, 1000)
    })
  }

  // Adding individual lobbies not supported yet
  addLobby(lobby: Lobby): Promise<Lobby> {
    return new Promise((res) => {
      setTimeout(() => {
        // lobby.id = this.lobbyIdCount++;
        // lobby.modifiedDate = new Date();
        // this.lobbies.push(lobby)
        res(lobby)
      }, 1000)
    })
  }

  // Removing individual lobbies not supported currently
  removeLobby(lobbyId: number): Promise<void> {
    return new Promise((res) => {
      setTimeout(() => {
        res()
      }, 1000)
    })
  }

  modifyAndSaveCurrentLobby(domainName: string, id: number, jsonLobbyContainer: JsonLobbyContainer): Promise<void> {
    return new Promise((res) => {
      setTimeout(() => {
        this.lobbiesMap.set(domainName, this.convertLobbyJsonToLobbiesObject(jsonLobbyContainer, id))
        res()
      }, 1000)
    })
  }

  modifyLobby(domainName: string, id: number): Promise<void> {
    return new Promise((res) => {
      setTimeout(() => {
        res()
      }, 1000)
    })
  }

  // TODO: Base this off domain and channel
  getGameProviders(domainName: string, channel: ChannelItem): Promise<GameProvider[]> {
    return new Promise((res) => {
      setTimeout(() => {
        res(this.gameProviders)
      }, 1000)
    })
  }

  add(domainName: string, jsonLobbyContainer: JsonLobbyContainer): Promise<void> {
    return new Promise((res) => {
      setTimeout(() => {
        this.lobbiesMap.set(domainName, this.convertLobbyJsonToLobbiesObject(jsonLobbyContainer, 0))
        res()
      }, 1000)
    })
  }

  getBannersForPage(domainName: string, lobbyId: number, channel: string, primaryNavCode: string, secondaryNav: string): Promise<PageBanner[]> {
    return new Promise((res) => {
      setTimeout(() => {
        res(this.pageBanners);
      }, 1000)
    })
  }

  getDomainBanners(domain:string): Promise<Banner[]> {
    return new Promise((res) => {
      setTimeout(() => {
        res(this.banners);
      }, 1000)
    })
  }

  getBanner(domainName: string, editBannerId: number): Promise<Banner> {
    return new Promise((res) => {
      setTimeout(() => {
        res(this.banners[0]);
      }, 1000)
    })
  }

  updateBanner(domainName: string, id: number, banner: Banner): Promise<Banner> {
    return new Promise((res) => {
      setTimeout(() => {
        const index = this.banners.findIndex(bannerOld => bannerOld.id === banner.id);
        this.banners[index] = banner;
        res(banner);
      }, 1000)
    })
  }

  deleteBanner(domainName: string, bannerId: number): Promise<void> {
    return new Promise((res, reject) => {
      setTimeout(() => {
        const index = this.banners.findIndex(banner => banner.id === bannerId);
        if (index >= 0) {
          this.banners.splice(index, 1);
          res();
        } else {
          reject('Banner not found');
        }

      }, 1000)
    })
  }

  saveBanner(domainName: string, banner: Banner): Promise<Banner> {
    return new Promise((res) => {
      setTimeout(() => {
        if(this.banners.length == 0) {
          banner.id = 1;
        } else {
          banner.id = this.banners[this.banners.length - 1].id + 1;
        }
        this.banners.push(banner);
        res(banner);
      }, 1000)
    })
  }

  updatePageBannersPosition (domainName: string, lobbyId: number, pageBannerList: PageBanner[]): Promise<PageBanner[]> {
    return new Promise((res, reject) => {
      setTimeout(() => {
        pageBannerList.forEach(pageBanner => {
          let i = 0;
          for(; i < this.pageBanners.length; i++) {
            if (!this.pageBanners[i].id || !pageBanner.id) {
              reject("Page banner id is null");
            }
            if (pageBanner.id === this.pageBanners[i].id) {
              this.pageBanners[i].position = pageBanner.position;
            }
          }
        })
        res(pageBannerList);
      }, 1000)
    })
  }

  removePageBanner(domainName: string, lobbyId: number, pageBannerId: number): Promise<void> {
      return new Promise((res, reject) => {
        setTimeout(() => {
          const index = this.pageBanners.findIndex(pageBanner => pageBanner.id === pageBannerId);
          if (index >= 0) {
            this.pageBanners.splice(index, 1);
            res();
          } else {
            reject('Page banner not found');
          }
          
        }, 1000)
      })
  }

  addPageBanner(domainName: string, lobbyId: number, bannerId: number, pageBanner: PageBanner): Promise<PageBanner> {
    return new Promise((res, reject) => {
      setTimeout(() => {
        const bannerIndex = this.banners.findIndex(banner => banner.id === bannerId);
        if(bannerIndex >= 0) {
          const nanoid = customAlphabet('0123456789', 10);
          pageBanner.id = Number(nanoid());
          this.pageBanners.push(pageBanner);
          res(pageBanner);
        } else {
          reject('Banner not found');
        }
        
      }, 1000)
    })
  }

  convertLobbyJsonToLobbiesObject(jsonLobbyContainer: JsonLobbyContainer, id: number): Lobby[] {
    const lobbyItems: LobbyItem[] = JSON.parse(jsonLobbyContainer.json)
    let tempLobbies: Lobby[] = []
    lobbyItems.forEach((lobbyItem) => {
      const index = tempLobbies.findIndex((lobby) => {
        return lobby.nav ? lobby.nav[0].primary_nav_code === lobbyItem.page.primary_nav_code && lobby.channel === lobbyItem.page.channel : false
      })
      if (index != -1) {
        tempLobbies[index].lobbyItems.push(lobbyItem)
      } else {
        const lobby = this.buildLobby(lobbyItem, id, jsonLobbyContainer.description)
        tempLobbies.push(lobby)
      }
    })
    return tempLobbies
  }

  buildLobby(lobbyItem: LobbyItem, id: number, description: string): Lobby {
    const lobby: Lobby = new Lobby(lobbyItem.page.primary_nav_code, lobbyItem.page.channel)
    lobby.nav = lobbyItem.nav
    lobby.modifiedDate = new Date()
    lobby.description = description
    lobby.active = false
    lobby.badges = lobbyItem.badges
    lobby.id = id
    lobby.name = lobbyItem.name
    return lobby
  }
}

;(window as any).VueCmsProvider = new CasinoCmsProviderMock()
