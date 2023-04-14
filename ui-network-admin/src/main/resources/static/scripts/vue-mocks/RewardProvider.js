/******/ (function(modules) { // webpackBootstrap
    /******/ 	// The module cache
    /******/ 	var installedModules = {};
    /******/
    /******/ 	// The require function
    /******/ 	function __webpack_require__(moduleId) {
        /******/
        /******/ 		// Check if module is in cache
        /******/ 		if(installedModules[moduleId]) {
            /******/ 			return installedModules[moduleId].exports;
            /******/ 		}
        /******/ 		// Create a new module (and put it into the cache)
        /******/ 		var module = installedModules[moduleId] = {
            /******/ 			i: moduleId,
            /******/ 			l: false,
            /******/ 			exports: {}
            /******/ 		};
        /******/
        /******/ 		// Execute the module function
        /******/ 		modules[moduleId].call(module.exports, module, module.exports, __webpack_require__);
        /******/
        /******/ 		// Flag the module as loaded
        /******/ 		module.l = true;
        /******/
        /******/ 		// Return the exports of the module
        /******/ 		return module.exports;
        /******/ 	}
    /******/
    /******/
    /******/ 	// expose the modules object (__webpack_modules__)
    /******/ 	__webpack_require__.m = modules;
    /******/
    /******/ 	// expose the module cache
    /******/ 	__webpack_require__.c = installedModules;
    /******/
    /******/ 	// define getter function for harmony exports
    /******/ 	__webpack_require__.d = function(exports, name, getter) {
        /******/ 		if(!__webpack_require__.o(exports, name)) {
            /******/ 			Object.defineProperty(exports, name, { enumerable: true, get: getter });
            /******/ 		}
        /******/ 	};
    /******/
    /******/ 	// define __esModule on exports
    /******/ 	__webpack_require__.r = function(exports) {
        /******/ 		if(typeof Symbol !== 'undefined' && Symbol.toStringTag) {
            /******/ 			Object.defineProperty(exports, Symbol.toStringTag, { value: 'Module' });
            /******/ 		}
        /******/ 		Object.defineProperty(exports, '__esModule', { value: true });
        /******/ 	};
    /******/
    /******/ 	// create a fake namespace object
    /******/ 	// mode & 1: value is a module id, require it
    /******/ 	// mode & 2: merge all properties of value into the ns
    /******/ 	// mode & 4: return value when already ns object
    /******/ 	// mode & 8|1: behave like require
    /******/ 	__webpack_require__.t = function(value, mode) {
        /******/ 		if(mode & 1) value = __webpack_require__(value);
        /******/ 		if(mode & 8) return value;
        /******/ 		if((mode & 4) && typeof value === 'object' && value && value.__esModule) return value;
        /******/ 		var ns = Object.create(null);
        /******/ 		__webpack_require__.r(ns);
        /******/ 		Object.defineProperty(ns, 'default', { enumerable: true, value: value });
        /******/ 		if(mode & 2 && typeof value != 'string') for(var key in value) __webpack_require__.d(ns, key, function(key) { return value[key]; }.bind(null, key));
        /******/ 		return ns;
        /******/ 	};
    /******/
    /******/ 	// getDefaultExport function for compatibility with non-harmony modules
    /******/ 	__webpack_require__.n = function(module) {
        /******/ 		var getter = module && module.__esModule ?
            /******/ 			function getDefault() { return module['default']; } :
            /******/ 			function getModuleExports() { return module; };
        /******/ 		__webpack_require__.d(getter, 'a', getter);
        /******/ 		return getter;
        /******/ 	};
    /******/
    /******/ 	// Object.prototype.hasOwnProperty.call
    /******/ 	__webpack_require__.o = function(object, property) { return Object.prototype.hasOwnProperty.call(object, property); };
    /******/
    /******/ 	// __webpack_public_path__
    /******/ 	__webpack_require__.p = "";
    /******/
    /******/
    /******/ 	// Load entry module and return exports
    /******/ 	return __webpack_require__(__webpack_require__.s = "./src/mock/provider/RewardProviderMock.ts");
    /******/ })
    /************************************************************************/
    /******/ ({

        /***/ "./node_modules/nanoid/index.browser.js":
        /*!**********************************************!*\
          !*** ./node_modules/nanoid/index.browser.js ***!
          \**********************************************/
        /*! exports provided: urlAlphabet, random, customRandom, customAlphabet, nanoid */
        /***/ (function(module, __webpack_exports__, __webpack_require__) {

            "use strict";
            __webpack_require__.r(__webpack_exports__);
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "random", function() { return random; });
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "customRandom", function() { return customRandom; });
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "customAlphabet", function() { return customAlphabet; });
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "nanoid", function() { return nanoid; });
            /* harmony import */ var _url_alphabet_index_js__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./url-alphabet/index.js */ "./node_modules/nanoid/url-alphabet/index.js");
            /* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "urlAlphabet", function() { return _url_alphabet_index_js__WEBPACK_IMPORTED_MODULE_0__["urlAlphabet"]; });


            let random = bytes => crypto.getRandomValues(new Uint8Array(bytes))
            let customRandom = (alphabet, defaultSize, getRandom) => {
                let mask = (2 << (Math.log(alphabet.length - 1) / Math.LN2)) - 1
                let step = -~((1.6 * mask * defaultSize) / alphabet.length)
                return (size = defaultSize) => {
                    let id = ''
                    while (true) {
                        let bytes = getRandom(step)
                        let j = step
                        while (j--) {
                            id += alphabet[bytes[j] & mask] || ''
                            if (id.length === size) return id
                        }
                    }
                }
            }
            let customAlphabet = (alphabet, size = 21) =>
                customRandom(alphabet, size, random)
            let nanoid = (size = 21) =>
                crypto.getRandomValues(new Uint8Array(size)).reduce((id, byte) => {
                    byte &= 63
                    if (byte < 36) {
                        id += byte.toString(36)
                    } else if (byte < 62) {
                        id += (byte - 26).toString(36).toUpperCase()
                    } else if (byte > 62) {
                        id += '-'
                    } else {
                        id += '_'
                    }
                    return id
                }, '')


            /***/ }),

        /***/ "./node_modules/nanoid/url-alphabet/index.js":
        /*!***************************************************!*\
          !*** ./node_modules/nanoid/url-alphabet/index.js ***!
          \***************************************************/
        /*! exports provided: urlAlphabet */
        /***/ (function(module, __webpack_exports__, __webpack_require__) {

            "use strict";
            __webpack_require__.r(__webpack_exports__);
            /* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "urlAlphabet", function() { return urlAlphabet; });
            const urlAlphabet =
                'useandom-26T198340PX75pxJACKVERYMINDBUSHWOLF_GQZbfghjklqvwyzrict'


            /***/ }),

        /***/ "./src/mock/provider/DomainProviderMock.ts":
        /*!*************************************************!*\
          !*** ./src/mock/provider/DomainProviderMock.ts ***!
          \*************************************************/
        /*! no static exports found */
        /***/ (function(module, exports, __webpack_require__) {

            "use strict";

            var __importDefault = (this && this.__importDefault) || function (mod) {
                return (mod && mod.__esModule) ? mod : { "default": mod };
            };
            Object.defineProperty(exports, "__esModule", { value: true });
            const DomainItem_1 = __importDefault(__webpack_require__(/*! @/plugin/cms/models/DomainItem */ "./src/plugin/cms/models/DomainItem.ts"));
            class DomainProviderMock {
                getDomains() {
                    // Data taken from $userService.domainsWithAnyRole(['ADMIN'])
                    return new Promise((res) => {
                        const domains = [
                            { name: 'livescore', pd: false, displayName: 'Livescore Develop Admin' },
                            { name: 'livescore_uk', pd: true, displayName: 'Livescore UK' },
                            { name: 'livescore_nigeria', pd: true, displayName: 'Livescore  Nigeria' },
                            { name: 'livescore_media', pd: true, displayName: 'Livescore Media' },
                            { name: 'livescore_nl', pd: true, displayName: 'Livescore Netherlands' },
                            { name: 'livescore_ie', pd: true, displayName: 'Livescore Ireland' },
                            { name: 'livescore_za', pd: false, displayName: 'Livescore South Africa' },
                            { name: 'virginbet_uk', pd: true, displayName: 'Virginbet UK' },
                        ];
                        const domainItems = [];
                        for (const item of domains) {
                            domainItems.push(new DomainItem_1.default(item.displayName, item.name, item.pd));
                        }
                        setTimeout(() => {
                            res(domainItems);
                        }, 1000);
                    });
                }
            }
            exports.default = DomainProviderMock;


            /***/ }),

        /***/ "./src/mock/provider/GamesProviderMock.ts":
        /*!************************************************!*\
          !*** ./src/mock/provider/GamesProviderMock.ts ***!
          \************************************************/
        /*! no static exports found */
        /***/ (function(module, exports, __webpack_require__) {

            "use strict";

            var __createBinding = (this && this.__createBinding) || (Object.create ? (function(o, m, k, k2) {
                if (k2 === undefined) k2 = k;
                Object.defineProperty(o, k2, { enumerable: true, get: function() { return m[k]; } });
            }) : (function(o, m, k, k2) {
                if (k2 === undefined) k2 = k;
                o[k2] = m[k];
            }));
            var __setModuleDefault = (this && this.__setModuleDefault) || (Object.create ? (function(o, v) {
                Object.defineProperty(o, "default", { enumerable: true, value: v });
            }) : function(o, v) {
                o["default"] = v;
            });
            var __importStar = (this && this.__importStar) || function (mod) {
                if (mod && mod.__esModule) return mod;
                var result = {};
                if (mod != null) for (var k in mod) if (k !== "default" && Object.prototype.hasOwnProperty.call(mod, k)) __createBinding(result, mod, k);
                __setModuleDefault(result, mod);
                return result;
            };
            var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
                function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
                return new (P || (P = Promise))(function (resolve, reject) {
                    function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
                    function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
                    function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
                    step((generator = generator.apply(thisArg, _arguments || [])).next());
                });
            };
            var __importDefault = (this && this.__importDefault) || function (mod) {
                return (mod && mod.__esModule) ? mod : { "default": mod };
            };
            Object.defineProperty(exports, "__esModule", { value: true });
            const GameProvider_1 = __importStar(__webpack_require__(/*! @/plugin/cms/models/GameProvider */ "./src/plugin/cms/models/GameProvider.ts"));
            const LayoutBannerItem_1 = __importDefault(__webpack_require__(/*! @/plugin/cms/models/LayoutBannerItem */ "./src/plugin/cms/models/LayoutBannerItem.ts"));
            const Category_1 = __importDefault(__webpack_require__(/*! @/plugin/components/Category */ "./src/plugin/components/Category.ts"));
            const DomainProviderMock_1 = __importDefault(__webpack_require__(/*! ./DomainProviderMock */ "./src/mock/provider/DomainProviderMock.ts"));
            class GamesProviderMock {
                constructor() {
                    this.gameProviders = [];
                    this.applyMockData();
                }
                applyMockData() {
                    return __awaiter(this, void 0, void 0, function* () {
                        const dp = new DomainProviderMock_1.default();
                        const domains = yield dp.getDomains();
                        const domainUk = domains[0];
                        const userCategory = new Category_1.default('user', domainUk);
                        const casinoCategory = new Category_1.default('casino', domainUk);
                        this.gameProviders = [
                            new GameProvider_1.default('Roxor', [
                                new LayoutBannerItem_1.default('10p Roulette', 'https://lbo.lithium-develop.ls-g.net/images/logo_wide.png'),
                                new LayoutBannerItem_1.default('20p Roulette', 'https://lbo.lithium-develop.ls-g.net/images/logo_wide.png'),
                                new LayoutBannerItem_1.default('Action Bank', 'https://lbo.lithium-develop.ls-g.net/images/logo_wide.png'),
                                new LayoutBannerItem_1.default('Around The Reels', 'https://lbo.lithium-develop.ls-g.net/images/logo_wide.png')
                            ], 'livescore_uk', 'svc-reward-pr-casino-roxor', casinoCategory, [new GameProvider_1.Activity('wager', 2), new GameProvider_1.Activity('win', 2)], [new GameProvider_1.ExtraField('game', 2), new GameProvider_1.ExtraField('game_type', 2)]),
                            new GameProvider_1.default('Microgaming', [
                                new LayoutBannerItem_1.default('Microgaming Book Of Oz', 'https://lbo.lithium-develop.ls-g.net/images/logo_wide.png'),
                                new LayoutBannerItem_1.default('On The House Roulette', 'https://lbo.lithium-develop.ls-g.net/images/logo_wide.png'),
                                new LayoutBannerItem_1.default('Phoenix Jackpot', 'https://lbo.lithium-develop.ls-g.net/images/logo_wide.png'),
                                new LayoutBannerItem_1.default('Lightning Roulette', 'https://lbo.lithium-develop.ls-g.net/images/logo_wide.png')
                            ], 'livescore_uk', 'svc-reward-pr-casino-microgaming'),
                            new GameProvider_1.default('User', [], 'livescore_uk', 'svc-promo-provider-user', userCategory, [new GameProvider_1.Activity('login-success', 1)], [new GameProvider_1.ExtraField('daysOfWeek', 1), new GameProvider_1.ExtraField('granularity', 1), new GameProvider_1.ExtraField('consecutiveLogins', 1)]),
                            new GameProvider_1.default('Sportsbook', [], 'livescore_uk', 'svc-reward-pr-sportsbook-sbt'),
                            new GameProvider_1.default('iForium', [], 'livescore_uk', 'svc-reward-pr-casino-iforium')
                        ];
                    });
                }
                getGamesByDomainAndEnabled(domainName, enabled, visible, channel) {
                    // Shamelessly dumped from the API
                    return new Promise((res) => {
                        Promise.resolve(JSON.parse(`[{"id":11,"name":"10p Roulette'''","domain":{"id":10,"version":0,"name":"livescore_uk"},"providerGameId":"play-10p-roulette","enabled":false,"visible":true,"locked":false,"lockedMessage":null,"hasLockImage":false,"guid":"service-casino-provider-roxor_play-10p-roulette","description":"10p Roulette description","rtp":97.3,"providerGuid":"service-casino-provider-roxor","freeSpinEnabled":true,"freeSpinValueRequired":true,"freeSpinPlayThroughEnabled":false,"casinoChipEnabled":true,"gameCurrency":null,"gameSupplier":{"id":3,"version":1,"domain":{"id":10,"version":0,"name":"livescore_uk"},"name":"Roxor Gaming","deleted":false},"gameType":{"id":3,"version":0,"domain":{"id":10,"version":0,"name":"livescore_uk"},"name":"DeezusTest","deleted":false},"labels":{"os":{"name":"os","value":"Desktop,Mobile","domainName":"livescore_uk","enabled":false,"deleted":false},"TAG":{"name":"TAG","value":"GAMETESTER","domainName":"livescore_uk","enabled":false,"deleted":false}},"progressiveJackpot":false,"networkedJackpotPool":false,"localJackpotPool":false,"freeGame":false,"cdnImageUrl":"https://www.livescorebet.com/casino-images/play-10p-roulette-240.png"},{"id":126,"name":"10s or Better","domain":{"id":10,"version":0,"name":"livescore_uk"},"providerGameId":"5044","enabled":true,"visible":false,"locked":false,"lockedMessage":null,"hasLockImage":false,"guid":"service-casino-provider-iforium_5044","description":"Iforium test page","rtp":null,"providerGuid":"service-casino-provider-iforium","freeSpinEnabled":false,"freeSpinValueRequired":false,"freeSpinPlayThroughEnabled":false,"casinoFreeBetEnabled":false,"gameCurrency":{"currencyCode":"GBP","minimumAmountCents":1},"gameSupplier":{"id":21,"version":0,"domain":{"id":10,"version":0,"name":"livescore_uk"},"name":"iForium","deleted":false},"gameType":null,"labels":{},"progressiveJackpot":false,"networkedJackpotPool":false,"localJackpotPool":false,"freeGame":false,"cdnImageUrl":null},{"id":125,"name":"11588","domain":{"id":10,"version":0,"name":"livescore_uk"},"providerGameId":"11588","enabled":true,"visible":true,"locked":false,"lockedMessage":null,"hasLockImage":false,"guid":"service-casino-provider-iforium_11588","description":"E2E Tests game","rtp":null,"providerGuid":"service-casino-provider-iforium","freeSpinEnabled":false,"freeSpinValueRequired":false,"freeSpinPlayThroughEnabled":false,"casinoFreeBetEnabled":false,"gameCurrency":{"currencyCode":"GBP","minimumAmountCents":1},"gameSupplier":{"id":21,"version":0,"domain":{"id":10,"version":0,"name":"livescore_uk"},"name":"iForium","deleted":false},"gameType":null,"labels":{},"progressiveJackpot":false,"networkedJackpotPool":false,"localJackpotPool":false,"freeGame":false,"cdnImageUrl":null},{"id":630,"name":"1234gamer","domain":{"id":10,"version":0,"name":"livescore_uk"},"providerGameId":"897465","enabled":true,"visible":false,"locked":false,"lockedMessage":null,"hasLockImage":false,"guid":"service-casino-provider-slotapi_897465","description":"hdfstgstdhsfgh","rtp":null,"providerGuid":"service-casino-provider-slotapi","freeSpinEnabled":true,"freeSpinValueRequired":false,"freeSpinPlayThroughEnabled":false,"casinoFreeBetEnabled":false,"gameCurrency":null,"gameSupplier":{"id":6,"version":0,"domain":{"id":10,"version":0,"name":"livescore_uk"},"name":"SlotAPI","deleted":false},"gameType":null,"labels":{},"progressiveJackpot":false,"networkedJackpotPool":false,"localJackpotPool":false,"freeGame":false,"cdnImageUrl":null},{"id":135,"name":"20234","domain":{"id":10,"version":0,"name":"livescore_uk"},"providerGameId":"20234","enabled":true,"visible":true,"locked":false,"lockedMessage":null,"hasLockImage":false,"guid":"service-casino-provider-iforium_20234","description":"20234","rtp":null,"providerGuid":"service-casino-provider-iforium","freeSpinEnabled":false,"freeSpinValueRequired":false,"freeSpinPlayThroughEnabled":false,"casinoFreeBetEnabled":false,"gameCurrency":{"currencyCode":"GBP","minimumAmountCents":1},"gameSupplier":{"id":21,"version":0,"domain":{"id":10,"version":0,"name":"livescore_uk"},"name":"iForium","deleted":false},"gameType":null,"labels":{"null":{"name":"null","value":"null","domainName":"livescore_uk","enabled":false,"deleted":false}},"progressiveJackpot":false,"networkedJackpotPool":false,"localJackpotPool":false,"freeGame":false,"cdnImageUrl":null}]`)).then((games) => {
                            games = games.filter((game) => game.enabled == enabled && game.visible == visible);
                            res(games);
                        });
                    });
                }
                // TODO: Base this off domain and channel
                getGameProviders(domain, channel) {
                    return new Promise((res) => {
                        setTimeout(() => {
                            res(this.gameProviders);
                        }, 1000);
                    });
                }
                getProvidersForDomain(domainName) {
                    return Promise.resolve(this.gameProviders.filter((x) => x.domain === domainName));
                }
                getProvidersForDomainAndCategory(domainName, category) {
                    return Promise.resolve(this.gameProviders.filter((x) => { var _a; return x.domain === domainName && ((_a = x.category) === null || _a === void 0 ? void 0 : _a.name) === category.name; }));
                }
            }
            exports.default = GamesProviderMock;
            ;
            window.VueGameProvider = new GamesProviderMock();


            /***/ }),

        /***/ "./src/mock/provider/RewardProviderMock.ts":
        /*!*************************************************!*\
          !*** ./src/mock/provider/RewardProviderMock.ts ***!
          \*************************************************/
        /*! no static exports found */
        /***/ (function(module, exports, __webpack_require__) {

            "use strict";

            var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
                function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
                return new (P || (P = Promise))(function (resolve, reject) {
                    function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
                    function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
                    function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
                    step((generator = generator.apply(thisArg, _arguments || [])).next());
                });
            };
            var __importDefault = (this && this.__importDefault) || function (mod) {
                return (mod && mod.__esModule) ? mod : { "default": mod };
            };
            Object.defineProperty(exports, "__esModule", { value: true });
            const Category_1 = __importDefault(__webpack_require__(/*! @/plugin/components/Category */ "./src/plugin/components/Category.ts"));
            const Reward_1 = __webpack_require__(/*! @/plugin/promotions/reward/Reward */ "./src/plugin/promotions/reward/Reward.ts");
            const DomainProviderMock_1 = __importDefault(__webpack_require__(/*! ./DomainProviderMock */ "./src/mock/provider/DomainProviderMock.ts"));
            const GamesProviderMock_1 = __importDefault(__webpack_require__(/*! ./GamesProviderMock */ "./src/mock/provider/GamesProviderMock.ts"));
            class RewardProviderMock {
                constructor() {
                    this.rewardTypes = [];
                    this.rewards = [];
                    this.applyMockData();
                }
                applyMockData() {
                    return __awaiter(this, void 0, void 0, function* () {
                        const gm = new GamesProviderMock_1.default();
                        const dp = new DomainProviderMock_1.default();
                        const domains = yield dp.getDomains();
                        const domainUk = domains[0];
                        const providerRoxor = gm.gameProviders[0];
                        const providerUser = gm.gameProviders[2];
                        const userCategory = new Category_1.default('user', domainUk);
                        const casinoCategory = new Category_1.default('casino', domainUk);
                        this.rewardTypes = [
                            new Reward_1.RewardType('Free Spins', domainUk, providerRoxor, casinoCategory),
                            new Reward_1.RewardType('Free Games', domainUk, providerRoxor, casinoCategory),
                            new Reward_1.RewardType('Instant rewards', domainUk, providerRoxor, userCategory),
                            new Reward_1.RewardType('Free Bets', domainUk, providerRoxor, userCategory),
                            new Reward_1.RewardType('Freespins', domainUk, providerUser, casinoCategory),
                            new Reward_1.RewardType('Cash', domainUk, providerUser, userCategory)
                        ];
                    });
                }
                getRewards() {
                    return new Promise((res, rej) => {
                        setTimeout(() => {
                            res(this.rewards);
                        }, 1500);
                    });
                }
                getRewardTypes() {
                    return new Promise((res, rej) => {
                        setTimeout(() => {
                            res(this.rewardTypes);
                        }, 1500);
                    });
                }
                getRewardTypesForProvider(provider) {
                    return new Promise((res) => {
                        setTimeout(() => {
                            const list = this.rewardTypes.filter((t) => t.provider.url === provider.url);
                            res(list);
                        }, 1500);
                    });
                }
                getRewardTypesForProviderAndCategory(provider, category) {
                    return new Promise((res) => {
                        setTimeout(() => {
                            const list = this.rewardTypes.filter((t) => t.provider.url === provider.url && t.category.name === category.name);
                            res(list);
                        }, 1500);
                    });
                }
                addReward(reward) {
                    return new Promise((res, rej) => {
                        setTimeout(() => {
                            this.rewards.push(reward);
                            res();
                        }, 1500);
                    });
                }
                addRewardType(rewardType) {
                    return new Promise((res, rej) => {
                        setTimeout(() => {
                            this.rewardTypes.push(rewardType);
                            res();
                        }, 1500);
                    });
                }
                updateReward(id, reward) {
                    const index = this.rewards.findIndex((x) => x.id === id);
                    if (index < 0) {
                        return Promise.reject('Can not find challenge');
                    }
                    reward.id = id;
                    this.rewards[index] = reward;
                    return Promise.resolve();
                }
            }
            exports.default = RewardProviderMock;
            ;
            window.VueRewardProvider = new RewardProviderMock();


            /***/ }),

        /***/ "./src/plugin/cms/models/DomainItem.ts":
        /*!*********************************************!*\
          !*** ./src/plugin/cms/models/DomainItem.ts ***!
          \*********************************************/
        /*! no static exports found */
        /***/ (function(module, exports, __webpack_require__) {

            "use strict";

            Object.defineProperty(exports, "__esModule", { value: true });
            const nanoid_1 = __webpack_require__(/*! nanoid */ "./node_modules/nanoid/index.browser.js");
            class DomainItem {
                constructor(displayName, name, pd) {
                    this.displayName = displayName;
                    this.name = name;
                    this.pd = pd;
                    this.randomId = nanoid_1.nanoid();
                }
            }
            exports.default = DomainItem;


            /***/ }),

        /***/ "./src/plugin/cms/models/GameProvider.ts":
        /*!***********************************************!*\
          !*** ./src/plugin/cms/models/GameProvider.ts ***!
          \***********************************************/
        /*! no static exports found */
        /***/ (function(module, exports, __webpack_require__) {

            "use strict";

            Object.defineProperty(exports, "__esModule", { value: true });
            exports.ExtraField = exports.Activity = void 0;
            const nanoid_1 = __webpack_require__(/*! nanoid */ "./node_modules/nanoid/index.browser.js");
            class Activity {
                constructor(name, promoProvider) {
                    this.name = name;
                    this.promoProvider = promoProvider;
                    this.id = nanoid_1.nanoid();
                }
            }
            exports.Activity = Activity;
            class ExtraField {
                constructor(name, promoProvider = -1, dataType = null, description = null) {
                    this.name = name;
                    this.promoProvider = promoProvider;
                    this.dataType = dataType;
                    this.description = description;
                    this.id = nanoid_1.nanoid();
                }
            }
            exports.ExtraField = ExtraField;
            class GameProvider {
                constructor(name, games = [], domain, url,
                            // + For promotions
                            category = null, activities = [], extraFields = [] // -
                ) {
                    this.name = name;
                    this.games = games;
                    this.domain = domain;
                    this.url = url;
                    this.category = category;
                    this.activities = activities;
                    this.extraFields = extraFields;
                    this.id = nanoid_1.nanoid();
                    this.active = false;
                }
                addGame(game) {
                    this.games.push(game);
                }
            }
            exports.default = GameProvider;


            /***/ }),

        /***/ "./src/plugin/cms/models/LayoutBannerItem.ts":
        /*!***************************************************!*\
          !*** ./src/plugin/cms/models/LayoutBannerItem.ts ***!
          \***************************************************/
        /*! no static exports found */
        /***/ (function(module, exports, __webpack_require__) {

            "use strict";

            Object.defineProperty(exports, "__esModule", { value: true });
            const nanoid_1 = __webpack_require__(/*! nanoid */ "./node_modules/nanoid/index.browser.js");
            class LayoutBannerItem {
                constructor(name, image) {
                    this.id = nanoid_1.nanoid();
                    this.url = '';
                    this.image = '';
                    this.display_text = '';
                    this.from = '';
                    this.to = '';
                    this.gameID = '';
                    this.terms_url = '';
                    this.runcount = 0;
                    this.name = name;
                    this.image = image;
                }
            }
            exports.default = LayoutBannerItem;


            /***/ }),

        /***/ "./src/plugin/components/Category.ts":
        /*!*******************************************!*\
          !*** ./src/plugin/components/Category.ts ***!
          \*******************************************/
        /*! no static exports found */
        /***/ (function(module, exports, __webpack_require__) {

            "use strict";

            Object.defineProperty(exports, "__esModule", { value: true });
            const nanoid_1 = __webpack_require__(/*! nanoid */ "./node_modules/nanoid/index.browser.js");
            class Category {
                constructor(name, domain) {
                    this.name = name;
                    this.domain = domain;
                    this.id = nanoid_1.nanoid();
                }
            }
            exports.default = Category;


            /***/ }),

        /***/ "./src/plugin/promotions/reward/Reward.ts":
        /*!************************************************!*\
          !*** ./src/plugin/promotions/reward/Reward.ts ***!
          \************************************************/
        /*! no static exports found */
        /***/ (function(module, exports, __webpack_require__) {

            "use strict";

            Object.defineProperty(exports, "__esModule", { value: true });
            exports.Reward = exports.RewardType = void 0;
            const nanoid_1 = __webpack_require__(/*! nanoid */ "./node_modules/nanoid/index.browser.js");
            class RewardType {
                constructor(name = '', domain, provider, category) {
                    this.name = name;
                    this.domain = domain;
                    this.provider = provider;
                    this.category = category;
                    this.id = nanoid_1.nanoid();
                    this.fields = [];
                }
                get hasFields() {
                    return this.fields.length > 0;
                }
            }
            exports.RewardType = RewardType;
            class Reward {
                constructor(name, types, code, description, enabled, domain) {
                    this.name = name;
                    this.types = types;
                    this.code = code;
                    this.description = description;
                    this.enabled = enabled;
                    this.domain = domain;
                    this.id = nanoid_1.nanoid();
                }
                get completed() {
                    return this.name && this.domain && this.types.length > 0 && this.code && this.description;
                }
            }
            exports.Reward = Reward;


            /***/ })

        /******/ });
//# sourceMappingURL=data:application/json;charset=utf-8;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIndlYnBhY2s6Ly8vd2VicGFjay9ib290c3RyYXAiLCJ3ZWJwYWNrOi8vLy4vbm9kZV9tb2R1bGVzL25hbm9pZC9pbmRleC5icm93c2VyLmpzIiwid2VicGFjazovLy8uL25vZGVfbW9kdWxlcy9uYW5vaWQvdXJsLWFscGhhYmV0L2luZGV4LmpzIiwid2VicGFjazovLy8uL3NyYy9tb2NrL3Byb3ZpZGVyL0RvbWFpblByb3ZpZGVyTW9jay50cyIsIndlYnBhY2s6Ly8vLi9zcmMvbW9jay9wcm92aWRlci9HYW1lc1Byb3ZpZGVyTW9jay50cyIsIndlYnBhY2s6Ly8vLi9zcmMvbW9jay9wcm92aWRlci9SZXdhcmRQcm92aWRlck1vY2sudHMiLCJ3ZWJwYWNrOi8vLy4vc3JjL3BsdWdpbi9jbXMvbW9kZWxzL0RvbWFpbkl0ZW0udHMiLCJ3ZWJwYWNrOi8vLy4vc3JjL3BsdWdpbi9jbXMvbW9kZWxzL0dhbWVQcm92aWRlci50cyIsIndlYnBhY2s6Ly8vLi9zcmMvcGx1Z2luL2Ntcy9tb2RlbHMvTGF5b3V0QmFubmVySXRlbS50cyIsIndlYnBhY2s6Ly8vLi9zcmMvcGx1Z2luL2NvbXBvbmVudHMvQ2F0ZWdvcnkudHMiLCJ3ZWJwYWNrOi8vLy4vc3JjL3BsdWdpbi9wcm9tb3Rpb25zL3Jld2FyZC9SZXdhcmQudHMiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6IjtRQUFBO1FBQ0E7O1FBRUE7UUFDQTs7UUFFQTtRQUNBO1FBQ0E7UUFDQTtRQUNBO1FBQ0E7UUFDQTtRQUNBO1FBQ0E7UUFDQTs7UUFFQTtRQUNBOztRQUVBO1FBQ0E7O1FBRUE7UUFDQTtRQUNBOzs7UUFHQTtRQUNBOztRQUVBO1FBQ0E7O1FBRUE7UUFDQTtRQUNBO1FBQ0EsMENBQTBDLGdDQUFnQztRQUMxRTtRQUNBOztRQUVBO1FBQ0E7UUFDQTtRQUNBLHdEQUF3RCxrQkFBa0I7UUFDMUU7UUFDQSxpREFBaUQsY0FBYztRQUMvRDs7UUFFQTtRQUNBO1FBQ0E7UUFDQTtRQUNBO1FBQ0E7UUFDQTtRQUNB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aUJBQWlCLG1CQUFPLENBQUMsc0RBQVE7QUFDakM7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQSIsImZpbGUiOiJSZXdhcmRQcm92aWRlck1vY2suanMiLCJzb3VyY2VzQ29udGVudCI6WyIgXHQvLyBUaGUgbW9kdWxlIGNhY2hlXG4gXHR2YXIgaW5zdGFsbGVkTW9kdWxlcyA9IHt9O1xuXG4gXHQvLyBUaGUgcmVxdWlyZSBmdW5jdGlvblxuIFx0ZnVuY3Rpb24gX193ZWJwYWNrX3JlcXVpcmVfXyhtb2R1bGVJZCkge1xuXG4gXHRcdC8vIENoZWNrIGlmIG1vZHVsZSBpcyBpbiBjYWNoZVxuIFx0XHRpZihpbnN0YWxsZWRNb2R1bGVzW21vZHVsZUlkXSkge1xuIFx0XHRcdHJldHVybiBpbnN0YWxsZWRNb2R1bGVzW21vZHVsZUlkXS5leHBvcnRzO1xuIFx0XHR9XG4gXHRcdC8vIENyZWF0ZSBhIG5ldyBtb2R1bGUgKGFuZCBwdXQgaXQgaW50byB0aGUgY2FjaGUpXG4gXHRcdHZhciBtb2R1bGUgPSBpbnN0YWxsZWRNb2R1bGVzW21vZHVsZUlkXSA9IHtcbiBcdFx0XHRpOiBtb2R1bGVJZCxcbiBcdFx0XHRsOiBmYWxzZSxcbiBcdFx0XHRleHBvcnRzOiB7fVxuIFx0XHR9O1xuXG4gXHRcdC8vIEV4ZWN1dGUgdGhlIG1vZHVsZSBmdW5jdGlvblxuIFx0XHRtb2R1bGVzW21vZHVsZUlkXS5jYWxsKG1vZHVsZS5leHBvcnRzLCBtb2R1bGUsIG1vZHVsZS5leHBvcnRzLCBfX3dlYnBhY2tfcmVxdWlyZV9fKTtcblxuIFx0XHQvLyBGbGFnIHRoZSBtb2R1bGUgYXMgbG9hZGVkXG4gXHRcdG1vZHVsZS5sID0gdHJ1ZTtcblxuIFx0XHQvLyBSZXR1cm4gdGhlIGV4cG9ydHMgb2YgdGhlIG1vZHVsZVxuIFx0XHRyZXR1cm4gbW9kdWxlLmV4cG9ydHM7XG4gXHR9XG5cblxuIFx0Ly8gZXhwb3NlIHRoZSBtb2R1bGVzIG9iamVjdCAoX193ZWJwYWNrX21vZHVsZXNfXylcbiBcdF9fd2VicGFja19yZXF1aXJlX18ubSA9IG1vZHVsZXM7XG5cbiBcdC8vIGV4cG9zZSB0aGUgbW9kdWxlIGNhY2hlXG4gXHRfX3dlYnBhY2tfcmVxdWlyZV9fLmMgPSBpbnN0YWxsZWRNb2R1bGVzO1xuXG4gXHQvLyBkZWZpbmUgZ2V0dGVyIGZ1bmN0aW9uIGZvciBoYXJtb255IGV4cG9ydHNcbiBcdF9fd2VicGFja19yZXF1aXJlX18uZCA9IGZ1bmN0aW9uKGV4cG9ydHMsIG5hbWUsIGdldHRlcikge1xuIFx0XHRpZighX193ZWJwYWNrX3JlcXVpcmVfXy5vKGV4cG9ydHMsIG5hbWUpKSB7XG4gXHRcdFx0T2JqZWN0LmRlZmluZVByb3BlcnR5KGV4cG9ydHMsIG5hbWUsIHsgZW51bWVyYWJsZTogdHJ1ZSwgZ2V0OiBnZXR0ZXIgfSk7XG4gXHRcdH1cbiBcdH07XG5cbiBcdC8vIGRlZmluZSBfX2VzTW9kdWxlIG9uIGV4cG9ydHNcbiBcdF9fd2VicGFja19yZXF1aXJlX18uciA9IGZ1bmN0aW9uKGV4cG9ydHMpIHtcbiBcdFx0aWYodHlwZW9mIFN5bWJvbCAhPT0gJ3VuZGVmaW5lZCcgJiYgU3ltYm9sLnRvU3RyaW5nVGFnKSB7XG4gXHRcdFx0T2JqZWN0LmRlZmluZVByb3BlcnR5KGV4cG9ydHMsIFN5bWJvbC50b1N0cmluZ1RhZywgeyB2YWx1ZTogJ01vZHVsZScgfSk7XG4gXHRcdH1cbiBcdFx0T2JqZWN0LmRlZmluZVByb3BlcnR5KGV4cG9ydHMsICdfX2VzTW9kdWxlJywgeyB2YWx1ZTogdHJ1ZSB9KTtcbiBcdH07XG5cbiBcdC8vIGNyZWF0ZSBhIGZha2UgbmFtZXNwYWNlIG9iamVjdFxuIFx0Ly8gbW9kZSAmIDE6IHZhbHVlIGlzIGEgbW9kdWxlIGlkLCByZXF1aXJlIGl0XG4gXHQvLyBtb2RlICYgMjogbWVyZ2UgYWxsIHByb3BlcnRpZXMgb2YgdmFsdWUgaW50byB0aGUgbnNcbiBcdC8vIG1vZGUgJiA0OiByZXR1cm4gdmFsdWUgd2hlbiBhbHJlYWR5IG5zIG9iamVjdFxuIFx0Ly8gbW9kZSAmIDh8MTogYmVoYXZlIGxpa2UgcmVxdWlyZVxuIFx0X193ZWJwYWNrX3JlcXVpcmVfXy50ID0gZnVuY3Rpb24odmFsdWUsIG1vZGUpIHtcbiBcdFx0aWYobW9kZSAmIDEpIHZhbHVlID0gX193ZWJwYWNrX3JlcXVpcmVfXyh2YWx1ZSk7XG4gXHRcdGlmKG1vZGUgJiA4KSByZXR1cm4gdmFsdWU7XG4gXHRcdGlmKChtb2RlICYgNCkgJiYgdHlwZW9mIHZhbHVlID09PSAnb2JqZWN0JyAmJiB2YWx1ZSAmJiB2YWx1ZS5fX2VzTW9kdWxlKSByZXR1cm4gdmFsdWU7XG4gXHRcdHZhciBucyA9IE9iamVjdC5jcmVhdGUobnVsbCk7XG4gXHRcdF9fd2VicGFja19yZXF1aXJlX18ucihucyk7XG4gXHRcdE9iamVjdC5kZWZpbmVQcm9wZXJ0eShucywgJ2RlZmF1bHQnLCB7IGVudW1lcmFibGU6IHRydWUsIHZhbHVlOiB2YWx1ZSB9KTtcbiBcdFx0aWYobW9kZSAmIDIgJiYgdHlwZW9mIHZhbHVlICE9ICdzdHJpbmcnKSBmb3IodmFyIGtleSBpbiB2YWx1ZSkgX193ZWJwYWNrX3JlcXVpcmVfXy5kKG5zLCBrZXksIGZ1bmN0aW9uKGtleSkgeyByZXR1cm4gdmFsdWVba2V5XTsgfS5iaW5kKG51bGwsIGtleSkpO1xuIFx0XHRyZXR1cm4gbnM7XG4gXHR9O1xuXG4gXHQvLyBnZXREZWZhdWx0RXhwb3J0IGZ1bmN0aW9uIGZvciBjb21wYXRpYmlsaXR5IHdpdGggbm9uLWhhcm1vbnkgbW9kdWxlc1xuIFx0X193ZWJwYWNrX3JlcXVpcmVfXy5uID0gZnVuY3Rpb24obW9kdWxlKSB7XG4gXHRcdHZhciBnZXR0ZXIgPSBtb2R1bGUgJiYgbW9kdWxlLl9fZXNNb2R1bGUgP1xuIFx0XHRcdGZ1bmN0aW9uIGdldERlZmF1bHQoKSB7IHJldHVybiBtb2R1bGVbJ2RlZmF1bHQnXTsgfSA6XG4gXHRcdFx0ZnVuY3Rpb24gZ2V0TW9kdWxlRXhwb3J0cygpIHsgcmV0dXJuIG1vZHVsZTsgfTtcbiBcdFx0X193ZWJwYWNrX3JlcXVpcmVfXy5kKGdldHRlciwgJ2EnLCBnZXR0ZXIpO1xuIFx0XHRyZXR1cm4gZ2V0dGVyO1xuIFx0fTtcblxuIFx0Ly8gT2JqZWN0LnByb3RvdHlwZS5oYXNPd25Qcm9wZXJ0eS5jYWxsXG4gXHRfX3dlYnBhY2tfcmVxdWlyZV9fLm8gPSBmdW5jdGlvbihvYmplY3QsIHByb3BlcnR5KSB7IHJldHVybiBPYmplY3QucHJvdG90eXBlLmhhc093blByb3BlcnR5LmNhbGwob2JqZWN0LCBwcm9wZXJ0eSk7IH07XG5cbiBcdC8vIF9fd2VicGFja19wdWJsaWNfcGF0aF9fXG4gXHRfX3dlYnBhY2tfcmVxdWlyZV9fLnAgPSBcIlwiO1xuXG5cbiBcdC8vIExvYWQgZW50cnkgbW9kdWxlIGFuZCByZXR1cm4gZXhwb3J0c1xuIFx0cmV0dXJuIF9fd2VicGFja19yZXF1aXJlX18oX193ZWJwYWNrX3JlcXVpcmVfXy5zID0gXCIuL3NyYy9tb2NrL3Byb3ZpZGVyL1Jld2FyZFByb3ZpZGVyTW9jay50c1wiKTtcbiIsImV4cG9ydCB7IHVybEFscGhhYmV0IH0gZnJvbSAnLi91cmwtYWxwaGFiZXQvaW5kZXguanMnXG5leHBvcnQgbGV0IHJhbmRvbSA9IGJ5dGVzID0+IGNyeXB0by5nZXRSYW5kb21WYWx1ZXMobmV3IFVpbnQ4QXJyYXkoYnl0ZXMpKVxuZXhwb3J0IGxldCBjdXN0b21SYW5kb20gPSAoYWxwaGFiZXQsIGRlZmF1bHRTaXplLCBnZXRSYW5kb20pID0+IHtcbiAgbGV0IG1hc2sgPSAoMiA8PCAoTWF0aC5sb2coYWxwaGFiZXQubGVuZ3RoIC0gMSkgLyBNYXRoLkxOMikpIC0gMVxuICBsZXQgc3RlcCA9IC1+KCgxLjYgKiBtYXNrICogZGVmYXVsdFNpemUpIC8gYWxwaGFiZXQubGVuZ3RoKVxuICByZXR1cm4gKHNpemUgPSBkZWZhdWx0U2l6ZSkgPT4ge1xuICAgIGxldCBpZCA9ICcnXG4gICAgd2hpbGUgKHRydWUpIHtcbiAgICAgIGxldCBieXRlcyA9IGdldFJhbmRvbShzdGVwKVxuICAgICAgbGV0IGogPSBzdGVwXG4gICAgICB3aGlsZSAoai0tKSB7XG4gICAgICAgIGlkICs9IGFscGhhYmV0W2J5dGVzW2pdICYgbWFza10gfHwgJydcbiAgICAgICAgaWYgKGlkLmxlbmd0aCA9PT0gc2l6ZSkgcmV0dXJuIGlkXG4gICAgICB9XG4gICAgfVxuICB9XG59XG5leHBvcnQgbGV0IGN1c3RvbUFscGhhYmV0ID0gKGFscGhhYmV0LCBzaXplID0gMjEpID0+XG4gIGN1c3RvbVJhbmRvbShhbHBoYWJldCwgc2l6ZSwgcmFuZG9tKVxuZXhwb3J0IGxldCBuYW5vaWQgPSAoc2l6ZSA9IDIxKSA9PlxuICBjcnlwdG8uZ2V0UmFuZG9tVmFsdWVzKG5ldyBVaW50OEFycmF5KHNpemUpKS5yZWR1Y2UoKGlkLCBieXRlKSA9PiB7XG4gICAgYnl0ZSAmPSA2M1xuICAgIGlmIChieXRlIDwgMzYpIHtcbiAgICAgIGlkICs9IGJ5dGUudG9TdHJpbmcoMzYpXG4gICAgfSBlbHNlIGlmIChieXRlIDwgNjIpIHtcbiAgICAgIGlkICs9IChieXRlIC0gMjYpLnRvU3RyaW5nKDM2KS50b1VwcGVyQ2FzZSgpXG4gICAgfSBlbHNlIGlmIChieXRlID4gNjIpIHtcbiAgICAgIGlkICs9ICctJ1xuICAgIH0gZWxzZSB7XG4gICAgICBpZCArPSAnXydcbiAgICB9XG4gICAgcmV0dXJuIGlkXG4gIH0sICcnKVxuIiwiZXhwb3J0IGNvbnN0IHVybEFscGhhYmV0ID1cbiAgJ3VzZWFuZG9tLTI2VDE5ODM0MFBYNzVweEpBQ0tWRVJZTUlOREJVU0hXT0xGX0dRWmJmZ2hqa2xxdnd5enJpY3QnXG4iLCJcInVzZSBzdHJpY3RcIjtcclxudmFyIF9faW1wb3J0RGVmYXVsdCA9ICh0aGlzICYmIHRoaXMuX19pbXBvcnREZWZhdWx0KSB8fCBmdW5jdGlvbiAobW9kKSB7XHJcbiAgICByZXR1cm4gKG1vZCAmJiBtb2QuX19lc01vZHVsZSkgPyBtb2QgOiB7IFwiZGVmYXVsdFwiOiBtb2QgfTtcclxufTtcclxuT2JqZWN0LmRlZmluZVByb3BlcnR5KGV4cG9ydHMsIFwiX19lc01vZHVsZVwiLCB7IHZhbHVlOiB0cnVlIH0pO1xyXG5jb25zdCBEb21haW5JdGVtXzEgPSBfX2ltcG9ydERlZmF1bHQocmVxdWlyZShcIkAvcGx1Z2luL2Ntcy9tb2RlbHMvRG9tYWluSXRlbVwiKSk7XHJcbmNsYXNzIERvbWFpblByb3ZpZGVyTW9jayB7XHJcbiAgICBnZXREb21haW5zKCkge1xyXG4gICAgICAgIC8vIERhdGEgdGFrZW4gZnJvbSAkdXNlclNlcnZpY2UuZG9tYWluc1dpdGhBbnlSb2xlKFsnQURNSU4nXSlcclxuICAgICAgICByZXR1cm4gbmV3IFByb21pc2UoKHJlcykgPT4ge1xyXG4gICAgICAgICAgICBjb25zdCBkb21haW5zID0gW1xyXG4gICAgICAgICAgICAgICAgeyBuYW1lOiAnbGl2ZXNjb3JlJywgcGQ6IGZhbHNlLCBkaXNwbGF5TmFtZTogJ0xpdmVzY29yZSBEZXZlbG9wIEFkbWluJyB9LFxyXG4gICAgICAgICAgICAgICAgeyBuYW1lOiAnbGl2ZXNjb3JlX3VrJywgcGQ6IHRydWUsIGRpc3BsYXlOYW1lOiAnTGl2ZXNjb3JlIFVLJyB9LFxyXG4gICAgICAgICAgICAgICAgeyBuYW1lOiAnbGl2ZXNjb3JlX25pZ2VyaWEnLCBwZDogdHJ1ZSwgZGlzcGxheU5hbWU6ICdMaXZlc2NvcmUgIE5pZ2VyaWEnIH0sXHJcbiAgICAgICAgICAgICAgICB7IG5hbWU6ICdsaXZlc2NvcmVfbWVkaWEnLCBwZDogdHJ1ZSwgZGlzcGxheU5hbWU6ICdMaXZlc2NvcmUgTWVkaWEnIH0sXHJcbiAgICAgICAgICAgICAgICB7IG5hbWU6ICdsaXZlc2NvcmVfbmwnLCBwZDogdHJ1ZSwgZGlzcGxheU5hbWU6ICdMaXZlc2NvcmUgTmV0aGVybGFuZHMnIH0sXHJcbiAgICAgICAgICAgICAgICB7IG5hbWU6ICdsaXZlc2NvcmVfaWUnLCBwZDogdHJ1ZSwgZGlzcGxheU5hbWU6ICdMaXZlc2NvcmUgSXJlbGFuZCcgfSxcclxuICAgICAgICAgICAgICAgIHsgbmFtZTogJ2xpdmVzY29yZV96YScsIHBkOiBmYWxzZSwgZGlzcGxheU5hbWU6ICdMaXZlc2NvcmUgU291dGggQWZyaWNhJyB9LFxyXG4gICAgICAgICAgICAgICAgeyBuYW1lOiAndmlyZ2luYmV0X3VrJywgcGQ6IHRydWUsIGRpc3BsYXlOYW1lOiAnVmlyZ2luYmV0IFVLJyB9LFxyXG4gICAgICAgICAgICBdO1xyXG4gICAgICAgICAgICBjb25zdCBkb21haW5JdGVtcyA9IFtdO1xyXG4gICAgICAgICAgICBmb3IgKGNvbnN0IGl0ZW0gb2YgZG9tYWlucykge1xyXG4gICAgICAgICAgICAgICAgZG9tYWluSXRlbXMucHVzaChuZXcgRG9tYWluSXRlbV8xLmRlZmF1bHQoaXRlbS5kaXNwbGF5TmFtZSwgaXRlbS5uYW1lLCBpdGVtLnBkKSk7XHJcbiAgICAgICAgICAgIH1cclxuICAgICAgICAgICAgc2V0VGltZW91dCgoKSA9PiB7XHJcbiAgICAgICAgICAgICAgICByZXMoZG9tYWluSXRlbXMpO1xyXG4gICAgICAgICAgICB9LCAxMDAwKTtcclxuICAgICAgICB9KTtcclxuICAgIH1cclxufVxyXG5leHBvcnRzLmRlZmF1bHQgPSBEb21haW5Qcm92aWRlck1vY2s7XHJcbiIsIlwidXNlIHN0cmljdFwiO1xyXG52YXIgX19jcmVhdGVCaW5kaW5nID0gKHRoaXMgJiYgdGhpcy5fX2NyZWF0ZUJpbmRpbmcpIHx8IChPYmplY3QuY3JlYXRlID8gKGZ1bmN0aW9uKG8sIG0sIGssIGsyKSB7XHJcbiAgICBpZiAoazIgPT09IHVuZGVmaW5lZCkgazIgPSBrO1xyXG4gICAgT2JqZWN0LmRlZmluZVByb3BlcnR5KG8sIGsyLCB7IGVudW1lcmFibGU6IHRydWUsIGdldDogZnVuY3Rpb24oKSB7IHJldHVybiBtW2tdOyB9IH0pO1xyXG59KSA6IChmdW5jdGlvbihvLCBtLCBrLCBrMikge1xyXG4gICAgaWYgKGsyID09PSB1bmRlZmluZWQpIGsyID0gaztcclxuICAgIG9bazJdID0gbVtrXTtcclxufSkpO1xyXG52YXIgX19zZXRNb2R1bGVEZWZhdWx0ID0gKHRoaXMgJiYgdGhpcy5fX3NldE1vZHVsZURlZmF1bHQpIHx8IChPYmplY3QuY3JlYXRlID8gKGZ1bmN0aW9uKG8sIHYpIHtcclxuICAgIE9iamVjdC5kZWZpbmVQcm9wZXJ0eShvLCBcImRlZmF1bHRcIiwgeyBlbnVtZXJhYmxlOiB0cnVlLCB2YWx1ZTogdiB9KTtcclxufSkgOiBmdW5jdGlvbihvLCB2KSB7XHJcbiAgICBvW1wiZGVmYXVsdFwiXSA9IHY7XHJcbn0pO1xyXG52YXIgX19pbXBvcnRTdGFyID0gKHRoaXMgJiYgdGhpcy5fX2ltcG9ydFN0YXIpIHx8IGZ1bmN0aW9uIChtb2QpIHtcclxuICAgIGlmIChtb2QgJiYgbW9kLl9fZXNNb2R1bGUpIHJldHVybiBtb2Q7XHJcbiAgICB2YXIgcmVzdWx0ID0ge307XHJcbiAgICBpZiAobW9kICE9IG51bGwpIGZvciAodmFyIGsgaW4gbW9kKSBpZiAoayAhPT0gXCJkZWZhdWx0XCIgJiYgT2JqZWN0LnByb3RvdHlwZS5oYXNPd25Qcm9wZXJ0eS5jYWxsKG1vZCwgaykpIF9fY3JlYXRlQmluZGluZyhyZXN1bHQsIG1vZCwgayk7XHJcbiAgICBfX3NldE1vZHVsZURlZmF1bHQocmVzdWx0LCBtb2QpO1xyXG4gICAgcmV0dXJuIHJlc3VsdDtcclxufTtcclxudmFyIF9fYXdhaXRlciA9ICh0aGlzICYmIHRoaXMuX19hd2FpdGVyKSB8fCBmdW5jdGlvbiAodGhpc0FyZywgX2FyZ3VtZW50cywgUCwgZ2VuZXJhdG9yKSB7XHJcbiAgICBmdW5jdGlvbiBhZG9wdCh2YWx1ZSkgeyByZXR1cm4gdmFsdWUgaW5zdGFuY2VvZiBQID8gdmFsdWUgOiBuZXcgUChmdW5jdGlvbiAocmVzb2x2ZSkgeyByZXNvbHZlKHZhbHVlKTsgfSk7IH1cclxuICAgIHJldHVybiBuZXcgKFAgfHwgKFAgPSBQcm9taXNlKSkoZnVuY3Rpb24gKHJlc29sdmUsIHJlamVjdCkge1xyXG4gICAgICAgIGZ1bmN0aW9uIGZ1bGZpbGxlZCh2YWx1ZSkgeyB0cnkgeyBzdGVwKGdlbmVyYXRvci5uZXh0KHZhbHVlKSk7IH0gY2F0Y2ggKGUpIHsgcmVqZWN0KGUpOyB9IH1cclxuICAgICAgICBmdW5jdGlvbiByZWplY3RlZCh2YWx1ZSkgeyB0cnkgeyBzdGVwKGdlbmVyYXRvcltcInRocm93XCJdKHZhbHVlKSk7IH0gY2F0Y2ggKGUpIHsgcmVqZWN0KGUpOyB9IH1cclxuICAgICAgICBmdW5jdGlvbiBzdGVwKHJlc3VsdCkgeyByZXN1bHQuZG9uZSA/IHJlc29sdmUocmVzdWx0LnZhbHVlKSA6IGFkb3B0KHJlc3VsdC52YWx1ZSkudGhlbihmdWxmaWxsZWQsIHJlamVjdGVkKTsgfVxyXG4gICAgICAgIHN0ZXAoKGdlbmVyYXRvciA9IGdlbmVyYXRvci5hcHBseSh0aGlzQXJnLCBfYXJndW1lbnRzIHx8IFtdKSkubmV4dCgpKTtcclxuICAgIH0pO1xyXG59O1xyXG52YXIgX19pbXBvcnREZWZhdWx0ID0gKHRoaXMgJiYgdGhpcy5fX2ltcG9ydERlZmF1bHQpIHx8IGZ1bmN0aW9uIChtb2QpIHtcclxuICAgIHJldHVybiAobW9kICYmIG1vZC5fX2VzTW9kdWxlKSA/IG1vZCA6IHsgXCJkZWZhdWx0XCI6IG1vZCB9O1xyXG59O1xyXG5PYmplY3QuZGVmaW5lUHJvcGVydHkoZXhwb3J0cywgXCJfX2VzTW9kdWxlXCIsIHsgdmFsdWU6IHRydWUgfSk7XHJcbmNvbnN0IEdhbWVQcm92aWRlcl8xID0gX19pbXBvcnRTdGFyKHJlcXVpcmUoXCJAL3BsdWdpbi9jbXMvbW9kZWxzL0dhbWVQcm92aWRlclwiKSk7XHJcbmNvbnN0IExheW91dEJhbm5lckl0ZW1fMSA9IF9faW1wb3J0RGVmYXVsdChyZXF1aXJlKFwiQC9wbHVnaW4vY21zL21vZGVscy9MYXlvdXRCYW5uZXJJdGVtXCIpKTtcclxuY29uc3QgQ2F0ZWdvcnlfMSA9IF9faW1wb3J0RGVmYXVsdChyZXF1aXJlKFwiQC9wbHVnaW4vY29tcG9uZW50cy9DYXRlZ29yeVwiKSk7XHJcbmNvbnN0IERvbWFpblByb3ZpZGVyTW9ja18xID0gX19pbXBvcnREZWZhdWx0KHJlcXVpcmUoXCIuL0RvbWFpblByb3ZpZGVyTW9ja1wiKSk7XHJcbmNsYXNzIEdhbWVzUHJvdmlkZXJNb2NrIHtcclxuICAgIGNvbnN0cnVjdG9yKCkge1xyXG4gICAgICAgIHRoaXMuZ2FtZVByb3ZpZGVycyA9IFtdO1xyXG4gICAgICAgIHRoaXMuYXBwbHlNb2NrRGF0YSgpO1xyXG4gICAgfVxyXG4gICAgYXBwbHlNb2NrRGF0YSgpIHtcclxuICAgICAgICByZXR1cm4gX19hd2FpdGVyKHRoaXMsIHZvaWQgMCwgdm9pZCAwLCBmdW5jdGlvbiogKCkge1xyXG4gICAgICAgICAgICBjb25zdCBkcCA9IG5ldyBEb21haW5Qcm92aWRlck1vY2tfMS5kZWZhdWx0KCk7XHJcbiAgICAgICAgICAgIGNvbnN0IGRvbWFpbnMgPSB5aWVsZCBkcC5nZXREb21haW5zKCk7XHJcbiAgICAgICAgICAgIGNvbnN0IGRvbWFpblVrID0gZG9tYWluc1swXTtcclxuICAgICAgICAgICAgY29uc3QgdXNlckNhdGVnb3J5ID0gbmV3IENhdGVnb3J5XzEuZGVmYXVsdCgndXNlcicsIGRvbWFpblVrKTtcclxuICAgICAgICAgICAgY29uc3QgY2FzaW5vQ2F0ZWdvcnkgPSBuZXcgQ2F0ZWdvcnlfMS5kZWZhdWx0KCdjYXNpbm8nLCBkb21haW5Vayk7XHJcbiAgICAgICAgICAgIHRoaXMuZ2FtZVByb3ZpZGVycyA9IFtcclxuICAgICAgICAgICAgICAgIG5ldyBHYW1lUHJvdmlkZXJfMS5kZWZhdWx0KCdSb3hvcicsIFtcclxuICAgICAgICAgICAgICAgICAgICBuZXcgTGF5b3V0QmFubmVySXRlbV8xLmRlZmF1bHQoJzEwcCBSb3VsZXR0ZScsICdodHRwczovL2xiby5saXRoaXVtLWRldmVsb3AubHMtZy5uZXQvaW1hZ2VzL2xvZ29fd2lkZS5wbmcnKSxcclxuICAgICAgICAgICAgICAgICAgICBuZXcgTGF5b3V0QmFubmVySXRlbV8xLmRlZmF1bHQoJzIwcCBSb3VsZXR0ZScsICdodHRwczovL2xiby5saXRoaXVtLWRldmVsb3AubHMtZy5uZXQvaW1hZ2VzL2xvZ29fd2lkZS5wbmcnKSxcclxuICAgICAgICAgICAgICAgICAgICBuZXcgTGF5b3V0QmFubmVySXRlbV8xLmRlZmF1bHQoJ0FjdGlvbiBCYW5rJywgJ2h0dHBzOi8vbGJvLmxpdGhpdW0tZGV2ZWxvcC5scy1nLm5ldC9pbWFnZXMvbG9nb193aWRlLnBuZycpLFxyXG4gICAgICAgICAgICAgICAgICAgIG5ldyBMYXlvdXRCYW5uZXJJdGVtXzEuZGVmYXVsdCgnQXJvdW5kIFRoZSBSZWVscycsICdodHRwczovL2xiby5saXRoaXVtLWRldmVsb3AubHMtZy5uZXQvaW1hZ2VzL2xvZ29fd2lkZS5wbmcnKVxyXG4gICAgICAgICAgICAgICAgXSwgJ2xpdmVzY29yZV91aycsICdzdmMtcmV3YXJkLXByLWNhc2luby1yb3hvcicsIGNhc2lub0NhdGVnb3J5LCBbbmV3IEdhbWVQcm92aWRlcl8xLkFjdGl2aXR5KCd3YWdlcicsIDIpLCBuZXcgR2FtZVByb3ZpZGVyXzEuQWN0aXZpdHkoJ3dpbicsIDIpXSwgW25ldyBHYW1lUHJvdmlkZXJfMS5FeHRyYUZpZWxkKCdnYW1lJywgMiksIG5ldyBHYW1lUHJvdmlkZXJfMS5FeHRyYUZpZWxkKCdnYW1lX3R5cGUnLCAyKV0pLFxyXG4gICAgICAgICAgICAgICAgbmV3IEdhbWVQcm92aWRlcl8xLmRlZmF1bHQoJ01pY3JvZ2FtaW5nJywgW1xyXG4gICAgICAgICAgICAgICAgICAgIG5ldyBMYXlvdXRCYW5uZXJJdGVtXzEuZGVmYXVsdCgnTWljcm9nYW1pbmcgQm9vayBPZiBPeicsICdodHRwczovL2xiby5saXRoaXVtLWRldmVsb3AubHMtZy5uZXQvaW1hZ2VzL2xvZ29fd2lkZS5wbmcnKSxcclxuICAgICAgICAgICAgICAgICAgICBuZXcgTGF5b3V0QmFubmVySXRlbV8xLmRlZmF1bHQoJ09uIFRoZSBIb3VzZSBSb3VsZXR0ZScsICdodHRwczovL2xiby5saXRoaXVtLWRldmVsb3AubHMtZy5uZXQvaW1hZ2VzL2xvZ29fd2lkZS5wbmcnKSxcclxuICAgICAgICAgICAgICAgICAgICBuZXcgTGF5b3V0QmFubmVySXRlbV8xLmRlZmF1bHQoJ1Bob2VuaXggSmFja3BvdCcsICdodHRwczovL2xiby5saXRoaXVtLWRldmVsb3AubHMtZy5uZXQvaW1hZ2VzL2xvZ29fd2lkZS5wbmcnKSxcclxuICAgICAgICAgICAgICAgICAgICBuZXcgTGF5b3V0QmFubmVySXRlbV8xLmRlZmF1bHQoJ0xpZ2h0bmluZyBSb3VsZXR0ZScsICdodHRwczovL2xiby5saXRoaXVtLWRldmVsb3AubHMtZy5uZXQvaW1hZ2VzL2xvZ29fd2lkZS5wbmcnKVxyXG4gICAgICAgICAgICAgICAgXSwgJ2xpdmVzY29yZV91aycsICdzdmMtcmV3YXJkLXByLWNhc2luby1taWNyb2dhbWluZycpLFxyXG4gICAgICAgICAgICAgICAgbmV3IEdhbWVQcm92aWRlcl8xLmRlZmF1bHQoJ1VzZXInLCBbXSwgJ2xpdmVzY29yZV91aycsICdzdmMtcHJvbW8tcHJvdmlkZXItdXNlcicsIHVzZXJDYXRlZ29yeSwgW25ldyBHYW1lUHJvdmlkZXJfMS5BY3Rpdml0eSgnbG9naW4tc3VjY2VzcycsIDEpXSwgW25ldyBHYW1lUHJvdmlkZXJfMS5FeHRyYUZpZWxkKCdkYXlzT2ZXZWVrJywgMSksIG5ldyBHYW1lUHJvdmlkZXJfMS5FeHRyYUZpZWxkKCdncmFudWxhcml0eScsIDEpLCBuZXcgR2FtZVByb3ZpZGVyXzEuRXh0cmFGaWVsZCgnY29uc2VjdXRpdmVMb2dpbnMnLCAxKV0pLFxyXG4gICAgICAgICAgICAgICAgbmV3IEdhbWVQcm92aWRlcl8xLmRlZmF1bHQoJ1Nwb3J0c2Jvb2snLCBbXSwgJ2xpdmVzY29yZV91aycsICdzdmMtcmV3YXJkLXByLXNwb3J0c2Jvb2stc2J0JyksXHJcbiAgICAgICAgICAgICAgICBuZXcgR2FtZVByb3ZpZGVyXzEuZGVmYXVsdCgnaUZvcml1bScsIFtdLCAnbGl2ZXNjb3JlX3VrJywgJ3N2Yy1yZXdhcmQtcHItY2FzaW5vLWlmb3JpdW0nKVxyXG4gICAgICAgICAgICBdO1xyXG4gICAgICAgIH0pO1xyXG4gICAgfVxyXG4gICAgZ2V0R2FtZXNCeURvbWFpbkFuZEVuYWJsZWQoZG9tYWluTmFtZSwgZW5hYmxlZCwgdmlzaWJsZSwgY2hhbm5lbCkge1xyXG4gICAgICAgIC8vIFNoYW1lbGVzc2x5IGR1bXBlZCBmcm9tIHRoZSBBUElcclxuICAgICAgICByZXR1cm4gbmV3IFByb21pc2UoKHJlcykgPT4ge1xyXG4gICAgICAgICAgICBQcm9taXNlLnJlc29sdmUoSlNPTi5wYXJzZShgW3tcImlkXCI6MTEsXCJuYW1lXCI6XCIxMHAgUm91bGV0dGUnJydcIixcImRvbWFpblwiOntcImlkXCI6MTAsXCJ2ZXJzaW9uXCI6MCxcIm5hbWVcIjpcImxpdmVzY29yZV91a1wifSxcInByb3ZpZGVyR2FtZUlkXCI6XCJwbGF5LTEwcC1yb3VsZXR0ZVwiLFwiZW5hYmxlZFwiOmZhbHNlLFwidmlzaWJsZVwiOnRydWUsXCJsb2NrZWRcIjpmYWxzZSxcImxvY2tlZE1lc3NhZ2VcIjpudWxsLFwiaGFzTG9ja0ltYWdlXCI6ZmFsc2UsXCJndWlkXCI6XCJzZXJ2aWNlLWNhc2luby1wcm92aWRlci1yb3hvcl9wbGF5LTEwcC1yb3VsZXR0ZVwiLFwiZGVzY3JpcHRpb25cIjpcIjEwcCBSb3VsZXR0ZSBkZXNjcmlwdGlvblwiLFwicnRwXCI6OTcuMyxcInByb3ZpZGVyR3VpZFwiOlwic2VydmljZS1jYXNpbm8tcHJvdmlkZXItcm94b3JcIixcImZyZWVTcGluRW5hYmxlZFwiOnRydWUsXCJmcmVlU3BpblZhbHVlUmVxdWlyZWRcIjp0cnVlLFwiZnJlZVNwaW5QbGF5VGhyb3VnaEVuYWJsZWRcIjpmYWxzZSxcImNhc2lub0NoaXBFbmFibGVkXCI6dHJ1ZSxcImdhbWVDdXJyZW5jeVwiOm51bGwsXCJnYW1lU3VwcGxpZXJcIjp7XCJpZFwiOjMsXCJ2ZXJzaW9uXCI6MSxcImRvbWFpblwiOntcImlkXCI6MTAsXCJ2ZXJzaW9uXCI6MCxcIm5hbWVcIjpcImxpdmVzY29yZV91a1wifSxcIm5hbWVcIjpcIlJveG9yIEdhbWluZ1wiLFwiZGVsZXRlZFwiOmZhbHNlfSxcImdhbWVUeXBlXCI6e1wiaWRcIjozLFwidmVyc2lvblwiOjAsXCJkb21haW5cIjp7XCJpZFwiOjEwLFwidmVyc2lvblwiOjAsXCJuYW1lXCI6XCJsaXZlc2NvcmVfdWtcIn0sXCJuYW1lXCI6XCJEZWV6dXNUZXN0XCIsXCJkZWxldGVkXCI6ZmFsc2V9LFwibGFiZWxzXCI6e1wib3NcIjp7XCJuYW1lXCI6XCJvc1wiLFwidmFsdWVcIjpcIkRlc2t0b3AsTW9iaWxlXCIsXCJkb21haW5OYW1lXCI6XCJsaXZlc2NvcmVfdWtcIixcImVuYWJsZWRcIjpmYWxzZSxcImRlbGV0ZWRcIjpmYWxzZX0sXCJUQUdcIjp7XCJuYW1lXCI6XCJUQUdcIixcInZhbHVlXCI6XCJHQU1FVEVTVEVSXCIsXCJkb21haW5OYW1lXCI6XCJsaXZlc2NvcmVfdWtcIixcImVuYWJsZWRcIjpmYWxzZSxcImRlbGV0ZWRcIjpmYWxzZX19LFwicHJvZ3Jlc3NpdmVKYWNrcG90XCI6ZmFsc2UsXCJuZXR3b3JrZWRKYWNrcG90UG9vbFwiOmZhbHNlLFwibG9jYWxKYWNrcG90UG9vbFwiOmZhbHNlLFwiZnJlZUdhbWVcIjpmYWxzZSxcImNkbkltYWdlVXJsXCI6XCJodHRwczovL3d3dy5saXZlc2NvcmViZXQuY29tL2Nhc2luby1pbWFnZXMvcGxheS0xMHAtcm91bGV0dGUtMjQwLnBuZ1wifSx7XCJpZFwiOjEyNixcIm5hbWVcIjpcIjEwcyBvciBCZXR0ZXJcIixcImRvbWFpblwiOntcImlkXCI6MTAsXCJ2ZXJzaW9uXCI6MCxcIm5hbWVcIjpcImxpdmVzY29yZV91a1wifSxcInByb3ZpZGVyR2FtZUlkXCI6XCI1MDQ0XCIsXCJlbmFibGVkXCI6dHJ1ZSxcInZpc2libGVcIjpmYWxzZSxcImxvY2tlZFwiOmZhbHNlLFwibG9ja2VkTWVzc2FnZVwiOm51bGwsXCJoYXNMb2NrSW1hZ2VcIjpmYWxzZSxcImd1aWRcIjpcInNlcnZpY2UtY2FzaW5vLXByb3ZpZGVyLWlmb3JpdW1fNTA0NFwiLFwiZGVzY3JpcHRpb25cIjpcIklmb3JpdW0gdGVzdCBwYWdlXCIsXCJydHBcIjpudWxsLFwicHJvdmlkZXJHdWlkXCI6XCJzZXJ2aWNlLWNhc2luby1wcm92aWRlci1pZm9yaXVtXCIsXCJmcmVlU3BpbkVuYWJsZWRcIjpmYWxzZSxcImZyZWVTcGluVmFsdWVSZXF1aXJlZFwiOmZhbHNlLFwiZnJlZVNwaW5QbGF5VGhyb3VnaEVuYWJsZWRcIjpmYWxzZSxcImNhc2lub0ZyZWVCZXRFbmFibGVkXCI6ZmFsc2UsXCJnYW1lQ3VycmVuY3lcIjp7XCJjdXJyZW5jeUNvZGVcIjpcIkdCUFwiLFwibWluaW11bUFtb3VudENlbnRzXCI6MX0sXCJnYW1lU3VwcGxpZXJcIjp7XCJpZFwiOjIxLFwidmVyc2lvblwiOjAsXCJkb21haW5cIjp7XCJpZFwiOjEwLFwidmVyc2lvblwiOjAsXCJuYW1lXCI6XCJsaXZlc2NvcmVfdWtcIn0sXCJuYW1lXCI6XCJpRm9yaXVtXCIsXCJkZWxldGVkXCI6ZmFsc2V9LFwiZ2FtZVR5cGVcIjpudWxsLFwibGFiZWxzXCI6e30sXCJwcm9ncmVzc2l2ZUphY2twb3RcIjpmYWxzZSxcIm5ldHdvcmtlZEphY2twb3RQb29sXCI6ZmFsc2UsXCJsb2NhbEphY2twb3RQb29sXCI6ZmFsc2UsXCJmcmVlR2FtZVwiOmZhbHNlLFwiY2RuSW1hZ2VVcmxcIjpudWxsfSx7XCJpZFwiOjEyNSxcIm5hbWVcIjpcIjExNTg4XCIsXCJkb21haW5cIjp7XCJpZFwiOjEwLFwidmVyc2lvblwiOjAsXCJuYW1lXCI6XCJsaXZlc2NvcmVfdWtcIn0sXCJwcm92aWRlckdhbWVJZFwiOlwiMTE1ODhcIixcImVuYWJsZWRcIjp0cnVlLFwidmlzaWJsZVwiOnRydWUsXCJsb2NrZWRcIjpmYWxzZSxcImxvY2tlZE1lc3NhZ2VcIjpudWxsLFwiaGFzTG9ja0ltYWdlXCI6ZmFsc2UsXCJndWlkXCI6XCJzZXJ2aWNlLWNhc2luby1wcm92aWRlci1pZm9yaXVtXzExNTg4XCIsXCJkZXNjcmlwdGlvblwiOlwiRTJFIFRlc3RzIGdhbWVcIixcInJ0cFwiOm51bGwsXCJwcm92aWRlckd1aWRcIjpcInNlcnZpY2UtY2FzaW5vLXByb3ZpZGVyLWlmb3JpdW1cIixcImZyZWVTcGluRW5hYmxlZFwiOmZhbHNlLFwiZnJlZVNwaW5WYWx1ZVJlcXVpcmVkXCI6ZmFsc2UsXCJmcmVlU3BpblBsYXlUaHJvdWdoRW5hYmxlZFwiOmZhbHNlLFwiY2FzaW5vRnJlZUJldEVuYWJsZWRcIjpmYWxzZSxcImdhbWVDdXJyZW5jeVwiOntcImN1cnJlbmN5Q29kZVwiOlwiR0JQXCIsXCJtaW5pbXVtQW1vdW50Q2VudHNcIjoxfSxcImdhbWVTdXBwbGllclwiOntcImlkXCI6MjEsXCJ2ZXJzaW9uXCI6MCxcImRvbWFpblwiOntcImlkXCI6MTAsXCJ2ZXJzaW9uXCI6MCxcIm5hbWVcIjpcImxpdmVzY29yZV91a1wifSxcIm5hbWVcIjpcImlGb3JpdW1cIixcImRlbGV0ZWRcIjpmYWxzZX0sXCJnYW1lVHlwZVwiOm51bGwsXCJsYWJlbHNcIjp7fSxcInByb2dyZXNzaXZlSmFja3BvdFwiOmZhbHNlLFwibmV0d29ya2VkSmFja3BvdFBvb2xcIjpmYWxzZSxcImxvY2FsSmFja3BvdFBvb2xcIjpmYWxzZSxcImZyZWVHYW1lXCI6ZmFsc2UsXCJjZG5JbWFnZVVybFwiOm51bGx9LHtcImlkXCI6NjMwLFwibmFtZVwiOlwiMTIzNGdhbWVyXCIsXCJkb21haW5cIjp7XCJpZFwiOjEwLFwidmVyc2lvblwiOjAsXCJuYW1lXCI6XCJsaXZlc2NvcmVfdWtcIn0sXCJwcm92aWRlckdhbWVJZFwiOlwiODk3NDY1XCIsXCJlbmFibGVkXCI6dHJ1ZSxcInZpc2libGVcIjpmYWxzZSxcImxvY2tlZFwiOmZhbHNlLFwibG9ja2VkTWVzc2FnZVwiOm51bGwsXCJoYXNMb2NrSW1hZ2VcIjpmYWxzZSxcImd1aWRcIjpcInNlcnZpY2UtY2FzaW5vLXByb3ZpZGVyLXNsb3RhcGlfODk3NDY1XCIsXCJkZXNjcmlwdGlvblwiOlwiaGRmc3Rnc3RkaHNmZ2hcIixcInJ0cFwiOm51bGwsXCJwcm92aWRlckd1aWRcIjpcInNlcnZpY2UtY2FzaW5vLXByb3ZpZGVyLXNsb3RhcGlcIixcImZyZWVTcGluRW5hYmxlZFwiOnRydWUsXCJmcmVlU3BpblZhbHVlUmVxdWlyZWRcIjpmYWxzZSxcImZyZWVTcGluUGxheVRocm91Z2hFbmFibGVkXCI6ZmFsc2UsXCJjYXNpbm9GcmVlQmV0RW5hYmxlZFwiOmZhbHNlLFwiZ2FtZUN1cnJlbmN5XCI6bnVsbCxcImdhbWVTdXBwbGllclwiOntcImlkXCI6NixcInZlcnNpb25cIjowLFwiZG9tYWluXCI6e1wiaWRcIjoxMCxcInZlcnNpb25cIjowLFwibmFtZVwiOlwibGl2ZXNjb3JlX3VrXCJ9LFwibmFtZVwiOlwiU2xvdEFQSVwiLFwiZGVsZXRlZFwiOmZhbHNlfSxcImdhbWVUeXBlXCI6bnVsbCxcImxhYmVsc1wiOnt9LFwicHJvZ3Jlc3NpdmVKYWNrcG90XCI6ZmFsc2UsXCJuZXR3b3JrZWRKYWNrcG90UG9vbFwiOmZhbHNlLFwibG9jYWxKYWNrcG90UG9vbFwiOmZhbHNlLFwiZnJlZUdhbWVcIjpmYWxzZSxcImNkbkltYWdlVXJsXCI6bnVsbH0se1wiaWRcIjoxMzUsXCJuYW1lXCI6XCIyMDIzNFwiLFwiZG9tYWluXCI6e1wiaWRcIjoxMCxcInZlcnNpb25cIjowLFwibmFtZVwiOlwibGl2ZXNjb3JlX3VrXCJ9LFwicHJvdmlkZXJHYW1lSWRcIjpcIjIwMjM0XCIsXCJlbmFibGVkXCI6dHJ1ZSxcInZpc2libGVcIjp0cnVlLFwibG9ja2VkXCI6ZmFsc2UsXCJsb2NrZWRNZXNzYWdlXCI6bnVsbCxcImhhc0xvY2tJbWFnZVwiOmZhbHNlLFwiZ3VpZFwiOlwic2VydmljZS1jYXNpbm8tcHJvdmlkZXItaWZvcml1bV8yMDIzNFwiLFwiZGVzY3JpcHRpb25cIjpcIjIwMjM0XCIsXCJydHBcIjpudWxsLFwicHJvdmlkZXJHdWlkXCI6XCJzZXJ2aWNlLWNhc2luby1wcm92aWRlci1pZm9yaXVtXCIsXCJmcmVlU3BpbkVuYWJsZWRcIjpmYWxzZSxcImZyZWVTcGluVmFsdWVSZXF1aXJlZFwiOmZhbHNlLFwiZnJlZVNwaW5QbGF5VGhyb3VnaEVuYWJsZWRcIjpmYWxzZSxcImNhc2lub0ZyZWVCZXRFbmFibGVkXCI6ZmFsc2UsXCJnYW1lQ3VycmVuY3lcIjp7XCJjdXJyZW5jeUNvZGVcIjpcIkdCUFwiLFwibWluaW11bUFtb3VudENlbnRzXCI6MX0sXCJnYW1lU3VwcGxpZXJcIjp7XCJpZFwiOjIxLFwidmVyc2lvblwiOjAsXCJkb21haW5cIjp7XCJpZFwiOjEwLFwidmVyc2lvblwiOjAsXCJuYW1lXCI6XCJsaXZlc2NvcmVfdWtcIn0sXCJuYW1lXCI6XCJpRm9yaXVtXCIsXCJkZWxldGVkXCI6ZmFsc2V9LFwiZ2FtZVR5cGVcIjpudWxsLFwibGFiZWxzXCI6e1wibnVsbFwiOntcIm5hbWVcIjpcIm51bGxcIixcInZhbHVlXCI6XCJudWxsXCIsXCJkb21haW5OYW1lXCI6XCJsaXZlc2NvcmVfdWtcIixcImVuYWJsZWRcIjpmYWxzZSxcImRlbGV0ZWRcIjpmYWxzZX19LFwicHJvZ3Jlc3NpdmVKYWNrcG90XCI6ZmFsc2UsXCJuZXR3b3JrZWRKYWNrcG90UG9vbFwiOmZhbHNlLFwibG9jYWxKYWNrcG90UG9vbFwiOmZhbHNlLFwiZnJlZUdhbWVcIjpmYWxzZSxcImNkbkltYWdlVXJsXCI6bnVsbH1dYCkpLnRoZW4oKGdhbWVzKSA9PiB7XHJcbiAgICAgICAgICAgICAgICBnYW1lcyA9IGdhbWVzLmZpbHRlcigoZ2FtZSkgPT4gZ2FtZS5lbmFibGVkID09IGVuYWJsZWQgJiYgZ2FtZS52aXNpYmxlID09IHZpc2libGUpO1xyXG4gICAgICAgICAgICAgICAgcmVzKGdhbWVzKTtcclxuICAgICAgICAgICAgfSk7XHJcbiAgICAgICAgfSk7XHJcbiAgICB9XHJcbiAgICAvLyBUT0RPOiBCYXNlIHRoaXMgb2ZmIGRvbWFpbiBhbmQgY2hhbm5lbFxyXG4gICAgZ2V0R2FtZVByb3ZpZGVycyhkb21haW4sIGNoYW5uZWwpIHtcclxuICAgICAgICByZXR1cm4gbmV3IFByb21pc2UoKHJlcykgPT4ge1xyXG4gICAgICAgICAgICBzZXRUaW1lb3V0KCgpID0+IHtcclxuICAgICAgICAgICAgICAgIHJlcyh0aGlzLmdhbWVQcm92aWRlcnMpO1xyXG4gICAgICAgICAgICB9LCAxMDAwKTtcclxuICAgICAgICB9KTtcclxuICAgIH1cclxuICAgIGdldFByb3ZpZGVyc0ZvckRvbWFpbihkb21haW5OYW1lKSB7XHJcbiAgICAgICAgcmV0dXJuIFByb21pc2UucmVzb2x2ZSh0aGlzLmdhbWVQcm92aWRlcnMuZmlsdGVyKCh4KSA9PiB4LmRvbWFpbiA9PT0gZG9tYWluTmFtZSkpO1xyXG4gICAgfVxyXG4gICAgZ2V0UHJvdmlkZXJzRm9yRG9tYWluQW5kQ2F0ZWdvcnkoZG9tYWluTmFtZSwgY2F0ZWdvcnkpIHtcclxuICAgICAgICByZXR1cm4gUHJvbWlzZS5yZXNvbHZlKHRoaXMuZ2FtZVByb3ZpZGVycy5maWx0ZXIoKHgpID0+IHsgdmFyIF9hOyByZXR1cm4geC5kb21haW4gPT09IGRvbWFpbk5hbWUgJiYgKChfYSA9IHguY2F0ZWdvcnkpID09PSBudWxsIHx8IF9hID09PSB2b2lkIDAgPyB2b2lkIDAgOiBfYS5uYW1lKSA9PT0gY2F0ZWdvcnkubmFtZTsgfSkpO1xyXG4gICAgfVxyXG59XHJcbmV4cG9ydHMuZGVmYXVsdCA9IEdhbWVzUHJvdmlkZXJNb2NrO1xyXG47XHJcbndpbmRvdy5WdWVHYW1lUHJvdmlkZXIgPSBuZXcgR2FtZXNQcm92aWRlck1vY2soKTtcclxuIiwiXCJ1c2Ugc3RyaWN0XCI7XHJcbnZhciBfX2F3YWl0ZXIgPSAodGhpcyAmJiB0aGlzLl9fYXdhaXRlcikgfHwgZnVuY3Rpb24gKHRoaXNBcmcsIF9hcmd1bWVudHMsIFAsIGdlbmVyYXRvcikge1xyXG4gICAgZnVuY3Rpb24gYWRvcHQodmFsdWUpIHsgcmV0dXJuIHZhbHVlIGluc3RhbmNlb2YgUCA/IHZhbHVlIDogbmV3IFAoZnVuY3Rpb24gKHJlc29sdmUpIHsgcmVzb2x2ZSh2YWx1ZSk7IH0pOyB9XHJcbiAgICByZXR1cm4gbmV3IChQIHx8IChQID0gUHJvbWlzZSkpKGZ1bmN0aW9uIChyZXNvbHZlLCByZWplY3QpIHtcclxuICAgICAgICBmdW5jdGlvbiBmdWxmaWxsZWQodmFsdWUpIHsgdHJ5IHsgc3RlcChnZW5lcmF0b3IubmV4dCh2YWx1ZSkpOyB9IGNhdGNoIChlKSB7IHJlamVjdChlKTsgfSB9XHJcbiAgICAgICAgZnVuY3Rpb24gcmVqZWN0ZWQodmFsdWUpIHsgdHJ5IHsgc3RlcChnZW5lcmF0b3JbXCJ0aHJvd1wiXSh2YWx1ZSkpOyB9IGNhdGNoIChlKSB7IHJlamVjdChlKTsgfSB9XHJcbiAgICAgICAgZnVuY3Rpb24gc3RlcChyZXN1bHQpIHsgcmVzdWx0LmRvbmUgPyByZXNvbHZlKHJlc3VsdC52YWx1ZSkgOiBhZG9wdChyZXN1bHQudmFsdWUpLnRoZW4oZnVsZmlsbGVkLCByZWplY3RlZCk7IH1cclxuICAgICAgICBzdGVwKChnZW5lcmF0b3IgPSBnZW5lcmF0b3IuYXBwbHkodGhpc0FyZywgX2FyZ3VtZW50cyB8fCBbXSkpLm5leHQoKSk7XHJcbiAgICB9KTtcclxufTtcclxudmFyIF9faW1wb3J0RGVmYXVsdCA9ICh0aGlzICYmIHRoaXMuX19pbXBvcnREZWZhdWx0KSB8fCBmdW5jdGlvbiAobW9kKSB7XHJcbiAgICByZXR1cm4gKG1vZCAmJiBtb2QuX19lc01vZHVsZSkgPyBtb2QgOiB7IFwiZGVmYXVsdFwiOiBtb2QgfTtcclxufTtcclxuT2JqZWN0LmRlZmluZVByb3BlcnR5KGV4cG9ydHMsIFwiX19lc01vZHVsZVwiLCB7IHZhbHVlOiB0cnVlIH0pO1xyXG5jb25zdCBDYXRlZ29yeV8xID0gX19pbXBvcnREZWZhdWx0KHJlcXVpcmUoXCJAL3BsdWdpbi9jb21wb25lbnRzL0NhdGVnb3J5XCIpKTtcclxuY29uc3QgUmV3YXJkXzEgPSByZXF1aXJlKFwiQC9wbHVnaW4vcHJvbW90aW9ucy9yZXdhcmQvUmV3YXJkXCIpO1xyXG5jb25zdCBEb21haW5Qcm92aWRlck1vY2tfMSA9IF9faW1wb3J0RGVmYXVsdChyZXF1aXJlKFwiLi9Eb21haW5Qcm92aWRlck1vY2tcIikpO1xyXG5jb25zdCBHYW1lc1Byb3ZpZGVyTW9ja18xID0gX19pbXBvcnREZWZhdWx0KHJlcXVpcmUoXCIuL0dhbWVzUHJvdmlkZXJNb2NrXCIpKTtcclxuY2xhc3MgUmV3YXJkUHJvdmlkZXJNb2NrIHtcclxuICAgIGNvbnN0cnVjdG9yKCkge1xyXG4gICAgICAgIHRoaXMucmV3YXJkVHlwZXMgPSBbXTtcclxuICAgICAgICB0aGlzLnJld2FyZHMgPSBbXTtcclxuICAgICAgICB0aGlzLmFwcGx5TW9ja0RhdGEoKTtcclxuICAgIH1cclxuICAgIGFwcGx5TW9ja0RhdGEoKSB7XHJcbiAgICAgICAgcmV0dXJuIF9fYXdhaXRlcih0aGlzLCB2b2lkIDAsIHZvaWQgMCwgZnVuY3Rpb24qICgpIHtcclxuICAgICAgICAgICAgY29uc3QgZ20gPSBuZXcgR2FtZXNQcm92aWRlck1vY2tfMS5kZWZhdWx0KCk7XHJcbiAgICAgICAgICAgIGNvbnN0IGRwID0gbmV3IERvbWFpblByb3ZpZGVyTW9ja18xLmRlZmF1bHQoKTtcclxuICAgICAgICAgICAgY29uc3QgZG9tYWlucyA9IHlpZWxkIGRwLmdldERvbWFpbnMoKTtcclxuICAgICAgICAgICAgY29uc3QgZG9tYWluVWsgPSBkb21haW5zWzBdO1xyXG4gICAgICAgICAgICBjb25zdCBwcm92aWRlclJveG9yID0gZ20uZ2FtZVByb3ZpZGVyc1swXTtcclxuICAgICAgICAgICAgY29uc3QgcHJvdmlkZXJVc2VyID0gZ20uZ2FtZVByb3ZpZGVyc1syXTtcclxuICAgICAgICAgICAgY29uc3QgdXNlckNhdGVnb3J5ID0gbmV3IENhdGVnb3J5XzEuZGVmYXVsdCgndXNlcicsIGRvbWFpblVrKTtcclxuICAgICAgICAgICAgY29uc3QgY2FzaW5vQ2F0ZWdvcnkgPSBuZXcgQ2F0ZWdvcnlfMS5kZWZhdWx0KCdjYXNpbm8nLCBkb21haW5Vayk7XHJcbiAgICAgICAgICAgIHRoaXMucmV3YXJkVHlwZXMgPSBbXHJcbiAgICAgICAgICAgICAgICBuZXcgUmV3YXJkXzEuUmV3YXJkVHlwZSgnRnJlZSBTcGlucycsIGRvbWFpblVrLCBwcm92aWRlclJveG9yLCBjYXNpbm9DYXRlZ29yeSksXHJcbiAgICAgICAgICAgICAgICBuZXcgUmV3YXJkXzEuUmV3YXJkVHlwZSgnRnJlZSBHYW1lcycsIGRvbWFpblVrLCBwcm92aWRlclJveG9yLCBjYXNpbm9DYXRlZ29yeSksXHJcbiAgICAgICAgICAgICAgICBuZXcgUmV3YXJkXzEuUmV3YXJkVHlwZSgnSW5zdGFudCByZXdhcmRzJywgZG9tYWluVWssIHByb3ZpZGVyUm94b3IsIHVzZXJDYXRlZ29yeSksXHJcbiAgICAgICAgICAgICAgICBuZXcgUmV3YXJkXzEuUmV3YXJkVHlwZSgnRnJlZSBCZXRzJywgZG9tYWluVWssIHByb3ZpZGVyUm94b3IsIHVzZXJDYXRlZ29yeSksXHJcbiAgICAgICAgICAgICAgICBuZXcgUmV3YXJkXzEuUmV3YXJkVHlwZSgnRnJlZXNwaW5zJywgZG9tYWluVWssIHByb3ZpZGVyVXNlciwgY2FzaW5vQ2F0ZWdvcnkpLFxyXG4gICAgICAgICAgICAgICAgbmV3IFJld2FyZF8xLlJld2FyZFR5cGUoJ0Nhc2gnLCBkb21haW5VaywgcHJvdmlkZXJVc2VyLCB1c2VyQ2F0ZWdvcnkpXHJcbiAgICAgICAgICAgIF07XHJcbiAgICAgICAgfSk7XHJcbiAgICB9XHJcbiAgICBnZXRSZXdhcmRzKCkge1xyXG4gICAgICAgIHJldHVybiBuZXcgUHJvbWlzZSgocmVzLCByZWopID0+IHtcclxuICAgICAgICAgICAgc2V0VGltZW91dCgoKSA9PiB7XHJcbiAgICAgICAgICAgICAgICByZXModGhpcy5yZXdhcmRzKTtcclxuICAgICAgICAgICAgfSwgMTUwMCk7XHJcbiAgICAgICAgfSk7XHJcbiAgICB9XHJcbiAgICBnZXRSZXdhcmRUeXBlcygpIHtcclxuICAgICAgICByZXR1cm4gbmV3IFByb21pc2UoKHJlcywgcmVqKSA9PiB7XHJcbiAgICAgICAgICAgIHNldFRpbWVvdXQoKCkgPT4ge1xyXG4gICAgICAgICAgICAgICAgcmVzKHRoaXMucmV3YXJkVHlwZXMpO1xyXG4gICAgICAgICAgICB9LCAxNTAwKTtcclxuICAgICAgICB9KTtcclxuICAgIH1cclxuICAgIGdldFJld2FyZFR5cGVzRm9yUHJvdmlkZXIocHJvdmlkZXIpIHtcclxuICAgICAgICByZXR1cm4gbmV3IFByb21pc2UoKHJlcykgPT4ge1xyXG4gICAgICAgICAgICBzZXRUaW1lb3V0KCgpID0+IHtcclxuICAgICAgICAgICAgICAgIGNvbnN0IGxpc3QgPSB0aGlzLnJld2FyZFR5cGVzLmZpbHRlcigodCkgPT4gdC5wcm92aWRlci51cmwgPT09IHByb3ZpZGVyLnVybCk7XHJcbiAgICAgICAgICAgICAgICByZXMobGlzdCk7XHJcbiAgICAgICAgICAgIH0sIDE1MDApO1xyXG4gICAgICAgIH0pO1xyXG4gICAgfVxyXG4gICAgZ2V0UmV3YXJkVHlwZXNGb3JQcm92aWRlckFuZENhdGVnb3J5KHByb3ZpZGVyLCBjYXRlZ29yeSkge1xyXG4gICAgICAgIHJldHVybiBuZXcgUHJvbWlzZSgocmVzKSA9PiB7XHJcbiAgICAgICAgICAgIHNldFRpbWVvdXQoKCkgPT4ge1xyXG4gICAgICAgICAgICAgICAgY29uc3QgbGlzdCA9IHRoaXMucmV3YXJkVHlwZXMuZmlsdGVyKCh0KSA9PiB0LnByb3ZpZGVyLnVybCA9PT0gcHJvdmlkZXIudXJsICYmIHQuY2F0ZWdvcnkubmFtZSA9PT0gY2F0ZWdvcnkubmFtZSk7XHJcbiAgICAgICAgICAgICAgICByZXMobGlzdCk7XHJcbiAgICAgICAgICAgIH0sIDE1MDApO1xyXG4gICAgICAgIH0pO1xyXG4gICAgfVxyXG4gICAgYWRkUmV3YXJkKHJld2FyZCkge1xyXG4gICAgICAgIHJldHVybiBuZXcgUHJvbWlzZSgocmVzLCByZWopID0+IHtcclxuICAgICAgICAgICAgc2V0VGltZW91dCgoKSA9PiB7XHJcbiAgICAgICAgICAgICAgICB0aGlzLnJld2FyZHMucHVzaChyZXdhcmQpO1xyXG4gICAgICAgICAgICAgICAgcmVzKCk7XHJcbiAgICAgICAgICAgIH0sIDE1MDApO1xyXG4gICAgICAgIH0pO1xyXG4gICAgfVxyXG4gICAgYWRkUmV3YXJkVHlwZShyZXdhcmRUeXBlKSB7XHJcbiAgICAgICAgcmV0dXJuIG5ldyBQcm9taXNlKChyZXMsIHJlaikgPT4ge1xyXG4gICAgICAgICAgICBzZXRUaW1lb3V0KCgpID0+IHtcclxuICAgICAgICAgICAgICAgIHRoaXMucmV3YXJkVHlwZXMucHVzaChyZXdhcmRUeXBlKTtcclxuICAgICAgICAgICAgICAgIHJlcygpO1xyXG4gICAgICAgICAgICB9LCAxNTAwKTtcclxuICAgICAgICB9KTtcclxuICAgIH1cclxuICAgIHVwZGF0ZVJld2FyZChpZCwgcmV3YXJkKSB7XHJcbiAgICAgICAgY29uc3QgaW5kZXggPSB0aGlzLnJld2FyZHMuZmluZEluZGV4KCh4KSA9PiB4LmlkID09PSBpZCk7XHJcbiAgICAgICAgaWYgKGluZGV4IDwgMCkge1xyXG4gICAgICAgICAgICByZXR1cm4gUHJvbWlzZS5yZWplY3QoJ0NhbiBub3QgZmluZCBjaGFsbGVuZ2UnKTtcclxuICAgICAgICB9XHJcbiAgICAgICAgcmV3YXJkLmlkID0gaWQ7XHJcbiAgICAgICAgdGhpcy5yZXdhcmRzW2luZGV4XSA9IHJld2FyZDtcclxuICAgICAgICByZXR1cm4gUHJvbWlzZS5yZXNvbHZlKCk7XHJcbiAgICB9XHJcbn1cclxuZXhwb3J0cy5kZWZhdWx0ID0gUmV3YXJkUHJvdmlkZXJNb2NrO1xyXG47XHJcbndpbmRvdy5WdWVSZXdhcmRQcm92aWRlciA9IG5ldyBSZXdhcmRQcm92aWRlck1vY2soKTtcclxuIiwiXCJ1c2Ugc3RyaWN0XCI7XHJcbk9iamVjdC5kZWZpbmVQcm9wZXJ0eShleHBvcnRzLCBcIl9fZXNNb2R1bGVcIiwgeyB2YWx1ZTogdHJ1ZSB9KTtcclxuY29uc3QgbmFub2lkXzEgPSByZXF1aXJlKFwibmFub2lkXCIpO1xyXG5jbGFzcyBEb21haW5JdGVtIHtcclxuICAgIGNvbnN0cnVjdG9yKGRpc3BsYXlOYW1lLCBuYW1lLCBwZCkge1xyXG4gICAgICAgIHRoaXMuZGlzcGxheU5hbWUgPSBkaXNwbGF5TmFtZTtcclxuICAgICAgICB0aGlzLm5hbWUgPSBuYW1lO1xyXG4gICAgICAgIHRoaXMucGQgPSBwZDtcclxuICAgICAgICB0aGlzLnJhbmRvbUlkID0gbmFub2lkXzEubmFub2lkKCk7XHJcbiAgICB9XHJcbn1cclxuZXhwb3J0cy5kZWZhdWx0ID0gRG9tYWluSXRlbTtcclxuIiwiXCJ1c2Ugc3RyaWN0XCI7XHJcbk9iamVjdC5kZWZpbmVQcm9wZXJ0eShleHBvcnRzLCBcIl9fZXNNb2R1bGVcIiwgeyB2YWx1ZTogdHJ1ZSB9KTtcclxuZXhwb3J0cy5FeHRyYUZpZWxkID0gZXhwb3J0cy5BY3Rpdml0eSA9IHZvaWQgMDtcclxuY29uc3QgbmFub2lkXzEgPSByZXF1aXJlKFwibmFub2lkXCIpO1xyXG5jbGFzcyBBY3Rpdml0eSB7XHJcbiAgICBjb25zdHJ1Y3RvcihuYW1lLCBwcm9tb1Byb3ZpZGVyKSB7XHJcbiAgICAgICAgdGhpcy5uYW1lID0gbmFtZTtcclxuICAgICAgICB0aGlzLnByb21vUHJvdmlkZXIgPSBwcm9tb1Byb3ZpZGVyO1xyXG4gICAgICAgIHRoaXMuaWQgPSBuYW5vaWRfMS5uYW5vaWQoKTtcclxuICAgIH1cclxufVxyXG5leHBvcnRzLkFjdGl2aXR5ID0gQWN0aXZpdHk7XHJcbmNsYXNzIEV4dHJhRmllbGQge1xyXG4gICAgY29uc3RydWN0b3IobmFtZSwgcHJvbW9Qcm92aWRlciA9IC0xLCBkYXRhVHlwZSA9IG51bGwsIGRlc2NyaXB0aW9uID0gbnVsbCkge1xyXG4gICAgICAgIHRoaXMubmFtZSA9IG5hbWU7XHJcbiAgICAgICAgdGhpcy5wcm9tb1Byb3ZpZGVyID0gcHJvbW9Qcm92aWRlcjtcclxuICAgICAgICB0aGlzLmRhdGFUeXBlID0gZGF0YVR5cGU7XHJcbiAgICAgICAgdGhpcy5kZXNjcmlwdGlvbiA9IGRlc2NyaXB0aW9uO1xyXG4gICAgICAgIHRoaXMuaWQgPSBuYW5vaWRfMS5uYW5vaWQoKTtcclxuICAgIH1cclxufVxyXG5leHBvcnRzLkV4dHJhRmllbGQgPSBFeHRyYUZpZWxkO1xyXG5jbGFzcyBHYW1lUHJvdmlkZXIge1xyXG4gICAgY29uc3RydWN0b3IobmFtZSwgZ2FtZXMgPSBbXSwgZG9tYWluLCB1cmwsIFxyXG4gICAgLy8gKyBGb3IgcHJvbW90aW9uc1xyXG4gICAgY2F0ZWdvcnkgPSBudWxsLCBhY3Rpdml0aWVzID0gW10sIGV4dHJhRmllbGRzID0gW10gLy8gLVxyXG4gICAgKSB7XHJcbiAgICAgICAgdGhpcy5uYW1lID0gbmFtZTtcclxuICAgICAgICB0aGlzLmdhbWVzID0gZ2FtZXM7XHJcbiAgICAgICAgdGhpcy5kb21haW4gPSBkb21haW47XHJcbiAgICAgICAgdGhpcy51cmwgPSB1cmw7XHJcbiAgICAgICAgdGhpcy5jYXRlZ29yeSA9IGNhdGVnb3J5O1xyXG4gICAgICAgIHRoaXMuYWN0aXZpdGllcyA9IGFjdGl2aXRpZXM7XHJcbiAgICAgICAgdGhpcy5leHRyYUZpZWxkcyA9IGV4dHJhRmllbGRzO1xyXG4gICAgICAgIHRoaXMuaWQgPSBuYW5vaWRfMS5uYW5vaWQoKTtcclxuICAgICAgICB0aGlzLmFjdGl2ZSA9IGZhbHNlO1xyXG4gICAgfVxyXG4gICAgYWRkR2FtZShnYW1lKSB7XHJcbiAgICAgICAgdGhpcy5nYW1lcy5wdXNoKGdhbWUpO1xyXG4gICAgfVxyXG59XHJcbmV4cG9ydHMuZGVmYXVsdCA9IEdhbWVQcm92aWRlcjtcclxuIiwiXCJ1c2Ugc3RyaWN0XCI7XHJcbk9iamVjdC5kZWZpbmVQcm9wZXJ0eShleHBvcnRzLCBcIl9fZXNNb2R1bGVcIiwgeyB2YWx1ZTogdHJ1ZSB9KTtcclxuY29uc3QgbmFub2lkXzEgPSByZXF1aXJlKFwibmFub2lkXCIpO1xyXG5jbGFzcyBMYXlvdXRCYW5uZXJJdGVtIHtcclxuICAgIGNvbnN0cnVjdG9yKG5hbWUsIGltYWdlKSB7XHJcbiAgICAgICAgdGhpcy5pZCA9IG5hbm9pZF8xLm5hbm9pZCgpO1xyXG4gICAgICAgIHRoaXMudXJsID0gJyc7XHJcbiAgICAgICAgdGhpcy5pbWFnZSA9ICcnO1xyXG4gICAgICAgIHRoaXMuZGlzcGxheV90ZXh0ID0gJyc7XHJcbiAgICAgICAgdGhpcy5mcm9tID0gJyc7XHJcbiAgICAgICAgdGhpcy50byA9ICcnO1xyXG4gICAgICAgIHRoaXMuZ2FtZUlEID0gJyc7XHJcbiAgICAgICAgdGhpcy50ZXJtc191cmwgPSAnJztcclxuICAgICAgICB0aGlzLnJ1bmNvdW50ID0gMDtcclxuICAgICAgICB0aGlzLm5hbWUgPSBuYW1lO1xyXG4gICAgICAgIHRoaXMuaW1hZ2UgPSBpbWFnZTtcclxuICAgIH1cclxufVxyXG5leHBvcnRzLmRlZmF1bHQgPSBMYXlvdXRCYW5uZXJJdGVtO1xyXG4iLCJcInVzZSBzdHJpY3RcIjtcclxuT2JqZWN0LmRlZmluZVByb3BlcnR5KGV4cG9ydHMsIFwiX19lc01vZHVsZVwiLCB7IHZhbHVlOiB0cnVlIH0pO1xyXG5jb25zdCBuYW5vaWRfMSA9IHJlcXVpcmUoXCJuYW5vaWRcIik7XHJcbmNsYXNzIENhdGVnb3J5IHtcclxuICAgIGNvbnN0cnVjdG9yKG5hbWUsIGRvbWFpbikge1xyXG4gICAgICAgIHRoaXMubmFtZSA9IG5hbWU7XHJcbiAgICAgICAgdGhpcy5kb21haW4gPSBkb21haW47XHJcbiAgICAgICAgdGhpcy5pZCA9IG5hbm9pZF8xLm5hbm9pZCgpO1xyXG4gICAgfVxyXG59XHJcbmV4cG9ydHMuZGVmYXVsdCA9IENhdGVnb3J5O1xyXG4iLCJcInVzZSBzdHJpY3RcIjtcclxuT2JqZWN0LmRlZmluZVByb3BlcnR5KGV4cG9ydHMsIFwiX19lc01vZHVsZVwiLCB7IHZhbHVlOiB0cnVlIH0pO1xyXG5leHBvcnRzLlJld2FyZCA9IGV4cG9ydHMuUmV3YXJkVHlwZSA9IHZvaWQgMDtcclxuY29uc3QgbmFub2lkXzEgPSByZXF1aXJlKFwibmFub2lkXCIpO1xyXG5jbGFzcyBSZXdhcmRUeXBlIHtcclxuICAgIGNvbnN0cnVjdG9yKG5hbWUgPSAnJywgZG9tYWluLCBwcm92aWRlciwgY2F0ZWdvcnkpIHtcclxuICAgICAgICB0aGlzLm5hbWUgPSBuYW1lO1xyXG4gICAgICAgIHRoaXMuZG9tYWluID0gZG9tYWluO1xyXG4gICAgICAgIHRoaXMucHJvdmlkZXIgPSBwcm92aWRlcjtcclxuICAgICAgICB0aGlzLmNhdGVnb3J5ID0gY2F0ZWdvcnk7XHJcbiAgICAgICAgdGhpcy5pZCA9IG5hbm9pZF8xLm5hbm9pZCgpO1xyXG4gICAgICAgIHRoaXMuZmllbGRzID0gW107XHJcbiAgICB9XHJcbiAgICBnZXQgaGFzRmllbGRzKCkge1xyXG4gICAgICAgIHJldHVybiB0aGlzLmZpZWxkcy5sZW5ndGggPiAwO1xyXG4gICAgfVxyXG59XHJcbmV4cG9ydHMuUmV3YXJkVHlwZSA9IFJld2FyZFR5cGU7XHJcbmNsYXNzIFJld2FyZCB7XHJcbiAgICBjb25zdHJ1Y3RvcihuYW1lLCB0eXBlcywgY29kZSwgZGVzY3JpcHRpb24sIGVuYWJsZWQsIGRvbWFpbikge1xyXG4gICAgICAgIHRoaXMubmFtZSA9IG5hbWU7XHJcbiAgICAgICAgdGhpcy50eXBlcyA9IHR5cGVzO1xyXG4gICAgICAgIHRoaXMuY29kZSA9IGNvZGU7XHJcbiAgICAgICAgdGhpcy5kZXNjcmlwdGlvbiA9IGRlc2NyaXB0aW9uO1xyXG4gICAgICAgIHRoaXMuZW5hYmxlZCA9IGVuYWJsZWQ7XHJcbiAgICAgICAgdGhpcy5kb21haW4gPSBkb21haW47XHJcbiAgICAgICAgdGhpcy5pZCA9IG5hbm9pZF8xLm5hbm9pZCgpO1xyXG4gICAgfVxyXG4gICAgZ2V0IGNvbXBsZXRlZCgpIHtcclxuICAgICAgICByZXR1cm4gdGhpcy5uYW1lICYmIHRoaXMuZG9tYWluICYmIHRoaXMudHlwZXMubGVuZ3RoID4gMCAmJiB0aGlzLmNvZGUgJiYgdGhpcy5kZXNjcmlwdGlvbjtcclxuICAgIH1cclxufVxyXG5leHBvcnRzLlJld2FyZCA9IFJld2FyZDtcclxuIl0sInNvdXJjZVJvb3QiOiIifQ==