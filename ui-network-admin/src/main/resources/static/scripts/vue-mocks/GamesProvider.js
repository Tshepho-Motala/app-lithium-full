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
    /******/ 	return __webpack_require__(__webpack_require__.s = "./src/mock/provider/GamesProviderMock.ts");
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


            /***/ })

        /******/ });
//# sourceMappingURL=data:application/json;charset=utf-8;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIndlYnBhY2s6Ly8vd2VicGFjay9ib290c3RyYXAiLCJ3ZWJwYWNrOi8vLy4vbm9kZV9tb2R1bGVzL25hbm9pZC9pbmRleC5icm93c2VyLmpzIiwid2VicGFjazovLy8uL25vZGVfbW9kdWxlcy9uYW5vaWQvdXJsLWFscGhhYmV0L2luZGV4LmpzIiwid2VicGFjazovLy8uL3NyYy9tb2NrL3Byb3ZpZGVyL0RvbWFpblByb3ZpZGVyTW9jay50cyIsIndlYnBhY2s6Ly8vLi9zcmMvbW9jay9wcm92aWRlci9HYW1lc1Byb3ZpZGVyTW9jay50cyIsIndlYnBhY2s6Ly8vLi9zcmMvcGx1Z2luL2Ntcy9tb2RlbHMvRG9tYWluSXRlbS50cyIsIndlYnBhY2s6Ly8vLi9zcmMvcGx1Z2luL2Ntcy9tb2RlbHMvR2FtZVByb3ZpZGVyLnRzIiwid2VicGFjazovLy8uL3NyYy9wbHVnaW4vY21zL21vZGVscy9MYXlvdXRCYW5uZXJJdGVtLnRzIiwid2VicGFjazovLy8uL3NyYy9wbHVnaW4vY29tcG9uZW50cy9DYXRlZ29yeS50cyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiO1FBQUE7UUFDQTs7UUFFQTtRQUNBOztRQUVBO1FBQ0E7UUFDQTtRQUNBO1FBQ0E7UUFDQTtRQUNBO1FBQ0E7UUFDQTtRQUNBOztRQUVBO1FBQ0E7O1FBRUE7UUFDQTs7UUFFQTtRQUNBO1FBQ0E7OztRQUdBO1FBQ0E7O1FBRUE7UUFDQTs7UUFFQTtRQUNBO1FBQ0E7UUFDQSwwQ0FBMEMsZ0NBQWdDO1FBQzFFO1FBQ0E7O1FBRUE7UUFDQTtRQUNBO1FBQ0Esd0RBQXdELGtCQUFrQjtRQUMxRTtRQUNBLGlEQUFpRCxjQUFjO1FBQy9EOztRQUVBO1FBQ0E7UUFDQTtRQUNBO1FBQ0E7UUFDQTtRQUNBO1FBQ0E7UUFDQTtRQUNBO1FBQ0E7UUFDQSx5Q0FBeUMsaUNBQWlDO1FBQzFFLGdIQUFnSCxtQkFBbUIsRUFBRTtRQUNySTtRQUNBOztRQUVBO1FBQ0E7UUFDQTtRQUNBLDJCQUEyQiwwQkFBMEIsRUFBRTtRQUN2RCxpQ0FBaUMsZUFBZTtRQUNoRDtRQUNBO1FBQ0E7O1FBRUE7UUFDQSxzREFBc0QsK0RBQStEOztRQUVySDtRQUNBOzs7UUFHQTtRQUNBOzs7Ozs7Ozs7Ozs7O0FDbEZBO0FBQUE7QUFBQTtBQUFBO0FBQUE7QUFBQTtBQUFBO0FBQUE7QUFBcUQ7QUFDOUM7QUFDQTtBQUNQO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDTztBQUNQO0FBQ087QUFDUDtBQUNBO0FBQ0E7QUFDQTtBQUNBLEtBQUs7QUFDTDtBQUNBLEtBQUs7QUFDTDtBQUNBLEtBQUs7QUFDTDtBQUNBO0FBQ0E7QUFDQSxHQUFHOzs7Ozs7Ozs7Ozs7O0FDaENIO0FBQUE7QUFBTztBQUNQOzs7Ozs7Ozs7Ozs7O0FDRGE7QUFDYjtBQUNBLDRDQUE0QztBQUM1QztBQUNBLDhDQUE4QyxjQUFjO0FBQzVELHFDQUFxQyxtQkFBTyxDQUFDLDZFQUFnQztBQUM3RTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0EsaUJBQWlCLHVFQUF1RTtBQUN4RixpQkFBaUIsOERBQThEO0FBQy9FLGlCQUFpQix5RUFBeUU7QUFDMUYsaUJBQWlCLG9FQUFvRTtBQUNyRixpQkFBaUIsdUVBQXVFO0FBQ3hGLGlCQUFpQixtRUFBbUU7QUFDcEYsaUJBQWlCLHlFQUF5RTtBQUMxRixpQkFBaUIsOERBQThEO0FBQy9FO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0EsYUFBYTtBQUNiLFNBQVM7QUFDVDtBQUNBO0FBQ0E7Ozs7Ozs7Ozs7Ozs7QUM5QmE7QUFDYjtBQUNBO0FBQ0Esa0NBQWtDLG9DQUFvQyxhQUFhLEVBQUUsRUFBRTtBQUN2RixDQUFDO0FBQ0Q7QUFDQTtBQUNBLENBQUM7QUFDRDtBQUNBLHlDQUF5Qyw2QkFBNkI7QUFDdEUsQ0FBQztBQUNEO0FBQ0EsQ0FBQztBQUNEO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQSwyQkFBMkIsK0RBQStELGdCQUFnQixFQUFFLEVBQUU7QUFDOUc7QUFDQSxtQ0FBbUMsTUFBTSw2QkFBNkIsRUFBRSxZQUFZLFdBQVcsRUFBRTtBQUNqRyxrQ0FBa0MsTUFBTSxpQ0FBaUMsRUFBRSxZQUFZLFdBQVcsRUFBRTtBQUNwRywrQkFBK0IscUZBQXFGO0FBQ3BIO0FBQ0EsS0FBSztBQUNMO0FBQ0E7QUFDQSw0Q0FBNEM7QUFDNUM7QUFDQSw4Q0FBOEMsY0FBYztBQUM1RCxvQ0FBb0MsbUJBQU8sQ0FBQyxpRkFBa0M7QUFDOUUsMkNBQTJDLG1CQUFPLENBQUMseUZBQXNDO0FBQ3pGLG1DQUFtQyxtQkFBTyxDQUFDLHlFQUE4QjtBQUN6RSw2Q0FBNkMsbUJBQU8sQ0FBQyx1RUFBc0I7QUFDM0U7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBLFNBQVM7QUFDVDtBQUNBO0FBQ0E7QUFDQTtBQUNBLDBDQUEwQywyQ0FBMkMsMENBQTBDLDhhQUE4YSw2QkFBNkIsMENBQTBDLHVDQUF1QyxhQUFhLDZCQUE2QiwwQ0FBMEMscUNBQXFDLFdBQVcsTUFBTSxpR0FBaUcsUUFBUSwrRkFBK0Ysd0xBQXdMLEVBQUUsMENBQTBDLDBDQUEwQyxtWUFBbVksNENBQTRDLGlCQUFpQiw4QkFBOEIsMENBQTBDLGtDQUFrQyw0QkFBNEIsc0hBQXNILEVBQUUsa0NBQWtDLDBDQUEwQyxpWUFBaVksNENBQTRDLGlCQUFpQiw4QkFBOEIsMENBQTBDLGtDQUFrQyw0QkFBNEIsc0hBQXNILEVBQUUsc0NBQXNDLDBDQUEwQyx1WkFBdVosNkJBQTZCLDBDQUEwQyxrQ0FBa0MsNEJBQTRCLHNIQUFzSCxFQUFFLGtDQUFrQywwQ0FBMEMsd1hBQXdYLDRDQUE0QyxpQkFBaUIsOEJBQThCLDBDQUEwQyxrQ0FBa0MsMkJBQTJCLFFBQVEsMEZBQTBGLHNIQUFzSDtBQUNud0k7QUFDQTtBQUNBLGFBQWE7QUFDYixTQUFTO0FBQ1Q7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0EsYUFBYTtBQUNiLFNBQVM7QUFDVDtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0EsaUVBQWlFLFFBQVEsc0hBQXNILEVBQUU7QUFDak07QUFDQTtBQUNBO0FBQ0E7QUFDQTs7Ozs7Ozs7Ozs7OztBQzlGYTtBQUNiLDhDQUE4QyxjQUFjO0FBQzVELGlCQUFpQixtQkFBTyxDQUFDLHNEQUFRO0FBQ2pDO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTs7Ozs7Ozs7Ozs7OztBQ1hhO0FBQ2IsOENBQThDLGNBQWM7QUFDNUQ7QUFDQSxpQkFBaUIsbUJBQU8sQ0FBQyxzREFBUTtBQUNqQztBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBOzs7Ozs7Ozs7Ozs7O0FDekNhO0FBQ2IsOENBQThDLGNBQWM7QUFDNUQsaUJBQWlCLG1CQUFPLENBQUMsc0RBQVE7QUFDakM7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7Ozs7Ozs7Ozs7Ozs7QUNsQmE7QUFDYiw4Q0FBOEMsY0FBYztBQUM1RCxpQkFBaUIsbUJBQU8sQ0FBQyxzREFBUTtBQUNqQztBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBIiwiZmlsZSI6IkdhbWVzUHJvdmlkZXJNb2NrLmpzIiwic291cmNlc0NvbnRlbnQiOlsiIFx0Ly8gVGhlIG1vZHVsZSBjYWNoZVxuIFx0dmFyIGluc3RhbGxlZE1vZHVsZXMgPSB7fTtcblxuIFx0Ly8gVGhlIHJlcXVpcmUgZnVuY3Rpb25cbiBcdGZ1bmN0aW9uIF9fd2VicGFja19yZXF1aXJlX18obW9kdWxlSWQpIHtcblxuIFx0XHQvLyBDaGVjayBpZiBtb2R1bGUgaXMgaW4gY2FjaGVcbiBcdFx0aWYoaW5zdGFsbGVkTW9kdWxlc1ttb2R1bGVJZF0pIHtcbiBcdFx0XHRyZXR1cm4gaW5zdGFsbGVkTW9kdWxlc1ttb2R1bGVJZF0uZXhwb3J0cztcbiBcdFx0fVxuIFx0XHQvLyBDcmVhdGUgYSBuZXcgbW9kdWxlIChhbmQgcHV0IGl0IGludG8gdGhlIGNhY2hlKVxuIFx0XHR2YXIgbW9kdWxlID0gaW5zdGFsbGVkTW9kdWxlc1ttb2R1bGVJZF0gPSB7XG4gXHRcdFx0aTogbW9kdWxlSWQsXG4gXHRcdFx0bDogZmFsc2UsXG4gXHRcdFx0ZXhwb3J0czoge31cbiBcdFx0fTtcblxuIFx0XHQvLyBFeGVjdXRlIHRoZSBtb2R1bGUgZnVuY3Rpb25cbiBcdFx0bW9kdWxlc1ttb2R1bGVJZF0uY2FsbChtb2R1bGUuZXhwb3J0cywgbW9kdWxlLCBtb2R1bGUuZXhwb3J0cywgX193ZWJwYWNrX3JlcXVpcmVfXyk7XG5cbiBcdFx0Ly8gRmxhZyB0aGUgbW9kdWxlIGFzIGxvYWRlZFxuIFx0XHRtb2R1bGUubCA9IHRydWU7XG5cbiBcdFx0Ly8gUmV0dXJuIHRoZSBleHBvcnRzIG9mIHRoZSBtb2R1bGVcbiBcdFx0cmV0dXJuIG1vZHVsZS5leHBvcnRzO1xuIFx0fVxuXG5cbiBcdC8vIGV4cG9zZSB0aGUgbW9kdWxlcyBvYmplY3QgKF9fd2VicGFja19tb2R1bGVzX18pXG4gXHRfX3dlYnBhY2tfcmVxdWlyZV9fLm0gPSBtb2R1bGVzO1xuXG4gXHQvLyBleHBvc2UgdGhlIG1vZHVsZSBjYWNoZVxuIFx0X193ZWJwYWNrX3JlcXVpcmVfXy5jID0gaW5zdGFsbGVkTW9kdWxlcztcblxuIFx0Ly8gZGVmaW5lIGdldHRlciBmdW5jdGlvbiBmb3IgaGFybW9ueSBleHBvcnRzXG4gXHRfX3dlYnBhY2tfcmVxdWlyZV9fLmQgPSBmdW5jdGlvbihleHBvcnRzLCBuYW1lLCBnZXR0ZXIpIHtcbiBcdFx0aWYoIV9fd2VicGFja19yZXF1aXJlX18ubyhleHBvcnRzLCBuYW1lKSkge1xuIFx0XHRcdE9iamVjdC5kZWZpbmVQcm9wZXJ0eShleHBvcnRzLCBuYW1lLCB7IGVudW1lcmFibGU6IHRydWUsIGdldDogZ2V0dGVyIH0pO1xuIFx0XHR9XG4gXHR9O1xuXG4gXHQvLyBkZWZpbmUgX19lc01vZHVsZSBvbiBleHBvcnRzXG4gXHRfX3dlYnBhY2tfcmVxdWlyZV9fLnIgPSBmdW5jdGlvbihleHBvcnRzKSB7XG4gXHRcdGlmKHR5cGVvZiBTeW1ib2wgIT09ICd1bmRlZmluZWQnICYmIFN5bWJvbC50b1N0cmluZ1RhZykge1xuIFx0XHRcdE9iamVjdC5kZWZpbmVQcm9wZXJ0eShleHBvcnRzLCBTeW1ib2wudG9TdHJpbmdUYWcsIHsgdmFsdWU6ICdNb2R1bGUnIH0pO1xuIFx0XHR9XG4gXHRcdE9iamVjdC5kZWZpbmVQcm9wZXJ0eShleHBvcnRzLCAnX19lc01vZHVsZScsIHsgdmFsdWU6IHRydWUgfSk7XG4gXHR9O1xuXG4gXHQvLyBjcmVhdGUgYSBmYWtlIG5hbWVzcGFjZSBvYmplY3RcbiBcdC8vIG1vZGUgJiAxOiB2YWx1ZSBpcyBhIG1vZHVsZSBpZCwgcmVxdWlyZSBpdFxuIFx0Ly8gbW9kZSAmIDI6IG1lcmdlIGFsbCBwcm9wZXJ0aWVzIG9mIHZhbHVlIGludG8gdGhlIG5zXG4gXHQvLyBtb2RlICYgNDogcmV0dXJuIHZhbHVlIHdoZW4gYWxyZWFkeSBucyBvYmplY3RcbiBcdC8vIG1vZGUgJiA4fDE6IGJlaGF2ZSBsaWtlIHJlcXVpcmVcbiBcdF9fd2VicGFja19yZXF1aXJlX18udCA9IGZ1bmN0aW9uKHZhbHVlLCBtb2RlKSB7XG4gXHRcdGlmKG1vZGUgJiAxKSB2YWx1ZSA9IF9fd2VicGFja19yZXF1aXJlX18odmFsdWUpO1xuIFx0XHRpZihtb2RlICYgOCkgcmV0dXJuIHZhbHVlO1xuIFx0XHRpZigobW9kZSAmIDQpICYmIHR5cGVvZiB2YWx1ZSA9PT0gJ29iamVjdCcgJiYgdmFsdWUgJiYgdmFsdWUuX19lc01vZHVsZSkgcmV0dXJuIHZhbHVlO1xuIFx0XHR2YXIgbnMgPSBPYmplY3QuY3JlYXRlKG51bGwpO1xuIFx0XHRfX3dlYnBhY2tfcmVxdWlyZV9fLnIobnMpO1xuIFx0XHRPYmplY3QuZGVmaW5lUHJvcGVydHkobnMsICdkZWZhdWx0JywgeyBlbnVtZXJhYmxlOiB0cnVlLCB2YWx1ZTogdmFsdWUgfSk7XG4gXHRcdGlmKG1vZGUgJiAyICYmIHR5cGVvZiB2YWx1ZSAhPSAnc3RyaW5nJykgZm9yKHZhciBrZXkgaW4gdmFsdWUpIF9fd2VicGFja19yZXF1aXJlX18uZChucywga2V5LCBmdW5jdGlvbihrZXkpIHsgcmV0dXJuIHZhbHVlW2tleV07IH0uYmluZChudWxsLCBrZXkpKTtcbiBcdFx0cmV0dXJuIG5zO1xuIFx0fTtcblxuIFx0Ly8gZ2V0RGVmYXVsdEV4cG9ydCBmdW5jdGlvbiBmb3IgY29tcGF0aWJpbGl0eSB3aXRoIG5vbi1oYXJtb255IG1vZHVsZXNcbiBcdF9fd2VicGFja19yZXF1aXJlX18ubiA9IGZ1bmN0aW9uKG1vZHVsZSkge1xuIFx0XHR2YXIgZ2V0dGVyID0gbW9kdWxlICYmIG1vZHVsZS5fX2VzTW9kdWxlID9cbiBcdFx0XHRmdW5jdGlvbiBnZXREZWZhdWx0KCkgeyByZXR1cm4gbW9kdWxlWydkZWZhdWx0J107IH0gOlxuIFx0XHRcdGZ1bmN0aW9uIGdldE1vZHVsZUV4cG9ydHMoKSB7IHJldHVybiBtb2R1bGU7IH07XG4gXHRcdF9fd2VicGFja19yZXF1aXJlX18uZChnZXR0ZXIsICdhJywgZ2V0dGVyKTtcbiBcdFx0cmV0dXJuIGdldHRlcjtcbiBcdH07XG5cbiBcdC8vIE9iamVjdC5wcm90b3R5cGUuaGFzT3duUHJvcGVydHkuY2FsbFxuIFx0X193ZWJwYWNrX3JlcXVpcmVfXy5vID0gZnVuY3Rpb24ob2JqZWN0LCBwcm9wZXJ0eSkgeyByZXR1cm4gT2JqZWN0LnByb3RvdHlwZS5oYXNPd25Qcm9wZXJ0eS5jYWxsKG9iamVjdCwgcHJvcGVydHkpOyB9O1xuXG4gXHQvLyBfX3dlYnBhY2tfcHVibGljX3BhdGhfX1xuIFx0X193ZWJwYWNrX3JlcXVpcmVfXy5wID0gXCJcIjtcblxuXG4gXHQvLyBMb2FkIGVudHJ5IG1vZHVsZSBhbmQgcmV0dXJuIGV4cG9ydHNcbiBcdHJldHVybiBfX3dlYnBhY2tfcmVxdWlyZV9fKF9fd2VicGFja19yZXF1aXJlX18ucyA9IFwiLi9zcmMvbW9jay9wcm92aWRlci9HYW1lc1Byb3ZpZGVyTW9jay50c1wiKTtcbiIsImV4cG9ydCB7IHVybEFscGhhYmV0IH0gZnJvbSAnLi91cmwtYWxwaGFiZXQvaW5kZXguanMnXG5leHBvcnQgbGV0IHJhbmRvbSA9IGJ5dGVzID0+IGNyeXB0by5nZXRSYW5kb21WYWx1ZXMobmV3IFVpbnQ4QXJyYXkoYnl0ZXMpKVxuZXhwb3J0IGxldCBjdXN0b21SYW5kb20gPSAoYWxwaGFiZXQsIGRlZmF1bHRTaXplLCBnZXRSYW5kb20pID0+IHtcbiAgbGV0IG1hc2sgPSAoMiA8PCAoTWF0aC5sb2coYWxwaGFiZXQubGVuZ3RoIC0gMSkgLyBNYXRoLkxOMikpIC0gMVxuICBsZXQgc3RlcCA9IC1+KCgxLjYgKiBtYXNrICogZGVmYXVsdFNpemUpIC8gYWxwaGFiZXQubGVuZ3RoKVxuICByZXR1cm4gKHNpemUgPSBkZWZhdWx0U2l6ZSkgPT4ge1xuICAgIGxldCBpZCA9ICcnXG4gICAgd2hpbGUgKHRydWUpIHtcbiAgICAgIGxldCBieXRlcyA9IGdldFJhbmRvbShzdGVwKVxuICAgICAgbGV0IGogPSBzdGVwXG4gICAgICB3aGlsZSAoai0tKSB7XG4gICAgICAgIGlkICs9IGFscGhhYmV0W2J5dGVzW2pdICYgbWFza10gfHwgJydcbiAgICAgICAgaWYgKGlkLmxlbmd0aCA9PT0gc2l6ZSkgcmV0dXJuIGlkXG4gICAgICB9XG4gICAgfVxuICB9XG59XG5leHBvcnQgbGV0IGN1c3RvbUFscGhhYmV0ID0gKGFscGhhYmV0LCBzaXplID0gMjEpID0+XG4gIGN1c3RvbVJhbmRvbShhbHBoYWJldCwgc2l6ZSwgcmFuZG9tKVxuZXhwb3J0IGxldCBuYW5vaWQgPSAoc2l6ZSA9IDIxKSA9PlxuICBjcnlwdG8uZ2V0UmFuZG9tVmFsdWVzKG5ldyBVaW50OEFycmF5KHNpemUpKS5yZWR1Y2UoKGlkLCBieXRlKSA9PiB7XG4gICAgYnl0ZSAmPSA2M1xuICAgIGlmIChieXRlIDwgMzYpIHtcbiAgICAgIGlkICs9IGJ5dGUudG9TdHJpbmcoMzYpXG4gICAgfSBlbHNlIGlmIChieXRlIDwgNjIpIHtcbiAgICAgIGlkICs9IChieXRlIC0gMjYpLnRvU3RyaW5nKDM2KS50b1VwcGVyQ2FzZSgpXG4gICAgfSBlbHNlIGlmIChieXRlID4gNjIpIHtcbiAgICAgIGlkICs9ICctJ1xuICAgIH0gZWxzZSB7XG4gICAgICBpZCArPSAnXydcbiAgICB9XG4gICAgcmV0dXJuIGlkXG4gIH0sICcnKVxuIiwiZXhwb3J0IGNvbnN0IHVybEFscGhhYmV0ID1cbiAgJ3VzZWFuZG9tLTI2VDE5ODM0MFBYNzVweEpBQ0tWRVJZTUlOREJVU0hXT0xGX0dRWmJmZ2hqa2xxdnd5enJpY3QnXG4iLCJcInVzZSBzdHJpY3RcIjtcclxudmFyIF9faW1wb3J0RGVmYXVsdCA9ICh0aGlzICYmIHRoaXMuX19pbXBvcnREZWZhdWx0KSB8fCBmdW5jdGlvbiAobW9kKSB7XHJcbiAgICByZXR1cm4gKG1vZCAmJiBtb2QuX19lc01vZHVsZSkgPyBtb2QgOiB7IFwiZGVmYXVsdFwiOiBtb2QgfTtcclxufTtcclxuT2JqZWN0LmRlZmluZVByb3BlcnR5KGV4cG9ydHMsIFwiX19lc01vZHVsZVwiLCB7IHZhbHVlOiB0cnVlIH0pO1xyXG5jb25zdCBEb21haW5JdGVtXzEgPSBfX2ltcG9ydERlZmF1bHQocmVxdWlyZShcIkAvcGx1Z2luL2Ntcy9tb2RlbHMvRG9tYWluSXRlbVwiKSk7XHJcbmNsYXNzIERvbWFpblByb3ZpZGVyTW9jayB7XHJcbiAgICBnZXREb21haW5zKCkge1xyXG4gICAgICAgIC8vIERhdGEgdGFrZW4gZnJvbSAkdXNlclNlcnZpY2UuZG9tYWluc1dpdGhBbnlSb2xlKFsnQURNSU4nXSlcclxuICAgICAgICByZXR1cm4gbmV3IFByb21pc2UoKHJlcykgPT4ge1xyXG4gICAgICAgICAgICBjb25zdCBkb21haW5zID0gW1xyXG4gICAgICAgICAgICAgICAgeyBuYW1lOiAnbGl2ZXNjb3JlJywgcGQ6IGZhbHNlLCBkaXNwbGF5TmFtZTogJ0xpdmVzY29yZSBEZXZlbG9wIEFkbWluJyB9LFxyXG4gICAgICAgICAgICAgICAgeyBuYW1lOiAnbGl2ZXNjb3JlX3VrJywgcGQ6IHRydWUsIGRpc3BsYXlOYW1lOiAnTGl2ZXNjb3JlIFVLJyB9LFxyXG4gICAgICAgICAgICAgICAgeyBuYW1lOiAnbGl2ZXNjb3JlX25pZ2VyaWEnLCBwZDogdHJ1ZSwgZGlzcGxheU5hbWU6ICdMaXZlc2NvcmUgIE5pZ2VyaWEnIH0sXHJcbiAgICAgICAgICAgICAgICB7IG5hbWU6ICdsaXZlc2NvcmVfbWVkaWEnLCBwZDogdHJ1ZSwgZGlzcGxheU5hbWU6ICdMaXZlc2NvcmUgTWVkaWEnIH0sXHJcbiAgICAgICAgICAgICAgICB7IG5hbWU6ICdsaXZlc2NvcmVfbmwnLCBwZDogdHJ1ZSwgZGlzcGxheU5hbWU6ICdMaXZlc2NvcmUgTmV0aGVybGFuZHMnIH0sXHJcbiAgICAgICAgICAgICAgICB7IG5hbWU6ICdsaXZlc2NvcmVfaWUnLCBwZDogdHJ1ZSwgZGlzcGxheU5hbWU6ICdMaXZlc2NvcmUgSXJlbGFuZCcgfSxcclxuICAgICAgICAgICAgICAgIHsgbmFtZTogJ2xpdmVzY29yZV96YScsIHBkOiBmYWxzZSwgZGlzcGxheU5hbWU6ICdMaXZlc2NvcmUgU291dGggQWZyaWNhJyB9LFxyXG4gICAgICAgICAgICAgICAgeyBuYW1lOiAndmlyZ2luYmV0X3VrJywgcGQ6IHRydWUsIGRpc3BsYXlOYW1lOiAnVmlyZ2luYmV0IFVLJyB9LFxyXG4gICAgICAgICAgICBdO1xyXG4gICAgICAgICAgICBjb25zdCBkb21haW5JdGVtcyA9IFtdO1xyXG4gICAgICAgICAgICBmb3IgKGNvbnN0IGl0ZW0gb2YgZG9tYWlucykge1xyXG4gICAgICAgICAgICAgICAgZG9tYWluSXRlbXMucHVzaChuZXcgRG9tYWluSXRlbV8xLmRlZmF1bHQoaXRlbS5kaXNwbGF5TmFtZSwgaXRlbS5uYW1lLCBpdGVtLnBkKSk7XHJcbiAgICAgICAgICAgIH1cclxuICAgICAgICAgICAgc2V0VGltZW91dCgoKSA9PiB7XHJcbiAgICAgICAgICAgICAgICByZXMoZG9tYWluSXRlbXMpO1xyXG4gICAgICAgICAgICB9LCAxMDAwKTtcclxuICAgICAgICB9KTtcclxuICAgIH1cclxufVxyXG5leHBvcnRzLmRlZmF1bHQgPSBEb21haW5Qcm92aWRlck1vY2s7XHJcbiIsIlwidXNlIHN0cmljdFwiO1xyXG52YXIgX19jcmVhdGVCaW5kaW5nID0gKHRoaXMgJiYgdGhpcy5fX2NyZWF0ZUJpbmRpbmcpIHx8IChPYmplY3QuY3JlYXRlID8gKGZ1bmN0aW9uKG8sIG0sIGssIGsyKSB7XHJcbiAgICBpZiAoazIgPT09IHVuZGVmaW5lZCkgazIgPSBrO1xyXG4gICAgT2JqZWN0LmRlZmluZVByb3BlcnR5KG8sIGsyLCB7IGVudW1lcmFibGU6IHRydWUsIGdldDogZnVuY3Rpb24oKSB7IHJldHVybiBtW2tdOyB9IH0pO1xyXG59KSA6IChmdW5jdGlvbihvLCBtLCBrLCBrMikge1xyXG4gICAgaWYgKGsyID09PSB1bmRlZmluZWQpIGsyID0gaztcclxuICAgIG9bazJdID0gbVtrXTtcclxufSkpO1xyXG52YXIgX19zZXRNb2R1bGVEZWZhdWx0ID0gKHRoaXMgJiYgdGhpcy5fX3NldE1vZHVsZURlZmF1bHQpIHx8IChPYmplY3QuY3JlYXRlID8gKGZ1bmN0aW9uKG8sIHYpIHtcclxuICAgIE9iamVjdC5kZWZpbmVQcm9wZXJ0eShvLCBcImRlZmF1bHRcIiwgeyBlbnVtZXJhYmxlOiB0cnVlLCB2YWx1ZTogdiB9KTtcclxufSkgOiBmdW5jdGlvbihvLCB2KSB7XHJcbiAgICBvW1wiZGVmYXVsdFwiXSA9IHY7XHJcbn0pO1xyXG52YXIgX19pbXBvcnRTdGFyID0gKHRoaXMgJiYgdGhpcy5fX2ltcG9ydFN0YXIpIHx8IGZ1bmN0aW9uIChtb2QpIHtcclxuICAgIGlmIChtb2QgJiYgbW9kLl9fZXNNb2R1bGUpIHJldHVybiBtb2Q7XHJcbiAgICB2YXIgcmVzdWx0ID0ge307XHJcbiAgICBpZiAobW9kICE9IG51bGwpIGZvciAodmFyIGsgaW4gbW9kKSBpZiAoayAhPT0gXCJkZWZhdWx0XCIgJiYgT2JqZWN0LnByb3RvdHlwZS5oYXNPd25Qcm9wZXJ0eS5jYWxsKG1vZCwgaykpIF9fY3JlYXRlQmluZGluZyhyZXN1bHQsIG1vZCwgayk7XHJcbiAgICBfX3NldE1vZHVsZURlZmF1bHQocmVzdWx0LCBtb2QpO1xyXG4gICAgcmV0dXJuIHJlc3VsdDtcclxufTtcclxudmFyIF9fYXdhaXRlciA9ICh0aGlzICYmIHRoaXMuX19hd2FpdGVyKSB8fCBmdW5jdGlvbiAodGhpc0FyZywgX2FyZ3VtZW50cywgUCwgZ2VuZXJhdG9yKSB7XHJcbiAgICBmdW5jdGlvbiBhZG9wdCh2YWx1ZSkgeyByZXR1cm4gdmFsdWUgaW5zdGFuY2VvZiBQID8gdmFsdWUgOiBuZXcgUChmdW5jdGlvbiAocmVzb2x2ZSkgeyByZXNvbHZlKHZhbHVlKTsgfSk7IH1cclxuICAgIHJldHVybiBuZXcgKFAgfHwgKFAgPSBQcm9taXNlKSkoZnVuY3Rpb24gKHJlc29sdmUsIHJlamVjdCkge1xyXG4gICAgICAgIGZ1bmN0aW9uIGZ1bGZpbGxlZCh2YWx1ZSkgeyB0cnkgeyBzdGVwKGdlbmVyYXRvci5uZXh0KHZhbHVlKSk7IH0gY2F0Y2ggKGUpIHsgcmVqZWN0KGUpOyB9IH1cclxuICAgICAgICBmdW5jdGlvbiByZWplY3RlZCh2YWx1ZSkgeyB0cnkgeyBzdGVwKGdlbmVyYXRvcltcInRocm93XCJdKHZhbHVlKSk7IH0gY2F0Y2ggKGUpIHsgcmVqZWN0KGUpOyB9IH1cclxuICAgICAgICBmdW5jdGlvbiBzdGVwKHJlc3VsdCkgeyByZXN1bHQuZG9uZSA/IHJlc29sdmUocmVzdWx0LnZhbHVlKSA6IGFkb3B0KHJlc3VsdC52YWx1ZSkudGhlbihmdWxmaWxsZWQsIHJlamVjdGVkKTsgfVxyXG4gICAgICAgIHN0ZXAoKGdlbmVyYXRvciA9IGdlbmVyYXRvci5hcHBseSh0aGlzQXJnLCBfYXJndW1lbnRzIHx8IFtdKSkubmV4dCgpKTtcclxuICAgIH0pO1xyXG59O1xyXG52YXIgX19pbXBvcnREZWZhdWx0ID0gKHRoaXMgJiYgdGhpcy5fX2ltcG9ydERlZmF1bHQpIHx8IGZ1bmN0aW9uIChtb2QpIHtcclxuICAgIHJldHVybiAobW9kICYmIG1vZC5fX2VzTW9kdWxlKSA/IG1vZCA6IHsgXCJkZWZhdWx0XCI6IG1vZCB9O1xyXG59O1xyXG5PYmplY3QuZGVmaW5lUHJvcGVydHkoZXhwb3J0cywgXCJfX2VzTW9kdWxlXCIsIHsgdmFsdWU6IHRydWUgfSk7XHJcbmNvbnN0IEdhbWVQcm92aWRlcl8xID0gX19pbXBvcnRTdGFyKHJlcXVpcmUoXCJAL3BsdWdpbi9jbXMvbW9kZWxzL0dhbWVQcm92aWRlclwiKSk7XHJcbmNvbnN0IExheW91dEJhbm5lckl0ZW1fMSA9IF9faW1wb3J0RGVmYXVsdChyZXF1aXJlKFwiQC9wbHVnaW4vY21zL21vZGVscy9MYXlvdXRCYW5uZXJJdGVtXCIpKTtcclxuY29uc3QgQ2F0ZWdvcnlfMSA9IF9faW1wb3J0RGVmYXVsdChyZXF1aXJlKFwiQC9wbHVnaW4vY29tcG9uZW50cy9DYXRlZ29yeVwiKSk7XHJcbmNvbnN0IERvbWFpblByb3ZpZGVyTW9ja18xID0gX19pbXBvcnREZWZhdWx0KHJlcXVpcmUoXCIuL0RvbWFpblByb3ZpZGVyTW9ja1wiKSk7XHJcbmNsYXNzIEdhbWVzUHJvdmlkZXJNb2NrIHtcclxuICAgIGNvbnN0cnVjdG9yKCkge1xyXG4gICAgICAgIHRoaXMuZ2FtZVByb3ZpZGVycyA9IFtdO1xyXG4gICAgICAgIHRoaXMuYXBwbHlNb2NrRGF0YSgpO1xyXG4gICAgfVxyXG4gICAgYXBwbHlNb2NrRGF0YSgpIHtcclxuICAgICAgICByZXR1cm4gX19hd2FpdGVyKHRoaXMsIHZvaWQgMCwgdm9pZCAwLCBmdW5jdGlvbiogKCkge1xyXG4gICAgICAgICAgICBjb25zdCBkcCA9IG5ldyBEb21haW5Qcm92aWRlck1vY2tfMS5kZWZhdWx0KCk7XHJcbiAgICAgICAgICAgIGNvbnN0IGRvbWFpbnMgPSB5aWVsZCBkcC5nZXREb21haW5zKCk7XHJcbiAgICAgICAgICAgIGNvbnN0IGRvbWFpblVrID0gZG9tYWluc1swXTtcclxuICAgICAgICAgICAgY29uc3QgdXNlckNhdGVnb3J5ID0gbmV3IENhdGVnb3J5XzEuZGVmYXVsdCgndXNlcicsIGRvbWFpblVrKTtcclxuICAgICAgICAgICAgY29uc3QgY2FzaW5vQ2F0ZWdvcnkgPSBuZXcgQ2F0ZWdvcnlfMS5kZWZhdWx0KCdjYXNpbm8nLCBkb21haW5Vayk7XHJcbiAgICAgICAgICAgIHRoaXMuZ2FtZVByb3ZpZGVycyA9IFtcclxuICAgICAgICAgICAgICAgIG5ldyBHYW1lUHJvdmlkZXJfMS5kZWZhdWx0KCdSb3hvcicsIFtcclxuICAgICAgICAgICAgICAgICAgICBuZXcgTGF5b3V0QmFubmVySXRlbV8xLmRlZmF1bHQoJzEwcCBSb3VsZXR0ZScsICdodHRwczovL2xiby5saXRoaXVtLWRldmVsb3AubHMtZy5uZXQvaW1hZ2VzL2xvZ29fd2lkZS5wbmcnKSxcclxuICAgICAgICAgICAgICAgICAgICBuZXcgTGF5b3V0QmFubmVySXRlbV8xLmRlZmF1bHQoJzIwcCBSb3VsZXR0ZScsICdodHRwczovL2xiby5saXRoaXVtLWRldmVsb3AubHMtZy5uZXQvaW1hZ2VzL2xvZ29fd2lkZS5wbmcnKSxcclxuICAgICAgICAgICAgICAgICAgICBuZXcgTGF5b3V0QmFubmVySXRlbV8xLmRlZmF1bHQoJ0FjdGlvbiBCYW5rJywgJ2h0dHBzOi8vbGJvLmxpdGhpdW0tZGV2ZWxvcC5scy1nLm5ldC9pbWFnZXMvbG9nb193aWRlLnBuZycpLFxyXG4gICAgICAgICAgICAgICAgICAgIG5ldyBMYXlvdXRCYW5uZXJJdGVtXzEuZGVmYXVsdCgnQXJvdW5kIFRoZSBSZWVscycsICdodHRwczovL2xiby5saXRoaXVtLWRldmVsb3AubHMtZy5uZXQvaW1hZ2VzL2xvZ29fd2lkZS5wbmcnKVxyXG4gICAgICAgICAgICAgICAgXSwgJ2xpdmVzY29yZV91aycsICdzdmMtcmV3YXJkLXByLWNhc2luby1yb3hvcicsIGNhc2lub0NhdGVnb3J5LCBbbmV3IEdhbWVQcm92aWRlcl8xLkFjdGl2aXR5KCd3YWdlcicsIDIpLCBuZXcgR2FtZVByb3ZpZGVyXzEuQWN0aXZpdHkoJ3dpbicsIDIpXSwgW25ldyBHYW1lUHJvdmlkZXJfMS5FeHRyYUZpZWxkKCdnYW1lJywgMiksIG5ldyBHYW1lUHJvdmlkZXJfMS5FeHRyYUZpZWxkKCdnYW1lX3R5cGUnLCAyKV0pLFxyXG4gICAgICAgICAgICAgICAgbmV3IEdhbWVQcm92aWRlcl8xLmRlZmF1bHQoJ01pY3JvZ2FtaW5nJywgW1xyXG4gICAgICAgICAgICAgICAgICAgIG5ldyBMYXlvdXRCYW5uZXJJdGVtXzEuZGVmYXVsdCgnTWljcm9nYW1pbmcgQm9vayBPZiBPeicsICdodHRwczovL2xiby5saXRoaXVtLWRldmVsb3AubHMtZy5uZXQvaW1hZ2VzL2xvZ29fd2lkZS5wbmcnKSxcclxuICAgICAgICAgICAgICAgICAgICBuZXcgTGF5b3V0QmFubmVySXRlbV8xLmRlZmF1bHQoJ09uIFRoZSBIb3VzZSBSb3VsZXR0ZScsICdodHRwczovL2xiby5saXRoaXVtLWRldmVsb3AubHMtZy5uZXQvaW1hZ2VzL2xvZ29fd2lkZS5wbmcnKSxcclxuICAgICAgICAgICAgICAgICAgICBuZXcgTGF5b3V0QmFubmVySXRlbV8xLmRlZmF1bHQoJ1Bob2VuaXggSmFja3BvdCcsICdodHRwczovL2xiby5saXRoaXVtLWRldmVsb3AubHMtZy5uZXQvaW1hZ2VzL2xvZ29fd2lkZS5wbmcnKSxcclxuICAgICAgICAgICAgICAgICAgICBuZXcgTGF5b3V0QmFubmVySXRlbV8xLmRlZmF1bHQoJ0xpZ2h0bmluZyBSb3VsZXR0ZScsICdodHRwczovL2xiby5saXRoaXVtLWRldmVsb3AubHMtZy5uZXQvaW1hZ2VzL2xvZ29fd2lkZS5wbmcnKVxyXG4gICAgICAgICAgICAgICAgXSwgJ2xpdmVzY29yZV91aycsICdzdmMtcmV3YXJkLXByLWNhc2luby1taWNyb2dhbWluZycpLFxyXG4gICAgICAgICAgICAgICAgbmV3IEdhbWVQcm92aWRlcl8xLmRlZmF1bHQoJ1VzZXInLCBbXSwgJ2xpdmVzY29yZV91aycsICdzdmMtcHJvbW8tcHJvdmlkZXItdXNlcicsIHVzZXJDYXRlZ29yeSwgW25ldyBHYW1lUHJvdmlkZXJfMS5BY3Rpdml0eSgnbG9naW4tc3VjY2VzcycsIDEpXSwgW25ldyBHYW1lUHJvdmlkZXJfMS5FeHRyYUZpZWxkKCdkYXlzT2ZXZWVrJywgMSksIG5ldyBHYW1lUHJvdmlkZXJfMS5FeHRyYUZpZWxkKCdncmFudWxhcml0eScsIDEpLCBuZXcgR2FtZVByb3ZpZGVyXzEuRXh0cmFGaWVsZCgnY29uc2VjdXRpdmVMb2dpbnMnLCAxKV0pLFxyXG4gICAgICAgICAgICAgICAgbmV3IEdhbWVQcm92aWRlcl8xLmRlZmF1bHQoJ1Nwb3J0c2Jvb2snLCBbXSwgJ2xpdmVzY29yZV91aycsICdzdmMtcmV3YXJkLXByLXNwb3J0c2Jvb2stc2J0JyksXHJcbiAgICAgICAgICAgICAgICBuZXcgR2FtZVByb3ZpZGVyXzEuZGVmYXVsdCgnaUZvcml1bScsIFtdLCAnbGl2ZXNjb3JlX3VrJywgJ3N2Yy1yZXdhcmQtcHItY2FzaW5vLWlmb3JpdW0nKVxyXG4gICAgICAgICAgICBdO1xyXG4gICAgICAgIH0pO1xyXG4gICAgfVxyXG4gICAgZ2V0R2FtZXNCeURvbWFpbkFuZEVuYWJsZWQoZG9tYWluTmFtZSwgZW5hYmxlZCwgdmlzaWJsZSwgY2hhbm5lbCkge1xyXG4gICAgICAgIC8vIFNoYW1lbGVzc2x5IGR1bXBlZCBmcm9tIHRoZSBBUElcclxuICAgICAgICByZXR1cm4gbmV3IFByb21pc2UoKHJlcykgPT4ge1xyXG4gICAgICAgICAgICBQcm9taXNlLnJlc29sdmUoSlNPTi5wYXJzZShgW3tcImlkXCI6MTEsXCJuYW1lXCI6XCIxMHAgUm91bGV0dGUnJydcIixcImRvbWFpblwiOntcImlkXCI6MTAsXCJ2ZXJzaW9uXCI6MCxcIm5hbWVcIjpcImxpdmVzY29yZV91a1wifSxcInByb3ZpZGVyR2FtZUlkXCI6XCJwbGF5LTEwcC1yb3VsZXR0ZVwiLFwiZW5hYmxlZFwiOmZhbHNlLFwidmlzaWJsZVwiOnRydWUsXCJsb2NrZWRcIjpmYWxzZSxcImxvY2tlZE1lc3NhZ2VcIjpudWxsLFwiaGFzTG9ja0ltYWdlXCI6ZmFsc2UsXCJndWlkXCI6XCJzZXJ2aWNlLWNhc2luby1wcm92aWRlci1yb3hvcl9wbGF5LTEwcC1yb3VsZXR0ZVwiLFwiZGVzY3JpcHRpb25cIjpcIjEwcCBSb3VsZXR0ZSBkZXNjcmlwdGlvblwiLFwicnRwXCI6OTcuMyxcInByb3ZpZGVyR3VpZFwiOlwic2VydmljZS1jYXNpbm8tcHJvdmlkZXItcm94b3JcIixcImZyZWVTcGluRW5hYmxlZFwiOnRydWUsXCJmcmVlU3BpblZhbHVlUmVxdWlyZWRcIjp0cnVlLFwiZnJlZVNwaW5QbGF5VGhyb3VnaEVuYWJsZWRcIjpmYWxzZSxcImNhc2lub0NoaXBFbmFibGVkXCI6dHJ1ZSxcImdhbWVDdXJyZW5jeVwiOm51bGwsXCJnYW1lU3VwcGxpZXJcIjp7XCJpZFwiOjMsXCJ2ZXJzaW9uXCI6MSxcImRvbWFpblwiOntcImlkXCI6MTAsXCJ2ZXJzaW9uXCI6MCxcIm5hbWVcIjpcImxpdmVzY29yZV91a1wifSxcIm5hbWVcIjpcIlJveG9yIEdhbWluZ1wiLFwiZGVsZXRlZFwiOmZhbHNlfSxcImdhbWVUeXBlXCI6e1wiaWRcIjozLFwidmVyc2lvblwiOjAsXCJkb21haW5cIjp7XCJpZFwiOjEwLFwidmVyc2lvblwiOjAsXCJuYW1lXCI6XCJsaXZlc2NvcmVfdWtcIn0sXCJuYW1lXCI6XCJEZWV6dXNUZXN0XCIsXCJkZWxldGVkXCI6ZmFsc2V9LFwibGFiZWxzXCI6e1wib3NcIjp7XCJuYW1lXCI6XCJvc1wiLFwidmFsdWVcIjpcIkRlc2t0b3AsTW9iaWxlXCIsXCJkb21haW5OYW1lXCI6XCJsaXZlc2NvcmVfdWtcIixcImVuYWJsZWRcIjpmYWxzZSxcImRlbGV0ZWRcIjpmYWxzZX0sXCJUQUdcIjp7XCJuYW1lXCI6XCJUQUdcIixcInZhbHVlXCI6XCJHQU1FVEVTVEVSXCIsXCJkb21haW5OYW1lXCI6XCJsaXZlc2NvcmVfdWtcIixcImVuYWJsZWRcIjpmYWxzZSxcImRlbGV0ZWRcIjpmYWxzZX19LFwicHJvZ3Jlc3NpdmVKYWNrcG90XCI6ZmFsc2UsXCJuZXR3b3JrZWRKYWNrcG90UG9vbFwiOmZhbHNlLFwibG9jYWxKYWNrcG90UG9vbFwiOmZhbHNlLFwiZnJlZUdhbWVcIjpmYWxzZSxcImNkbkltYWdlVXJsXCI6XCJodHRwczovL3d3dy5saXZlc2NvcmViZXQuY29tL2Nhc2luby1pbWFnZXMvcGxheS0xMHAtcm91bGV0dGUtMjQwLnBuZ1wifSx7XCJpZFwiOjEyNixcIm5hbWVcIjpcIjEwcyBvciBCZXR0ZXJcIixcImRvbWFpblwiOntcImlkXCI6MTAsXCJ2ZXJzaW9uXCI6MCxcIm5hbWVcIjpcImxpdmVzY29yZV91a1wifSxcInByb3ZpZGVyR2FtZUlkXCI6XCI1MDQ0XCIsXCJlbmFibGVkXCI6dHJ1ZSxcInZpc2libGVcIjpmYWxzZSxcImxvY2tlZFwiOmZhbHNlLFwibG9ja2VkTWVzc2FnZVwiOm51bGwsXCJoYXNMb2NrSW1hZ2VcIjpmYWxzZSxcImd1aWRcIjpcInNlcnZpY2UtY2FzaW5vLXByb3ZpZGVyLWlmb3JpdW1fNTA0NFwiLFwiZGVzY3JpcHRpb25cIjpcIklmb3JpdW0gdGVzdCBwYWdlXCIsXCJydHBcIjpudWxsLFwicHJvdmlkZXJHdWlkXCI6XCJzZXJ2aWNlLWNhc2luby1wcm92aWRlci1pZm9yaXVtXCIsXCJmcmVlU3BpbkVuYWJsZWRcIjpmYWxzZSxcImZyZWVTcGluVmFsdWVSZXF1aXJlZFwiOmZhbHNlLFwiZnJlZVNwaW5QbGF5VGhyb3VnaEVuYWJsZWRcIjpmYWxzZSxcImNhc2lub0ZyZWVCZXRFbmFibGVkXCI6ZmFsc2UsXCJnYW1lQ3VycmVuY3lcIjp7XCJjdXJyZW5jeUNvZGVcIjpcIkdCUFwiLFwibWluaW11bUFtb3VudENlbnRzXCI6MX0sXCJnYW1lU3VwcGxpZXJcIjp7XCJpZFwiOjIxLFwidmVyc2lvblwiOjAsXCJkb21haW5cIjp7XCJpZFwiOjEwLFwidmVyc2lvblwiOjAsXCJuYW1lXCI6XCJsaXZlc2NvcmVfdWtcIn0sXCJuYW1lXCI6XCJpRm9yaXVtXCIsXCJkZWxldGVkXCI6ZmFsc2V9LFwiZ2FtZVR5cGVcIjpudWxsLFwibGFiZWxzXCI6e30sXCJwcm9ncmVzc2l2ZUphY2twb3RcIjpmYWxzZSxcIm5ldHdvcmtlZEphY2twb3RQb29sXCI6ZmFsc2UsXCJsb2NhbEphY2twb3RQb29sXCI6ZmFsc2UsXCJmcmVlR2FtZVwiOmZhbHNlLFwiY2RuSW1hZ2VVcmxcIjpudWxsfSx7XCJpZFwiOjEyNSxcIm5hbWVcIjpcIjExNTg4XCIsXCJkb21haW5cIjp7XCJpZFwiOjEwLFwidmVyc2lvblwiOjAsXCJuYW1lXCI6XCJsaXZlc2NvcmVfdWtcIn0sXCJwcm92aWRlckdhbWVJZFwiOlwiMTE1ODhcIixcImVuYWJsZWRcIjp0cnVlLFwidmlzaWJsZVwiOnRydWUsXCJsb2NrZWRcIjpmYWxzZSxcImxvY2tlZE1lc3NhZ2VcIjpudWxsLFwiaGFzTG9ja0ltYWdlXCI6ZmFsc2UsXCJndWlkXCI6XCJzZXJ2aWNlLWNhc2luby1wcm92aWRlci1pZm9yaXVtXzExNTg4XCIsXCJkZXNjcmlwdGlvblwiOlwiRTJFIFRlc3RzIGdhbWVcIixcInJ0cFwiOm51bGwsXCJwcm92aWRlckd1aWRcIjpcInNlcnZpY2UtY2FzaW5vLXByb3ZpZGVyLWlmb3JpdW1cIixcImZyZWVTcGluRW5hYmxlZFwiOmZhbHNlLFwiZnJlZVNwaW5WYWx1ZVJlcXVpcmVkXCI6ZmFsc2UsXCJmcmVlU3BpblBsYXlUaHJvdWdoRW5hYmxlZFwiOmZhbHNlLFwiY2FzaW5vRnJlZUJldEVuYWJsZWRcIjpmYWxzZSxcImdhbWVDdXJyZW5jeVwiOntcImN1cnJlbmN5Q29kZVwiOlwiR0JQXCIsXCJtaW5pbXVtQW1vdW50Q2VudHNcIjoxfSxcImdhbWVTdXBwbGllclwiOntcImlkXCI6MjEsXCJ2ZXJzaW9uXCI6MCxcImRvbWFpblwiOntcImlkXCI6MTAsXCJ2ZXJzaW9uXCI6MCxcIm5hbWVcIjpcImxpdmVzY29yZV91a1wifSxcIm5hbWVcIjpcImlGb3JpdW1cIixcImRlbGV0ZWRcIjpmYWxzZX0sXCJnYW1lVHlwZVwiOm51bGwsXCJsYWJlbHNcIjp7fSxcInByb2dyZXNzaXZlSmFja3BvdFwiOmZhbHNlLFwibmV0d29ya2VkSmFja3BvdFBvb2xcIjpmYWxzZSxcImxvY2FsSmFja3BvdFBvb2xcIjpmYWxzZSxcImZyZWVHYW1lXCI6ZmFsc2UsXCJjZG5JbWFnZVVybFwiOm51bGx9LHtcImlkXCI6NjMwLFwibmFtZVwiOlwiMTIzNGdhbWVyXCIsXCJkb21haW5cIjp7XCJpZFwiOjEwLFwidmVyc2lvblwiOjAsXCJuYW1lXCI6XCJsaXZlc2NvcmVfdWtcIn0sXCJwcm92aWRlckdhbWVJZFwiOlwiODk3NDY1XCIsXCJlbmFibGVkXCI6dHJ1ZSxcInZpc2libGVcIjpmYWxzZSxcImxvY2tlZFwiOmZhbHNlLFwibG9ja2VkTWVzc2FnZVwiOm51bGwsXCJoYXNMb2NrSW1hZ2VcIjpmYWxzZSxcImd1aWRcIjpcInNlcnZpY2UtY2FzaW5vLXByb3ZpZGVyLXNsb3RhcGlfODk3NDY1XCIsXCJkZXNjcmlwdGlvblwiOlwiaGRmc3Rnc3RkaHNmZ2hcIixcInJ0cFwiOm51bGwsXCJwcm92aWRlckd1aWRcIjpcInNlcnZpY2UtY2FzaW5vLXByb3ZpZGVyLXNsb3RhcGlcIixcImZyZWVTcGluRW5hYmxlZFwiOnRydWUsXCJmcmVlU3BpblZhbHVlUmVxdWlyZWRcIjpmYWxzZSxcImZyZWVTcGluUGxheVRocm91Z2hFbmFibGVkXCI6ZmFsc2UsXCJjYXNpbm9GcmVlQmV0RW5hYmxlZFwiOmZhbHNlLFwiZ2FtZUN1cnJlbmN5XCI6bnVsbCxcImdhbWVTdXBwbGllclwiOntcImlkXCI6NixcInZlcnNpb25cIjowLFwiZG9tYWluXCI6e1wiaWRcIjoxMCxcInZlcnNpb25cIjowLFwibmFtZVwiOlwibGl2ZXNjb3JlX3VrXCJ9LFwibmFtZVwiOlwiU2xvdEFQSVwiLFwiZGVsZXRlZFwiOmZhbHNlfSxcImdhbWVUeXBlXCI6bnVsbCxcImxhYmVsc1wiOnt9LFwicHJvZ3Jlc3NpdmVKYWNrcG90XCI6ZmFsc2UsXCJuZXR3b3JrZWRKYWNrcG90UG9vbFwiOmZhbHNlLFwibG9jYWxKYWNrcG90UG9vbFwiOmZhbHNlLFwiZnJlZUdhbWVcIjpmYWxzZSxcImNkbkltYWdlVXJsXCI6bnVsbH0se1wiaWRcIjoxMzUsXCJuYW1lXCI6XCIyMDIzNFwiLFwiZG9tYWluXCI6e1wiaWRcIjoxMCxcInZlcnNpb25cIjowLFwibmFtZVwiOlwibGl2ZXNjb3JlX3VrXCJ9LFwicHJvdmlkZXJHYW1lSWRcIjpcIjIwMjM0XCIsXCJlbmFibGVkXCI6dHJ1ZSxcInZpc2libGVcIjp0cnVlLFwibG9ja2VkXCI6ZmFsc2UsXCJsb2NrZWRNZXNzYWdlXCI6bnVsbCxcImhhc0xvY2tJbWFnZVwiOmZhbHNlLFwiZ3VpZFwiOlwic2VydmljZS1jYXNpbm8tcHJvdmlkZXItaWZvcml1bV8yMDIzNFwiLFwiZGVzY3JpcHRpb25cIjpcIjIwMjM0XCIsXCJydHBcIjpudWxsLFwicHJvdmlkZXJHdWlkXCI6XCJzZXJ2aWNlLWNhc2luby1wcm92aWRlci1pZm9yaXVtXCIsXCJmcmVlU3BpbkVuYWJsZWRcIjpmYWxzZSxcImZyZWVTcGluVmFsdWVSZXF1aXJlZFwiOmZhbHNlLFwiZnJlZVNwaW5QbGF5VGhyb3VnaEVuYWJsZWRcIjpmYWxzZSxcImNhc2lub0ZyZWVCZXRFbmFibGVkXCI6ZmFsc2UsXCJnYW1lQ3VycmVuY3lcIjp7XCJjdXJyZW5jeUNvZGVcIjpcIkdCUFwiLFwibWluaW11bUFtb3VudENlbnRzXCI6MX0sXCJnYW1lU3VwcGxpZXJcIjp7XCJpZFwiOjIxLFwidmVyc2lvblwiOjAsXCJkb21haW5cIjp7XCJpZFwiOjEwLFwidmVyc2lvblwiOjAsXCJuYW1lXCI6XCJsaXZlc2NvcmVfdWtcIn0sXCJuYW1lXCI6XCJpRm9yaXVtXCIsXCJkZWxldGVkXCI6ZmFsc2V9LFwiZ2FtZVR5cGVcIjpudWxsLFwibGFiZWxzXCI6e1wibnVsbFwiOntcIm5hbWVcIjpcIm51bGxcIixcInZhbHVlXCI6XCJudWxsXCIsXCJkb21haW5OYW1lXCI6XCJsaXZlc2NvcmVfdWtcIixcImVuYWJsZWRcIjpmYWxzZSxcImRlbGV0ZWRcIjpmYWxzZX19LFwicHJvZ3Jlc3NpdmVKYWNrcG90XCI6ZmFsc2UsXCJuZXR3b3JrZWRKYWNrcG90UG9vbFwiOmZhbHNlLFwibG9jYWxKYWNrcG90UG9vbFwiOmZhbHNlLFwiZnJlZUdhbWVcIjpmYWxzZSxcImNkbkltYWdlVXJsXCI6bnVsbH1dYCkpLnRoZW4oKGdhbWVzKSA9PiB7XHJcbiAgICAgICAgICAgICAgICBnYW1lcyA9IGdhbWVzLmZpbHRlcigoZ2FtZSkgPT4gZ2FtZS5lbmFibGVkID09IGVuYWJsZWQgJiYgZ2FtZS52aXNpYmxlID09IHZpc2libGUpO1xyXG4gICAgICAgICAgICAgICAgcmVzKGdhbWVzKTtcclxuICAgICAgICAgICAgfSk7XHJcbiAgICAgICAgfSk7XHJcbiAgICB9XHJcbiAgICAvLyBUT0RPOiBCYXNlIHRoaXMgb2ZmIGRvbWFpbiBhbmQgY2hhbm5lbFxyXG4gICAgZ2V0R2FtZVByb3ZpZGVycyhkb21haW4sIGNoYW5uZWwpIHtcclxuICAgICAgICByZXR1cm4gbmV3IFByb21pc2UoKHJlcykgPT4ge1xyXG4gICAgICAgICAgICBzZXRUaW1lb3V0KCgpID0+IHtcclxuICAgICAgICAgICAgICAgIHJlcyh0aGlzLmdhbWVQcm92aWRlcnMpO1xyXG4gICAgICAgICAgICB9LCAxMDAwKTtcclxuICAgICAgICB9KTtcclxuICAgIH1cclxuICAgIGdldFByb3ZpZGVyc0ZvckRvbWFpbihkb21haW5OYW1lKSB7XHJcbiAgICAgICAgcmV0dXJuIFByb21pc2UucmVzb2x2ZSh0aGlzLmdhbWVQcm92aWRlcnMuZmlsdGVyKCh4KSA9PiB4LmRvbWFpbiA9PT0gZG9tYWluTmFtZSkpO1xyXG4gICAgfVxyXG4gICAgZ2V0UHJvdmlkZXJzRm9yRG9tYWluQW5kQ2F0ZWdvcnkoZG9tYWluTmFtZSwgY2F0ZWdvcnkpIHtcclxuICAgICAgICByZXR1cm4gUHJvbWlzZS5yZXNvbHZlKHRoaXMuZ2FtZVByb3ZpZGVycy5maWx0ZXIoKHgpID0+IHsgdmFyIF9hOyByZXR1cm4geC5kb21haW4gPT09IGRvbWFpbk5hbWUgJiYgKChfYSA9IHguY2F0ZWdvcnkpID09PSBudWxsIHx8IF9hID09PSB2b2lkIDAgPyB2b2lkIDAgOiBfYS5uYW1lKSA9PT0gY2F0ZWdvcnkubmFtZTsgfSkpO1xyXG4gICAgfVxyXG59XHJcbmV4cG9ydHMuZGVmYXVsdCA9IEdhbWVzUHJvdmlkZXJNb2NrO1xyXG47XHJcbndpbmRvdy5WdWVHYW1lUHJvdmlkZXIgPSBuZXcgR2FtZXNQcm92aWRlck1vY2soKTtcclxuIiwiXCJ1c2Ugc3RyaWN0XCI7XHJcbk9iamVjdC5kZWZpbmVQcm9wZXJ0eShleHBvcnRzLCBcIl9fZXNNb2R1bGVcIiwgeyB2YWx1ZTogdHJ1ZSB9KTtcclxuY29uc3QgbmFub2lkXzEgPSByZXF1aXJlKFwibmFub2lkXCIpO1xyXG5jbGFzcyBEb21haW5JdGVtIHtcclxuICAgIGNvbnN0cnVjdG9yKGRpc3BsYXlOYW1lLCBuYW1lLCBwZCkge1xyXG4gICAgICAgIHRoaXMuZGlzcGxheU5hbWUgPSBkaXNwbGF5TmFtZTtcclxuICAgICAgICB0aGlzLm5hbWUgPSBuYW1lO1xyXG4gICAgICAgIHRoaXMucGQgPSBwZDtcclxuICAgICAgICB0aGlzLnJhbmRvbUlkID0gbmFub2lkXzEubmFub2lkKCk7XHJcbiAgICB9XHJcbn1cclxuZXhwb3J0cy5kZWZhdWx0ID0gRG9tYWluSXRlbTtcclxuIiwiXCJ1c2Ugc3RyaWN0XCI7XHJcbk9iamVjdC5kZWZpbmVQcm9wZXJ0eShleHBvcnRzLCBcIl9fZXNNb2R1bGVcIiwgeyB2YWx1ZTogdHJ1ZSB9KTtcclxuZXhwb3J0cy5FeHRyYUZpZWxkID0gZXhwb3J0cy5BY3Rpdml0eSA9IHZvaWQgMDtcclxuY29uc3QgbmFub2lkXzEgPSByZXF1aXJlKFwibmFub2lkXCIpO1xyXG5jbGFzcyBBY3Rpdml0eSB7XHJcbiAgICBjb25zdHJ1Y3RvcihuYW1lLCBwcm9tb1Byb3ZpZGVyKSB7XHJcbiAgICAgICAgdGhpcy5uYW1lID0gbmFtZTtcclxuICAgICAgICB0aGlzLnByb21vUHJvdmlkZXIgPSBwcm9tb1Byb3ZpZGVyO1xyXG4gICAgICAgIHRoaXMuaWQgPSBuYW5vaWRfMS5uYW5vaWQoKTtcclxuICAgIH1cclxufVxyXG5leHBvcnRzLkFjdGl2aXR5ID0gQWN0aXZpdHk7XHJcbmNsYXNzIEV4dHJhRmllbGQge1xyXG4gICAgY29uc3RydWN0b3IobmFtZSwgcHJvbW9Qcm92aWRlciA9IC0xLCBkYXRhVHlwZSA9IG51bGwsIGRlc2NyaXB0aW9uID0gbnVsbCkge1xyXG4gICAgICAgIHRoaXMubmFtZSA9IG5hbWU7XHJcbiAgICAgICAgdGhpcy5wcm9tb1Byb3ZpZGVyID0gcHJvbW9Qcm92aWRlcjtcclxuICAgICAgICB0aGlzLmRhdGFUeXBlID0gZGF0YVR5cGU7XHJcbiAgICAgICAgdGhpcy5kZXNjcmlwdGlvbiA9IGRlc2NyaXB0aW9uO1xyXG4gICAgICAgIHRoaXMuaWQgPSBuYW5vaWRfMS5uYW5vaWQoKTtcclxuICAgIH1cclxufVxyXG5leHBvcnRzLkV4dHJhRmllbGQgPSBFeHRyYUZpZWxkO1xyXG5jbGFzcyBHYW1lUHJvdmlkZXIge1xyXG4gICAgY29uc3RydWN0b3IobmFtZSwgZ2FtZXMgPSBbXSwgZG9tYWluLCB1cmwsIFxyXG4gICAgLy8gKyBGb3IgcHJvbW90aW9uc1xyXG4gICAgY2F0ZWdvcnkgPSBudWxsLCBhY3Rpdml0aWVzID0gW10sIGV4dHJhRmllbGRzID0gW10gLy8gLVxyXG4gICAgKSB7XHJcbiAgICAgICAgdGhpcy5uYW1lID0gbmFtZTtcclxuICAgICAgICB0aGlzLmdhbWVzID0gZ2FtZXM7XHJcbiAgICAgICAgdGhpcy5kb21haW4gPSBkb21haW47XHJcbiAgICAgICAgdGhpcy51cmwgPSB1cmw7XHJcbiAgICAgICAgdGhpcy5jYXRlZ29yeSA9IGNhdGVnb3J5O1xyXG4gICAgICAgIHRoaXMuYWN0aXZpdGllcyA9IGFjdGl2aXRpZXM7XHJcbiAgICAgICAgdGhpcy5leHRyYUZpZWxkcyA9IGV4dHJhRmllbGRzO1xyXG4gICAgICAgIHRoaXMuaWQgPSBuYW5vaWRfMS5uYW5vaWQoKTtcclxuICAgICAgICB0aGlzLmFjdGl2ZSA9IGZhbHNlO1xyXG4gICAgfVxyXG4gICAgYWRkR2FtZShnYW1lKSB7XHJcbiAgICAgICAgdGhpcy5nYW1lcy5wdXNoKGdhbWUpO1xyXG4gICAgfVxyXG59XHJcbmV4cG9ydHMuZGVmYXVsdCA9IEdhbWVQcm92aWRlcjtcclxuIiwiXCJ1c2Ugc3RyaWN0XCI7XHJcbk9iamVjdC5kZWZpbmVQcm9wZXJ0eShleHBvcnRzLCBcIl9fZXNNb2R1bGVcIiwgeyB2YWx1ZTogdHJ1ZSB9KTtcclxuY29uc3QgbmFub2lkXzEgPSByZXF1aXJlKFwibmFub2lkXCIpO1xyXG5jbGFzcyBMYXlvdXRCYW5uZXJJdGVtIHtcclxuICAgIGNvbnN0cnVjdG9yKG5hbWUsIGltYWdlKSB7XHJcbiAgICAgICAgdGhpcy5pZCA9IG5hbm9pZF8xLm5hbm9pZCgpO1xyXG4gICAgICAgIHRoaXMudXJsID0gJyc7XHJcbiAgICAgICAgdGhpcy5pbWFnZSA9ICcnO1xyXG4gICAgICAgIHRoaXMuZGlzcGxheV90ZXh0ID0gJyc7XHJcbiAgICAgICAgdGhpcy5mcm9tID0gJyc7XHJcbiAgICAgICAgdGhpcy50byA9ICcnO1xyXG4gICAgICAgIHRoaXMuZ2FtZUlEID0gJyc7XHJcbiAgICAgICAgdGhpcy50ZXJtc191cmwgPSAnJztcclxuICAgICAgICB0aGlzLnJ1bmNvdW50ID0gMDtcclxuICAgICAgICB0aGlzLm5hbWUgPSBuYW1lO1xyXG4gICAgICAgIHRoaXMuaW1hZ2UgPSBpbWFnZTtcclxuICAgIH1cclxufVxyXG5leHBvcnRzLmRlZmF1bHQgPSBMYXlvdXRCYW5uZXJJdGVtO1xyXG4iLCJcInVzZSBzdHJpY3RcIjtcclxuT2JqZWN0LmRlZmluZVByb3BlcnR5KGV4cG9ydHMsIFwiX19lc01vZHVsZVwiLCB7IHZhbHVlOiB0cnVlIH0pO1xyXG5jb25zdCBuYW5vaWRfMSA9IHJlcXVpcmUoXCJuYW5vaWRcIik7XHJcbmNsYXNzIENhdGVnb3J5IHtcclxuICAgIGNvbnN0cnVjdG9yKG5hbWUsIGRvbWFpbikge1xyXG4gICAgICAgIHRoaXMubmFtZSA9IG5hbWU7XHJcbiAgICAgICAgdGhpcy5kb21haW4gPSBkb21haW47XHJcbiAgICAgICAgdGhpcy5pZCA9IG5hbm9pZF8xLm5hbm9pZCgpO1xyXG4gICAgfVxyXG59XHJcbmV4cG9ydHMuZGVmYXVsdCA9IENhdGVnb3J5O1xyXG4iXSwic291cmNlUm9vdCI6IiJ9