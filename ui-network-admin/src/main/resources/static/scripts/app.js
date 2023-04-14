'use strict';
Vue.use(Vuetify)
const router = new VueRouter({
    mode: 'abstract'
})
router.beforeEach(async (to, from, next) => {
    // This is BAD! But because we can't await it
    // on load, we have to have this check.
    if(!appVue.hasFetchedRoute) {
        await window.VueFetchRouteList()
    }

    const path = to.hash.replace('#', '') // Get the hash, convert to path
    const matchedRoutes = appVue.routePaths.filter(x => x.path === path) // Get any plugins that match the route
    // Iteratively load the plugin associated
    for(const route of matchedRoutes) {
        window.VueLoadPlugin(route)
    }

    // next() DO NOT CALL THIS! Unless you want an infinite loop :)
})

// Vue needs to be created and mounted prior to Angular loading itself
window.parentVueInstance = new Vue({
    el: "#app",
    vuetify: new Vuetify(),
    mounted: function () {
        // I would have preferred to await these,
        // however Angular is not async so there
        // is turmoil when we try
        window.VueFetchRouteList()
        window.VueFetchPluginList()

        // Build the store
        const StoreProxy = {}
        window.VueSetStore(new Vuex.Store(), StoreProxy)

        initAngular()
    }
})

function initAngular() {
    angular.module('lithium', ['pascalprecht.translate', 'ngMessages', 'ngAnimate', 'oc.lazyLoad', 'ui.router', 'datatables.scroller',
        'ui.bootstrap', 'datatables.select', 'angular-loading-bar', 'userService', 'lithium-dt', 'chart.js', 'gridshore.c3js.chart', 'lithium-dt2', 'ngResource',
        'litSecurity', 'ncy-angular-breadcrumb', 'formly', 'formlyBootstrap', 'ngSanitize', 'ui.select', 'restangular',
        'lithium-rest-domain', 'lithium-rest-provider-auth-client', 'lithium-rest-cashier', 'lithium-rest-group', 'lithium-rest-roles', 'lithium-rest-accounting', 'lithium-rest-accounting-internal', 'lithium-rest-tranta', 'lithium-rest-trans-type', 'lithium-rest-casino', 'lithium-rest-userevents',
        'lithium-rest-mail', 'lithium-rest-accesscontrol', 'lithium-rest-accessrule', 'lithium-rest-signupevents', 'lithium-rest-authorization', 'lithium-rest-sms', 'lithium-rest-user-limits', 'lithium-rest-cataboom-campaigns', 'lithium-rest-user-mass-action',
        'lithium-rest-domain-limits', 'lithium-rest-domain-age-limits', 'lithium-rest-cashier-dmp', 'lithium-rest-cashier-dm', 'angular-growl', 'ckeditor', 'ui.checkbox', 'angular.filter', 'naif.base64', 'bootstrapLightbox', 'lithium-rest-pushmsg', 'lithium-rest-translate',
        'toggle-switch', 'ui.mask', 'ui.utils.masks', 'angular-jwt', 'ui.tree', 'bsLoadingOverlay', 'bsLoadingOverlaySpinJs', 'angular-timezone-selector', 'angular-cron-gen', 'ui.ace', 'ngFileUpload', 'lithium-rest-document', 'changelogService'])
        .constant('RRule', rrule)
        .filter('decodeURIComponent', function () {
            return window.decodeURIComponent;
        })
        .filter('wordBreak', function () {
            return function (string) {
                return string.replace(/(.{1})/g, '$1​');
            }
        })
        .filter('unique', function () {
            return function (collection, keyname) {
                var output = [], keys = [];
                angular.forEach(collection, function (item) {
                    var key = item[keyname];
                    if (keys.indexOf(key) === -1) {
                        keys.push(key);
                        output.push(item);
                    }
                });
                return output;
            }
        })
        .filter('isEmpty', function () {
            var bar;
            return function (obj) {
                for (bar in obj) {
                    if (obj.hasOwnProperty(bar)) {
                        return false;
                    }
                }
                return true;
            };
        })
        .filter('cents', function ($filter) {
            return function (amount, symbol, fractionSize) {
                if (angular.isDefined(amount) && amount != null && isFinite(amount)) {
//				console.log("before big: ", amount, isFinite(amount), amount == null);
                    amount = (new Big(amount).div(100)).toString();
                } else {
                    return '';
                }
                return $filter('currency')(amount, symbol, fractionSize);
            }
        })
        .filter('datedefault', function ($filter) {
            return function (value) {
                return $filter('date')(value, 'yyyy-MM-dd', 'GMT');
            }
        })
        .filter('datetimedefault', function ($filter) {
            return function (value) {
                return $filter('date')(value, 'yyyy-MM-dd HH:mm:ss', 'GMT');
            }
        })
        .filter('datetimegmt', function ($filter) {
            return function (value) {
                return $filter('date')(value, "MMM d, y h:mm:ss a", 'GMT');
            }
        })
        .run(["$http", '$uibModal', "$timeout", "$menu", "$translate", "$rootScope", "$state", "$stateParams", "$userService", "$log", "$templateCache", "$transitions", "$q", "Restangular", "jwtHelper", "bsLoadingOverlayService", "rest-games", "CmsAssetRest", "CasinoCMSRest", "rest-progressive-feeds",
            async function ($http,$uibModal, $timeout, $menu, $translate, $rootScope, $state, $stateParams, $userService, $log, $templateCache, $transitions, $q, Restangular, jwtHelper, bsLoadingOverlayService, gamesRest, cmsAssetRest, casinoCmsRest, progressiveFeedsRest) {
                //Removing the prettyCron component until legal matters can be resolved.
                window.prettyCron = {};
                window.prettyCron.toString = function(cronspec, sixth) {
                    return cronspec;
                };

                const apiClient = window.VueCreateAxiosClient($userService)

                // Proxying from Angular Router to Vue Router
               $rootScope.$on("$locationChangeSuccess", (evt) => {
                    router.push({ path: window.location.hash })
                })

                    // Create a base provider object that we can attach all our objects
                // required for the Vue instance
                $rootScope.provide = {
                    authentication: {
                        onSuccess: function (access, refresh) {
                            $userService.onSuccessfulAuth(access, refresh)
                        },
                        onFail: function () {
                            $userService.onFailAuth()
                        }
                    },
                    bankAccountLookupGeneration: {},
                    pageHeaderProvider: {},
                    cashierConfigProvider: {},
                    documentGeneration: { data: {}},
                    cashierProvider: {},
                    casinoCmsProvider: casinoCmsRest,
                    domainProvider: window.VueDomainProvider,
                    quickActionProvider: {},
                    playerKYCProvider: {},
                    dropDownMenuProvider: {},
                    gamesProvider: gamesRest,
                    promotionsGamesProvider: window.VueGameProvider, // TODO: Why is games provider not the same interface as the SDK
                    bannerImagesProvider: cmsAssetRest,
                    progressiveFeedsProvider: progressiveFeedsRest,
                    rewardProvider: {data: {}},
                    challengeProvider: window.VueChallengeProvider
                }

                // When required, create a new Vue mounting instance,
                // forwarding the required objects as Dependancy Injection providers
                window.VueMount = async (script, mountElementId) => {
                    // Wait for the DOM element to be visible (hack around Angular load times)
                    await window.VueWaitForElement(mountElementId)

                    const ChildVueCtor = Vue.extend(window.parentVueInstance)
                    new ChildVueCtor({
                        el: mountElementId,
                        vuetify: new Vuetify(),
                        components: {},
                        provide: { // Make sure to add the required properties here
                            extHttp: $http,
                            rootScope: $rootScope,
                            userService: $userService,
                            translateService: $translate,
                            listenerService: window.vueListener,
                            logService: window.vueLog,
                            storeService: window.vueStore,
                            apiClients: apiClient
                        },
                        render: (r) => r(script)
                    })
                }

                // It's very handy to add references to $state and $stateParams to the $rootScope
                // so that you can access them from any scope within your applications.For example,
                // <li ng-class="{ active: $state.includes('contacts.list') }"> will set the <li>
                // to active whenever 'contacts.list' or one of its decendents is active.
                $rootScope.$on("$locationChangeSuccess", function (event) {
                    var titleTranslate = getTitle(event);

                    $translate(titleTranslate).then(function (translations) {
                        $rootScope.title = translations;
                    });
                });

                function getTitle(event) {
                    var state = event.targetScope.$state.current;

                    if (state.data && state.data.pageTitle) {
                        return state.data.pageTitle;
                    }
                    return "UI_NETWORK_ADMIN.PAGE.TITLE.DEFAULT";
                }

                bsLoadingOverlayService.setGlobalConfig({
                    templateUrl: '../templates/loading-overlay-template.html',
//			templateOptions: {
//				color: '#d6eaef',
//				lines: 11 // The number of lines to draw
//				, length: 25 // The length of each line
//				, width: 10 // The line thickness
//				, radius: 5 // The radius of the inner circle
//				, scale: 0.5 // Scales overall size of the spinner
//				, corners: 1 // Corner roundness (0..1)
//				, opacity: 0.15 // Opacity of the lines
//				, rotate: 8 // The rotation offset
//				, direction: 1 // 1: clockwise, -1: counterclockwise
//				, speed: 0.9 // Rounds per second
//				, trail: 65 // Afterglow percentage
//				, fps: 20 // Frames per second when using setTimeout() as a fallback for CSS
//				, zIndex: 2e9 // The z-index (defaults to 2000000000)
//				, className: 'spinner' // The CSS class to assign to the spinner
//				, top: '50%' // Top position relative to parent
//				, left: '50%' // Left position relative to parent
//				, shadow: false // Whether to render a shadow
//				, hwaccel: false // Whether to use hardware acceleration
//				, position: 'absolute' // Element positioning
//			}
                });
                $rootScope.refreshNotificationCount = 0;

                // https://stackoverflow.com/questions/105034/how-to-create-a-guid-uuid
                $rootScope.generateUuidv4 = function () {
                    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
                        var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
                        return v.toString(16);
                    });
                };

                $rootScope.queryCurrentSession = function () {
                    return sessionStorage.getItem('appInstanceId');
                }

                $rootScope.generateSessionIfNecessary = function () {
                    // data in sessionStorage is cleared when the page session ends.
                    // A page session lasts as long as the browser is open, and survives over page reloads and restores.
                    if ($rootScope.queryCurrentSession()) {
                        return;
                    }
                    sessionStorage.setItem('appInstanceId', $rootScope.generateUuidv4());
                };

                $rootScope.queryCurrentSessionTimeout = function () {
                    var expires_in = localStorage.getItem("expires_in");
                    return expires_in;
                };

                $rootScope.generateSessionIfNecessary();

                angular.isUndefinedOrNull = function (val) {
                    return angular.isUndefined(val) || val === null
                }

                angular.range = (min, max) => Array.from({ length: max - min + 1 }, (_, i) => min + i)

                angular.confirmDialog = (confirmDialogConfig) => {

                    if(angular.isUndefinedOrNull(confirmDialogConfig) || typeof confirmDialogConfig !== 'object') {
                        throw new Error($translate.instant("UI_NETWORK_ADMIN.DIALOGS.CONFIRM.ERRORS.INVALID_CONFIRM_OBJECT"))
                    }

                    if(angular.isUndefinedOrNull(confirmDialogConfig.onConfirm) || typeof confirmDialogConfig.onConfirm !== 'function') {
                        throw new Error($translate.instant("UI_NETWORK_ADMIN.DIALOGS.CONFIRM.ERRORS.MISSING_CONFIRM_FUNCTION"))
                    }

                    if(angular.isUndefinedOrNull(confirmDialogConfig.title) || typeof confirmDialogConfig.title !== 'string') {
                        throw new Error($translate.instant("UI_NETWORK_ADMIN.DIALOGS.CONFIRM.ERRORS.MISSING_TITLE"))
                    }

                    if(angular.isUndefinedOrNull(confirmDialogConfig.message) || typeof confirmDialogConfig.message !== 'string') {
                        throw new Error($translate.instant("UI_NETWORK_ADMIN.DIALOGS.CONFIRM.ERRORS.MISSING_MESSAGE"))
                    }

                    var modalInstance = $uibModal.open({
                        animation: true,
                        ariaLabelledBy: 'modal-title',
                        ariaDescribedBy: 'modal-body',
                        templateUrl: 'scripts/controllers/dialogs/confirm.html',
                        controller: 'ConfirmationDialog',
                        controllerAs: 'controller',
                        keyboard: false,
                        backdrop: 'static',
                        size: 'md',
                        resolve: {
                            title: () => confirmDialogConfig.title,
                            message: () => confirmDialogConfig.message,
                            onConfirm: () => {
                                return () => {
                                    confirmDialogConfig.onConfirm.call(null)
                                    modalInstance.dismiss('cancel')
                                }
                            },
                            confirmButtonText: () => confirmDialogConfig.confirmButtonText,
                            cancelButtonText: () => confirmDialogConfig.cancelButtonText,
                            confirmType:() => confirmDialogConfig.confirmType,
                            confirmIcon: () => confirmDialogConfig.confirmButtonIcon,
                            loadMyFiles: function($ocLazyLoad) {
                                return $ocLazyLoad.load({
                                    name:'lithium',
                                    files: ['scripts/controllers/dialogs/confirm.js']
                                })
                            }
                        }
                    });
                }

                $http.get("version").then(function success(response) {
                    $rootScope.version = response.data.data;
                });

                $rootScope.$state = $state;
                $rootScope.$stateParams = $stateParams;
                $rootScope.currentdate = new Date();

                $rootScope.logException = function (exception, cause) {
                    console.error(exception, cause);
                }

                $rootScope.logout = function () {
                    $userService.logout();
                    $state.go("login");
                }
                $rootScope.extendSession = function () {
                    $userService.extendSession();
                }

                $transitions.onSuccess({}, function (t) {
                    $menu.setActive();
                    $rootScope.sidebarOpen = false;
                });

                $transitions.onStart({to: 'dashboard.**', from: '*'}, function ($transition$) {

                    if ($transition$.to().redirectTo) {
                        $state.go($transition$.to().redirectTo, $transition$.params())
                        return false;
                    }

                    if ($userService.authenticated) {
                        return true;
                    }
                    var deferred = $q.defer();

                    $rootScope.toState = $transition$.to().name;
                    $rootScope.toParams = $transition$.params();
                    $userService.authenticate(null).then(function (result) {
                        $state.go($rootScope.toState, $rootScope.toParams);
                    }, function (error) {
                        $state.go('login');
                    });
                    return deferred.promise;
                });
//		$transitions.onBefore({}, function($state, $transition$) {
//			$log.info($state);
//			$log.info($transition$);
//			if ($transition$ != null) {
//				if (!$transition$.valid()) {
//					var params = $transition$.params();
//					if (isNaN(params.x)) {
//						params.x = null;
//					}
//					return $state.target($transition$.to(), params, $transition$.options())
//				}
//			}
//		});

                $rootScope.clearCache = function () {
                    $templateCache.removeAll();
                    $log.info("Removed all pages from cache");
                };

                angular.element(document).ready(function () {
                    $timeout(function () {
                        $.AdminLTE.layout.fix();
                    }, 100);
                    $timeout(function () {
                        $.AdminLTE.layout.fix();
                    }, 500);
                    $timeout(function () {
                        $.AdminLTE.layout.fix();
                    }, 1000);
                });

            }])
        .config(["growlProvider", "$translateProvider", "$stateProvider", "$urlRouterProvider", "$ocLazyLoadProvider", "$httpProvider", "ChartJsProvider", "$breadcrumbProvider", "formlyConfigProvider", "RestangularProvider", "$urlMatcherFactoryProvider", "uiMask.ConfigProvider", "$provide", "jwtOptionsProvider", "jwtInterceptorProvider", "LightboxProvider",
            function (growlProvider, $translateProvider, $stateProvider, $urlRouterProvider, $ocLazyLoadProvider, $httpProvider, ChartJsProvider, $breadcrumbProvider, formlyConfigProvider, RestangularProvider, $urlMatcherFactoryProvider, uiMaskConfigProvider, $provide, jwtOptionsProvider, jwtInterceptorProvider, LightboxProvider) {
                LightboxProvider.templateUrl = 'scripts/directives/lightbox/lightbox.html';
                LightboxProvider.calculateModalDimensions = function (dimensions) {
                    var width = Math.max(400, dimensions.imageDisplayWidth + 32);

                    if (width >= dimensions.windowWidth - 20 || dimensions.windowWidth < 768) {
                        width = 'auto';
                    }

                    return {
                        'width': width, // default
                        'height': 'auto' // custom
                    };
                };

                jwtOptionsProvider.config({
                    tokenGetter: ['$injector', 'jwtHelper', '$http', function ($injector, jwtHelper, $http) {
                        var $rootScope = $injector.get("$rootScope");
                        var notify = $injector.get("notify");

                        if ((!localStorage.getItem('lithium-oauth-token') || !localStorage.getItem('lithium-refresh-token')) && ($rootScope.token || $rootScope.refreshToken)) {
                            $rootScope.logout();
                            notify.error("You were logged out because your session expired. Please login again to proceed.");
                        }

                        var jwt = $rootScope.token;
                        var refreshToken = $rootScope.refreshToken;

                        if ((!$rootScope.refreshInProgress) && (refreshToken)) {
                            if (jwtHelper.isTokenExpired(refreshToken)) {
                                $rootScope.logout();
                            } else {
                                $rootScope.refreshInProgress = true;
                                $rootScope.extendSession();
                            }
                        }
                        return jwt;
                    }]
                });
                $httpProvider.interceptors.push('jwtInterceptor');

                $provide.decorator("$exceptionHandler", function ($delegate, $injector) {
                    var $location;
                    return function (exception, cause, displayErrorPage) {
                        var $rootScope = $injector.get("$rootScope");
                        var $state = $injector.get("$state");
                        var $stateParams = $injector.get("$stateParams");
                        var notify = $injector.get("notify");
                        $location = $location || $injector.get('$location');
                        $rootScope.logException(exception, cause);

                        if (displayErrorPage === true) {
                            if (exception.status === 401) {
                                notify.error("There was a problem with your login, or your session has expired.. Please login again.");
                                $rootScope.logout();
                            } else if (exception.status === 403) {
                                $state.transitionTo('dashboard.403', $stateParams, {
                                    location: false,
                                    inherit: false
                                }).then(function (result) {
                                    //						console.log(result);
                                    notify.error("You are not authorized to view this page, or your session has expired..");
                                });
                            } else if (exception.status === 404) {
                                console.log("404");
                                $state.transitionTo('dashboard.404', $stateParams, {
                                    location: false,
                                    inherit: true
                                }).then(function (result) {
                                    //						console.log(result);
                                    notify.error("We could not find the page you were looking for..");
                                });
                            } else if (exception.status === 500) {
                                $state.transitionTo('dashboard.500', $stateParams, {
                                    location: false,
                                    inherit: false
                                }).then(function (result) {
                                    console.log(result);
                                    notify.error("Something unexpected happened, please try again.");
                                });
                            } else {
                                //					$state.transitionTo('login', $stateParams, {location: false, reload: true, inherit: false, notify: true}).then(function(result) {
                                //					console.log("/login");
                                //						console.log(result);
                                notify.error("You are not authorized to view this page, or your session has expired..");
                                //					});
                                //$location.path("/login");
                            }
                        } else {
                            if (exception.reason !== undefined && exception.reason.status !== undefined && exception.reason.status === 403) {
                                notify.error(exception.reason.data.error_description);
                            }
                        }
                    };
                });

                uiMaskConfigProvider.maskDefinitions({'@': /[a-zA-Z]/, '#': /[0-9]/});

                growlProvider.globalTimeToLive(3000);
//		growlProvider.globalReversedOrder(true);
                growlProvider.globalDisableCountDown(true);
                growlProvider.globalPosition('bottom-right');

                RestangularProvider.addResponseInterceptor(function (data, operation, what, url, response, deferred) {
                    var extractedData = [];
                    if (angular.isObject(data.data)) {
                        extractedData = data.data;
                        extractedData._status = data.status;
                        extractedData._message = data.message;
                        if (angular.isDefined(data.successful)) {
                            extractedData._successful = data.successful;
                        }
                        if (angular.isDefined(data.data2)) {
                            extractedData._data2 = data.data2;
                        }
                    } else {
                        extractedData = data.data;  /// ???
                        if (extractedData === null) {
                            extractedData = [];
                            if (angular.isDefined(data.status)) {
                                extractedData._status = data.status;
                            }
                            if (angular.isDefined(data.message)) {
                                extractedData._message = data.message;
                            }
                            if (angular.isDefined(data.data2)) {
                                extractedData._data2 = data.data2;
                            }
                        }
//				console.log("??");
                    }
                    if (data.successful === false) {
                        if (extractedData === null) {
                            extractedData = [];
                        }
                        if (angular.isDefined(data.status)) {
                            extractedData._status = data.status;
                        }
                        if (angular.isDefined(data.message)) {
                            extractedData._message = data.message;
                        }
                        if (angular.isDefined(data.successful)) {
                            extractedData._successful = data.successful;
                        }
                        if (angular.isDefined(data.data2)) {
                            extractedData._data2 = data.data2;
                        }
                    } else if (data.length > 0) {
                        extractedData = data;
                    }
//			console.log(extractedData);
                    return extractedData;
                });

                ChartJsProvider.setOptions({colors: ['#FF0000', '#aaaaaa', '#aaaaaa', '#46BFBD', '#FDB45C', '#949FB1', '#4D5360']});

                $breadcrumbProvider.setOptions({
                    prefixStateName: 'dashboard.home-beta',
                    templateUrl: 'templates/breadcrumb.html'
                });

                formlyConfig(formlyConfigProvider);

                $translateProvider.useMissingTranslationHandler('lithiumCustomTranslationHandlerFactory');
                $translateProvider.useSanitizeValueStrategy('escape');
                $translateProvider.useUrlLoader('services/service-translate/apiv2/translations/angular/ui_network_admin/get');
                $translateProvider.useCookieStorage();
                $translateProvider.determinePreferredLanguage();

                statesConfig($stateProvider);

                $ocLazyLoadProvider.config({debug: false, events: true});
                $urlRouterProvider.when('', function ($state, $location) {
                    $state.go("dashboard.home-beta");
                });
                $urlRouterProvider.otherwise(function ($injector, $location) {
//			console.log("injector: ", $injector);
//			console.log("location: ", $location);
//			var $userService = $injector.get('$userService');
                    var notify = $injector.get("notify");
                    notify.error("We could not find the page you were looking for..");

                    var state = $injector.get('$state');
                    state.go("dashboard.404");
                });

                $httpProvider.defaults.headers.common["X-Requested-With"] = 'XMLHttpRequest';
                $httpProvider.defaults.headers.common['Accept'] = 'application/json';

                $httpProvider.interceptors.push('httpInterceptor');
            }])
        .factory('httpInterceptor', function ($exceptionHandler, $q, $rootScope, errors) {
            return {
//			request: function(config) {
//				console.log($rootScope.token);
//				return config;
//			},
                responseError: function responseError(rejection) {
                    var message = '';
                    if (angular.isDefined(rejection.data)) {
                        if (angular.isUndefinedOrNull(rejection.data.message)) {
                            message = '';
                        } else {
                            message = rejection.data.message;
                        }
                    }
                    if (rejection.status === 401) {
                        errors.catch(message, true)({reason: rejection});
                    } else {
                        errors.catch(message, false)({reason: rejection});
                    }
                    return $q.reject(rejection);
                }
            };
        })
        .factory('errors', function ($translate, notify, $exceptionHandler, $q) {
            return {
                catch: function (message, displayErrorPage) {
                    return function (reason) {
                        if (message) {
                            $translate(message).then(function (t) {
                                notify.error(t);
                            }).catch(function (error) {
                                notify.error(error);
                            });
                        }
                        $exceptionHandler(reason, message, displayErrorPage);
                    };
                }
            }
        })
        .factory('lithiumCustomTranslationHandlerFactory', function () {
            return function (translationID) {
                //We will be returning the translation code when the translation code was not translated
                return translationID;
            };
        });
}
