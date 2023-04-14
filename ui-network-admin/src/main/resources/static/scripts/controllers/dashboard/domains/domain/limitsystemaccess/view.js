angular.module('lithium')
    .controller('LimitSystemAccess', ["domain", "rest-domain", "notify", "$uibModal", "$dt", "DTOptionsBuilder", "RestLimitSysAccess", "$translate",
        function(domain, restDomain, notify, $uibModal, $dt, DTOptionsBuilder, restLimitSysAccess, $translate) {
            var controller = this;

            controller.listAccLimits = function() {
                restLimitSysAccess.all(domain.name).then(function(response) {
                    controller.llsa = response.plain();
                });
            }
            controller.listAccLimits();

            controller.toggleLimit = function(limit, rowName, enabledValue, errorMessageKey) {
                console.log("row v-reset-row Name= " + rowName);
                console.log("new Row value=" + enabledValue.toString());
                console.log(limit);

                let modalInstance = $uibModal.open({
                    animation: true,
                    ariaLabelledBy: 'modal-title',
                    ariaDescribedBy: 'modal-body',
                    templateUrl: 'scripts/controllers/dashboard/brandConfig/errormessages/editerrormessage.html',
                    controller: 'EditErrorMessageModal',
                    controllerAs: 'controller',
                    backdrop: 'static',
                    size: 'md',
                    resolve: {
                        domainName: function() {
                            return domain.name;
                        },
                        translationData: ['rest-translate', function(translateRest) {
                            return translateRest.getKeyIdByCode(errorMessageKey, domain.name).then(function(keyId) {
                                return {id: keyId};
                            });
                        }],
                        domainSpecific: function() {
                            return false;
                        },
                        toggleErrorMessage: function() {
                            return {
                                enabled: true,
                                label: $translate.instant("UI_NETWORK_ADMIN.DOMAIN.LIMIT_SYSTEM_ACCESS.VIEW.TOGGLE_ERROR_MESSAGE.REASON.LABEL"),
                                placeholder: $translate.instant("UI_NETWORK_ADMIN.DOMAIN.LIMIT_SYSTEM_ACCESS.VIEW.TOGGLE_ERROR_MESSAGE.REASON.PLACEHOLDER"),
                                buttonType: enabledValue ? $translate.instant("UI_NETWORK_ADMIN.DOMAIN.TOGGLE_ERROR_MESSAGE.REASON.LABEL.ENABLED") :  $translate.instant("UI_NETWORK_ADMIN.DOMAIN.TOGGLE_ERROR_MESSAGE.REASON.LABEL.DISABLED")
                            }
                        },
                        loadMyFiles: function($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'lithium',
                                files: ['scripts/controllers/dashboard/brandConfig/errormessages/editerrormessage.js']
                            })
                        }
                    }
                });

                modalInstance.result.then(function (response) {
                    if (response.action === 'accept') {
                        switch (rowName) {
                            case 'login' : {
                                limit.login = enabledValue;
                                break;
                            }
                            case 'deposit' : {
                                limit.deposit = enabledValue;
                                break;
                            }
                            case 'withdraw' : {
                                limit.withdraw = enabledValue;
                                break;
                            }
                            case 'betPlacement' : {
                                limit.betPlacement = enabledValue;
                                break;
                            }
                            case 'casino' : {
                                limit.casino = enabledValue;
                                break;
                            }
                        }
                        let lim = {
                            domainName: domain.name,
                            verificationId: limit.verificationStatus.id,
                            login: limit.login,
                            deposit: limit.deposit,
                            withdraw: limit.withdraw,
                            betPlacement: limit.betPlacement,
                            casino: limit.casino,
                            comment: response.comment
                        }
                        console.log(lim)

                        restLimitSysAccess.saveLimitSystemAccess(domain.name, JSON.stringify(lim)).then(function (response) {
                        }, function (status) {
                            notify.error("Unable to save. Please try again.");
                        });
                    } else {
                        switch (rowName) {
                            case 'login' : {
                                limit.login = !enabledValue;
                                break;
                            }
                            case 'deposit' : {
                                limit.deposit = !enabledValue;
                                break;
                            }
                            case 'withdraw' : {
                                limit.withdraw = !enabledValue;
                                break;
                            }
                            case 'betPlacement' : {
                                limit.betPlacement = !enabledValue;
                                break;
                            }
                            case 'casino' : {
                                limit.casino = !enabledValue;
                                break;
                            }
                        }
                    }
                });
            };

            controller.getErrorMessageKey = function(verificationStatusCode, restriction) {
                return 'ERROR_DICTIONARY.LIMIT_SYSTEM_ACCESS.' + verificationStatusCode.replace(" ", "_").toUpperCase() + '.' + restriction;
            }

            controller.editTranslation = function (errorMessageKey) {
                let modalInstance = $uibModal.open({
                    animation: true,
                    ariaLabelledBy: 'modal-title',
                    ariaDescribedBy: 'modal-body',
                    templateUrl: 'scripts/controllers/dashboard/brandConfig/errormessages/editerrormessage.html',
                    controller: 'EditErrorMessageModal',
                    controllerAs: 'controller',
                    backdrop: 'static',
                    size: 'md',
                    resolve: {
                        domainName: function() {
                            return domain.name;
                        },
                        translationData: ['rest-translate', function(translateRest) {
                            return translateRest.getKeyIdByCode(errorMessageKey, domain.name).then(function(keyId) {
                                return {id: keyId};
                            });
                        }],
                        domainSpecific: function() {
                            return false;
                        },
                        toggleErrorMessage: function() {
                            return {
                                enabled: false,
                            }
                        },
                        loadMyFiles: function($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'lithium',
                                files: ['scripts/controllers/dashboard/brandConfig/errormessages/editerrormessage.js']
                            })
                        }
                    }
                });

                modalInstance.result.then(function (response) {
                });
            };
            controller.model = domain;
            controller.changelogs = {
                domainName: domain.name,
                entityId: domain.id,
                restService: restLimitSysAccess,
                reload: 0
            }
        }
    ]);
