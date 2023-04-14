'use strict';

angular.module('lithium').controller('ErrorMessagesController', ['domain', '$dt', '$translate', 'DTOptionsBuilder', '$state', '$security', 'rest-translate', '$uibModal', '$userService', 'notify', '$rootScope', '$scope', '$compile', 'UserRest',
    function(domain, $dt, $translate, DTOptionsBuilder, $state, $security, translateRest, $uibModal, $userService, notify, $rootScope, $scope, $compile, UserRest) {

        var controller = this;
        controller.data = {
            domainName: domain ? domain.name : domain
        };

        controller.tabs = [
            { name: 'Registration', title: $translate.instant('UI_NETWORK_ADMIN.BRANDCONFIG.TAB.ERROR_MESSAGES.TAB.REGISTRATION.TITLE'), roles: "ERROR_MESSAGES_VIEW"},
            { name: 'Login', title: $translate.instant('UI_NETWORK_ADMIN.BRANDCONFIG.TAB.ERROR_MESSAGES.TAB.LOGIN.TITLE'), roles: "ERROR_MESSAGES_VIEW"},
            { name: 'Password', title: $translate.instant('UI_NETWORK_ADMIN.BRANDCONFIG.TAB.ERROR_MESSAGES.TAB.PASSWORD.TITLE'), roles: "ERROR_MESSAGES_VIEW"},
            { name: 'Cashier', title: $translate.instant('UI_NETWORK_ADMIN.BRANDCONFIG.TAB.ERROR_MESSAGES.TAB.CASHIER.TITLE'), roles: "ERROR_MESSAGES_VIEW"},
            { name: 'My Account', title: $translate.instant('UI_NETWORK_ADMIN.BRANDCONFIG.TAB.ERROR_MESSAGES.TAB.MY_ACCOUNT.TITLE'), roles: "ERROR_MESSAGES_VIEW"},
            { name: 'Limit System Access', title: $translate.instant('UI_NETWORK_ADMIN.BRANDCONFIG.TAB.ERROR_MESSAGES.TAB.LIMIT_SYSTEM_ACCESS.TITLE'), roles: "ERROR_MESSAGES_VIEW"},
            { name: 'Restrictions', title: $translate.instant('UI_NETWORK_ADMIN.BRANDCONFIG.TAB.ERROR_MESSAGES.TAB.RESTRICTIONS.TITLE'), roles: "ERROR_MESSAGES_VIEW"},
            { name: 'Games', title: $translate.instant('UI_NETWORK_ADMIN.BRANDCONFIG.TAB.ERROR_MESSAGES.TAB.GAMES.TITLE'), roles: "ERROR_MESSAGES_VIEW"}
        ];

        controller.showErrorMessages = false;
        controller.resetTabs = function () {
            controller.showRegistration = false;
            controller.showLogin = false;
            controller.showPassword = false;
            controller.showCashier = false;
            controller.showMyAccount = false;
            controller.limitSystemAccess = false;
            controller.showRestrictions = false;
            controller.showGames = false;
        }
        controller.setTab = function(tab) {
            if (tab.tclass !== 'disabled') { //Ensures that you are not able to click on the disabled tabs
                controller.tab = tab;
                if (controller.tabs[0] == tab) {
                    controller.subModule = ['REGISTRATION'];
                    controller.resetTabs();
                    controller.showRegistration = true;
                    controller.domainSpecific = false;
                }
                if (controller.tabs[1] == tab) {
                    controller.subModule = ['LOGIN'];
                    controller.resetTabs();
                    controller.showLogin = true;
                    controller.domainSpecific = false;
                }
                if (controller.tabs[2] == tab) {
                    controller.subModule = ['PASSWORD'];
                    controller.resetTabs();
                    controller.showPassword = true;
                    controller.domainSpecific = false;
                }
                if (controller.tabs[3] == tab) {
                    controller.subModule = ['CASHIER'];
                    controller.resetTabs();
                    controller.showCashier = true;
                    controller.domainSpecific = false;
                }
                if (controller.tabs[4] == tab) {
                    controller.subModule = ['MY_ACCOUNT'];
                    controller.resetTabs();
                    controller.showMyAccount = true;
                    controller.domainSpecific = false;
                }
                if (controller.tabs[5] == tab) {
                    controller.subModule = ['LIMIT_SYSTEM_ACCESS'];
                    controller.resetTabs();
                    controller.limitSystemAccess = true;
                    controller.domainSpecific = false;
                }
                if (controller.tabs[6] == tab) {
                    controller.subModule = ['NORMAL_RESTRICTION', 'SYSTEM_RESTRICTION'];
                    controller.resetTabs();
                    controller.showRestrictions = true;
                    controller.domainSpecific = true;
                }
                if (controller.tabs[7] == tab) {
                    controller.subModule = ['GAMES'];
                    controller.resetTabs();
                    controller.showGames = true;
                    controller.domainSpecific = false;
                }
            }
        }

        controller.onInit = function() {

            controller.translatedLanguages = [];

            if (domain) {
                if ($security.hasRoleInTree("ERROR_MESSAGES_*")) {
                    angular.forEach(controller.tabs, function(tab) {
                        if ($state.includes(tab.name)) controller.tab = tab;
                    });

                    controller.setTab(controller.tabs[0]); //On init, show cash bonuses tab
                }

                controller.changelogs = {
                    domainName: domain.name,
                    entityId: domain.id,
                    restService: translateRest,
                    reload: 0
                }
            }
        }

        controller.onInit();

        controller.reload = function () {
            if (domain) {
                controller.changelogs.reload++;
            }
        };
        controller.reload();

        //Enabling the error-messages directive to reload changelogs
        controller.data.reload = controller.reload;
    }]);
