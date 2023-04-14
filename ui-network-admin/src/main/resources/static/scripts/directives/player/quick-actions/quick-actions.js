'use strict';

angular.module('lithium').directive('quickActions', function() {
    return {
        templateUrl: '/scripts/directives/player/quick-actions/quick-actions.html',
        scope: {
            data: "=",
            user: "=ngModel",
        },
        restrict: 'E',
        replace: true,
        controllerAs: 'controller',
        controller: ['$translate', '$scope', '$rootScope','notify', '$state', 'UserRest', 'errors', '$uibModal','bsLoadingOverlayService', 'rest-accounting','userLimitsRest', 'ExclusionRest','CoolOffRest', 'UserRestrictionsRest','BvnVerifyRest', 'KycVerifyRest', 'rest-domain', 'StatusRest', 'rest-games', 'dev-tool-rest', '$userService', 'EcosysRest',
            function ($translate, $scope, $rootScope, notify, $state, UserRest, errors, $uibModal, bsLoadingOverlayService, restAcc, userLimitsRest, exclusionRest, restCoolOff, userRestrictionsRest, bvnVerifyRest, kycVerifyRest, restDomain, statusRest, gameRest, devToolRest, $userService, EcosysRest) {
                let controller = this;

                controller.domainSettings = function() {
                    restDomain.findCurrentDomainSettings($state.params.domainName).then(function(response) {
                        let settings = response.plain();
                        let objSettings = {};
                        for (let i = 0; i < settings.length; i++) {
                            let domainSetting = settings[i];
                            objSettings[domainSetting.labelValue.label.name] = domainSetting.labelValue.value;
                        }
                        $scope.data.domainSettings = objSettings;
                    }).catch(function(error) {
                        notify.error($translate.instant('UI_NETWORK_ADMIN.DOMAIN.SETTINGS.ERRORS.COULD_NOT_RETRIEVE'));
                        errors.catch('', false)(error)
                    });
                };

                controller.timeSlotLimitAllowed = true;

                const checkIfTimeSlotLimitAllowed = async function () {
                    controller.timeSlotLimitAllowed = $scope.data.domain.playerTimeSlotLimits
                    if(controller.timeSlotLimitAllowed) {
                        await userLimitsRest.findTimeSlotLimit($scope.user.guid, $scope.user.domain.name)
                    }
                }
                checkIfTimeSlotLimitAllowed()

                controller.changePassword = function () {
                    var modalInstance = $uibModal.open({
                        animation: true,
                        ariaLabelledBy: 'modal-title',
                        ariaDescribedBy: 'modal-body',
                        templateUrl: 'scripts/directives/player/password/changepassword.html',
                        controller: 'ChangePasswordModal',
                        controllerAs: 'controller',
                        backdrop: 'static',
                        size: 'md',
                        resolve: {
                            user: function () {
                                return $scope.user;
                            },
                            profile: function () {
                                return null;
                            },
                            loadMyFiles: function ($ocLazyLoad) {
                                return $ocLazyLoad.load({
                                    name: 'lithium',
                                    files: ['scripts/directives/player/password/changepassword.js']
                                })
                            }
                        }
                    });

                    modalInstance.result.then(function (user) {
                        $scope.user.passwordUpdated = user.passwordUpdated;
                        $scope.user.passwordUpdatedBy = user.passwordUpdatedBy;
                        notify.success("Password updated successfully");
                    });
                }

                controller.resetPassword = function () {
                    var modalInstance = $uibModal.open({
                        animation: true,
                        ariaLabelledBy: 'modal-title',
                        ariaDescribedBy: 'modal-body',
                        templateUrl: 'scripts/directives/player/password/password-reset.html',
                        controller: 'PasswordResetModal',
                        controllerAs: 'controller',
                        backdrop: 'static',
                        size: 'md',
                        resolve: {
                            user: function () {
                                return $scope.user;
                            },
                            profile: function () {
                                return null;
                            },
                            loadMyFiles: function ($ocLazyLoad) {
                                return $ocLazyLoad.load({
                                    name: 'lithium',
                                    files: ['scripts/directives/player/password/password-reset.js']
                                })
                            }
                        }
                    });

                    modalInstance.result.then(function () {
                        notify.success("Password reset token sent");
                    });
                }

                controller.changeStatus = function() {
                    var modalInstance = $uibModal.open({
                        animation: true,
                        ariaLabelledBy: 'modal-title',
                        ariaDescribedBy: 'modal-body',
                        templateUrl: 'scripts/directives/player/status/changestatus.html',
                        controller: 'ChangeStatusModal',
                        controllerAs: 'vm',
                        backdrop: 'static',
                        size: 'md',
                        resolve: {
                            statuses: function() {
                                return statusRest.findAll().then(function(statuses) {
                                    return statuses.plain();
                                });
                            },
                            excludeStatusReasons: ['$stateParams', function($stateParams) {
                                return restDomain.findCurrentDomainSettings($stateParams.domainName).then(function(response) {
                                    var settings = response.plain();
                                    var domainSettings = {};
                                    for (var i = 0; i < settings.length; i++) {
                                        var dslv = settings[i];
                                        domainSettings[dslv.labelValue.label.name] = dslv.labelValue.value;
                                    }

                                    let excludeStatusReasons = [];

                                    var cruksSelfExclEnabled = (domainSettings['cruksId'] == "show" ? true : false);
                                    if (!cruksSelfExclEnabled) {
                                        excludeStatusReasons.push("CRUKS_SELF_EXCLUSION")
                                    }
                                    excludeStatusReasons.push("GAMSTOP_SELF_EXCLUSION")
                                    return excludeStatusReasons;
                                });
                            }],
                            user: function() {return angular.copy($scope.user);},
                            loadMyFiles: function($ocLazyLoad) {
                                return $ocLazyLoad.load({
                                    name:'lithium',
                                    files: [ 'scripts/directives/player/status/changestatus.js' ]
                                })
                            }
                        }
                    });
                    modalInstance.result.then(function (user) {
                        $scope.user.status = user.status;
                        $scope.user.statusReason = user.statusReason;
                        notify.success("Status updated successfully");
                    });
                };

                controller.changeVerificationStatus = function() {
                    $scope.userCopy = angular.copy($scope.user);
                    var modalInstance = $uibModal.open({
                        animation: true,
                        ariaLabelledBy: 'modal-title',
                        ariaDescribedBy: 'modal-body',
                        templateUrl: 'scripts/directives/player/verificationstatus/changestatus.html',
                        controller: 'ChangeStatusVerificationModal',
                        controllerAs: 'vm',
                        backdrop: 'static',
                        size: 'md',
                        resolve: {
                            user: function() {return $scope.userCopy;},
                            loadMyFiles: function($ocLazyLoad) {
                                return $ocLazyLoad.load({
                                    name:'lithium',
                                    files: [ 'scripts/directives/player/verificationstatus/changestatus.js' ]
                                })
                            }
                        }
                    });
                    modalInstance.result.then(function (user) {
                        $scope.user.verificationStatus = user.verificationStatus;
                        $scope.user.ageVerified = user.ageVerified;
                        $scope.user.addressVerified = user.addressVerified;
                        notify.success("Status updated successfully");
                    });
                };

                controller.changeTag = function() {
                    var modalInstance = $uibModal.open({
                        animation: true,
                        ariaLabelledBy: 'modal-title',
                        ariaDescribedBy: 'modal-body',
                        templateUrl: 'scripts/directives/player/tag/changetag.html',
                        controller: 'ChangeTagModal',
                        controllerAs: 'vm',
                        backdrop: 'static',
                        size: 'md cascading-modal',
                        resolve: {
                            user: function() {return angular.copy($scope.user);},
                            domainName: function() { return $scope.user.domain.name; },
                            loadMyFiles: function($ocLazyLoad) {
                                return $ocLazyLoad.load({
                                    name:'lithium',
                                    files: [ 'scripts/directives/player/tag/changetag.js' ]
                                })
                            }
                        }
                    });
                    modalInstance.result.then(function (user) {
                        $scope.user.userCategories = user.userCategories;
                        notify.success("Tag updated successfully");
                    });
                };

                controller.commentReferenceId = 'last-comment-overlay';
                controller.loadLastComment = function() {
                    bsLoadingOverlayService.start({referenceId:controller.commentReferenceId});
                    $scope.data.comment.restService.lastComment($scope.data.comment.domainName, $scope.data.comment.entityId).then(function(response) {
                        $scope.data.comment.lastComment = response;
                    }).catch(
                        //errors.catch("UI_NETWORK_ADMIN.USER.ERRORS.LASTCOMMENT", false)
                    ).finally(function () {
                        bsLoadingOverlayService.stop({referenceId:controller.commentReferenceId});
                    });
                };

                controller.addComment = function() {
                    var modalInstance = $uibModal.open({
                        animation: true,
                        ariaLabelledBy: 'modal-title',
                        ariaDescribedBy: 'modal-body',
                        templateUrl: 'scripts/directives/comment/addcomment.html',
                        controller: 'AddCommentModal',
                        controllerAs: 'controller',
                        backdrop: 'static',
                        size: 'md',
                        resolve: {
                            domainName: function () {
                                return $scope.user.domain.name;
                            },
                            entityId: function () {
                                return $scope.user.id;
                            },
                            restService: function () {
                                return UserRest;
                            },
                            loadMyFiles: function ($ocLazyLoad) {
                                return $ocLazyLoad.load({
                                    name: 'lithium',
                                    files: ['scripts/directives/comment/addcomment.js']
                                })
                            }
                        }
                    });
                    modalInstance.result.then(function(response) {
                        $scope.data.changelogs.reload += 1;
                        controller.loadLastComment();
                        notify.success("Comment added successfully");
                    });
                };

                controller.toggleMobileValidation = function () {
                    $scope.referenceId = 'personal-overlay';
                    bsLoadingOverlayService.start({referenceId: $scope.referenceId});
                    UserRest.toggleMobileValidation($scope.user.domain.name, $scope.user.id).then(function (response) {
                        $scope.user.cellphoneValidated = response.cellphoneValidated;
                        notify.success($scope.user.cellphoneValidated === true ? "UI_NETWORK_ADMIN.USER.MOBILE.VALIDATION.SUCCESS" : "UI_NETWORK_ADMIN.USER.MOBILE.INVALIDATION.SUCCESS");
                    }).catch(
                        errors.catch("UI_NETWORK_ADMIN.USER.ERRORS.VALIDATE_MOBILE", false)
                    ).finally(function () {
                        bsLoadingOverlayService.stop({referenceId: $scope.referenceId});
                    });
                }

                controller.toggleAgeVerification = function () {
                    $scope.referenceId = 'personal-overlay';
                    bsLoadingOverlayService.start({referenceId: $scope.referenceId});
                    UserRest.toggleAgeVerification($scope.user.domain.name, $scope.user.id).then(function (response) {
                        $scope.user.ageVerified = response.ageVerified;
                        $scope.user.verificationStatus = response.verificationStatus;
                        notify.success($scope.user.ageVerified === true ? "UI_NETWORK_ADMIN.USER.AGE_VERIFIED.VALIDATION.SUCCESS" : "UI_NETWORK_ADMIN.USER.AGE_VERIFIED.INVALIDATION.SUCCESS");
                    }).catch(
                        errors.catch("UI_NETWORK_ADMIN.USER.ERRORS.AGE_VERIFIED", false)
                    ).finally(function () {
                        bsLoadingOverlayService.stop({referenceId: $scope.referenceId});
                    });
                }

                controller.toggleAddressVerification = function () {
                    $scope.referenceId = 'personal-overlay';
                    bsLoadingOverlayService.start({referenceId: $scope.referenceId});
                    UserRest.toggleAddressVerification($scope.user.domain.name, $scope.user.id).then(function (response) {
                        $scope.user.addressVerified = response.addressVerified;
                        $scope.user.verificationStatus = response.verificationStatus;
                        notify.success($scope.user.addressVerified === true ? "UI_NETWORK_ADMIN.USER.ADDRESS_VERIFIED.VALIDATION.SUCCESS" : "UI_NETWORK_ADMIN.USER.ADDRESS_VERIFIED.INVALIDATION.SUCCESS");
                    }).catch(
                        errors.catch("UI_NETWORK_ADMIN.USER.ERRORS.ADDRESS_VERIFIED", false)
                    ).finally(function () {
                        bsLoadingOverlayService.stop({referenceId: $scope.referenceId});
                    });
                }

                controller.toggleEmailValidation = function () {
                    $scope.referenceId = 'personal-overlay';
                    bsLoadingOverlayService.start({referenceId: $scope.referenceId});
                    UserRest.toggleEmailValidation($scope.user.domain.name, $scope.user.id).then(function (response) {
                        $scope.user.emailValidated = response.emailValidated;
                        notify.success($scope.user.emailValidated === true ? "UI_NETWORK_ADMIN.USER.EMAIL.VALIDATION.SUCCESS" : "UI_NETWORK_ADMIN.USER.EMAIL.INVALIDATION.SUCCESS");
                    }).catch(
                        errors.catch("UI_NETWORK_ADMIN.USER.ERRORS.VALIDATE_EMAIL", false)
                    ).finally(function () {
                        bsLoadingOverlayService.stop({referenceId: $scope.referenceId});
                    });
                };

                controller.toggleSowValidation = function() {
                    $scope.referenceId = 'personal-overlay';
                    bsLoadingOverlayService.start({referenceId:$scope.referenceId});
                    UserRest.toggleSowValidation($scope.user.domain.name, $scope.user.id).then(function(response) {
                        $scope.user.requireSowDocument = response.requireSowDocument;
                        notify.success($scope.user.requireSowDocument === true ? "UI_NETWORK_ADMIN.PLAYER.MESSAGE.SUCCESS_ENABLE" : "UI_NETWORK_ADMIN.PLAYER.MESSAGE.SUCCESS_DISABLE");
                    }).catch(
                        errors.catch("UI_NETWORK_ADMIN.PLAYER.MESSAGE.ERROR", false)
                    ).finally(function () {
                        bsLoadingOverlayService.stop({referenceId:$scope.referenceId});
                    });
                };

                controller.changePersonalInfo = function() {
                    var modalInstance = $uibModal.open({
                        animation: true,
                        ariaLabelledBy: 'modal-title',
                        ariaDescribedBy: 'modal-body',
                        templateUrl: 'scripts/directives/player/personal/changepersonal.html',
                        controller: 'ChangePersonalModal',
                        controllerAs: 'controller',
                        backdrop: 'static',
                        size: 'md',
                        resolve: {
                            user: function() {return angular.copy($scope.user);},
                            domainSettings: ['rest-domain', '$stateParams', '$security', function (domainRest, $stateParams, $security) {
                                return domainRest.findCurrentDomainSettings($stateParams.domainName).then(function(response) {
                                    var settings = response.plain();
                                    var objSettings = {};
                                    var viewCountry = $security.domainsWithRole("IBAN_VIEW").length > 0;
                                    for (var i = 0; i < settings.length; i++) {
                                        var dslv = settings[i];
                                        if (dslv.labelValue.label.name == 'iban') {
                                            if (viewCountry) {
                                                objSettings[dslv.labelValue.label.name] = dslv.labelValue.value;
                                            }
                                        } else {
                                            objSettings[dslv.labelValue.label.name] = dslv.labelValue.value;
                                        }
                                    }
                                    return objSettings;
                                });
                            }],
                            profile: function() {return null;},
                            loadMyFiles: function($ocLazyLoad) {
                                return $ocLazyLoad.load({
                                    name:'lithium',
                                    files: [ 'scripts/directives/player/personal/changepersonal.js' ]
                                })
                            }
                        }
                    });

                    modalInstance.result.then(function (user) {
                        angular.copy(user, $scope.user);
                        notify.success("Personal details updated successfully");
                    });
                };

                controller.changeDateOfBirth = function() {
                    var modalInstance = $uibModal.open({
                        animation: true,
                        ariaLabelledBy: 'modal-title',
                        ariaDescribedBy: 'modal-body',
                        templateUrl: 'scripts/directives/player/personal/changeDateOfBirth.html',
                        controller: 'ChangeDoBModal',
                        controllerAs: 'controller',
                        backdrop: 'static',
                        size: 'md',
                        resolve: {
                            type: function () {
                                return $scope.data.type;
                            },
                            user: function () {
                                return angular.copy($scope.user);
                            },
                            profile: function () {
                                return $scope.data.profile;
                            },
                            domainSettings: function () {
                                return $scope.data.domainSettings
                            },
                            loadMyFiles: function ($ocLazyLoad) {
                                return $ocLazyLoad.load({
                                    name: 'lithium',
                                    files: ['scripts/directives/player/personal/changeDateOfBirth.js']
                                })
                            }
                        }
                    });

                    modalInstance.result.then(function (user) {
                        angular.copy(user, $scope.user);
                        notify.success("Date Of Birth updated successfully");
                    });
                };

                controller.registerBonus = function() {
                    var modalInstance = $uibModal.open({
                        animation: true,
                        ariaLabelledBy: 'modal-title',
                        ariaDescribedBy: 'modal-body',
                        templateUrl: 'scripts/directives/player/bonus/registerbonus.html',
                        controller: 'RegisterBonusModal',
                        controllerAs: 'controller',
                        backdrop: 'static',
                        size: 'md',
                        resolve: {
                            user: function() {
                                return $scope.user;
                            },
                            loadMyFiles: function($ocLazyLoad) {
                                return $ocLazyLoad.load({
                                    name:'lithium',
                                    files: [ 'scripts/directives/player/bonus/registerbonus.js' ]
                                })
                            }
                        }
                    });
                };

                controller.changeLossLimitVisibility = function() {
                    var modalInstance = $uibModal.open({
                        animation: true,
                        ariaLabelledBy: 'modal-title',
                        ariaDescribedBy: 'modal-body',
                        backdrop: 'static',
                        templateUrl: 'scripts/directives/player/limits/change-losslimit-visibility.html',
                        controller: 'LossLimitVisibilityModal',
                        controllerAs: 'controller',
                        size: 'md',
                        resolve: {
                            user: function() {
                                return $scope.user;
                            },
                            visibility: function() {
                                return $scope.lossLimitVisibility;
                            },
                            loadMyFiles: function($ocLazyLoad) {
                                return $ocLazyLoad.load({
                                    name: 'lithium',
                                    files: [ 'scripts/directives/player/limits/change-losslimit-visibility.js' ]
                                })
                            }
                        }
                    });

                    modalInstance.result.then(function (result) {
                        if (result) {
                            $scope.lossLimitVisibility = result.lossLimitsVisibility;
                            $scope.user.lossLimitVisibility = result.lossLimitsVisibility;
                        }
                    });
                };

                $scope.getLossLimitVisibility = function() {
                    userLimitsRest.getLossLimitVisibility($scope.user.domain.name, $scope.user.guid).then(function(response) {
                        $scope.lossLimitVisibility = response.lossLimitsVisibility;
                        $scope.user.lossLimitVisibility = response.lossLimitsVisibility;
                    }).catch(function() {
                        errors.catch('', false);
                    });
                }

                controller.grantBonus = function() {
                    var modalInstance = $uibModal.open({
                        animation: true,
                        ariaLabelledBy: 'modal-title',
                        ariaDescribedBy: 'modal-body',
                        templateUrl: 'scripts/directives/player/bonus/grantbonus.html',
                        controller: 'GrantBonusModal',
                        controllerAs: 'controller',
                        backdrop: 'static',
                        size: 'md',
                        resolve: {
                            symbol: function(){ return $scope.data.playerBalance[0].currency.symbol },
                            user: function () { return $scope.user; },
                            loadMyFiles: function ($ocLazyLoad) {
                                return $ocLazyLoad.load({
                                    name: 'lithium',
                                    files: ['scripts/directives/player/bonus/grantbonus.js']
                                })
                            },
                            bonusCodes: ['rest-casino', 'errors', function(casinoRest, errors) {
                                return casinoRest.getActiveCashBonusTypes($scope.user.domain.name).then(function(response) {
                                    var bonusCodes = (response != undefined && response != null) ? response.plain() : [];
                                    // Build the bonus codes into key value pairs so it is usable by formly
                                    let obj = new Array();
                                    for (let i = 0; i < bonusCodes.length; i++) {
                                        let name = bonusCodes[i].bonusName + ' (' + bonusCodes[i].bonusCode + ')';
                                        obj[i] = { name: name, value: bonusCodes[i].bonusCode}
                                    }
                                    return obj;
                                }).catch(function(error) {
                                    errors.catch("", false)(error)
                                });
                            }]
                        }
                    });
                    modalInstance.result.then(function (response) {
                        notify.success("GLOBAL.RESPONSE.FORM_SUCCESS");
                        restAcc.balance(
                            $scope.data.playerBalance[0].accountType,
                            $scope.data.playerBalance[0].accountType,
                            $scope.data.playerBalance[0].currency.code,
                            $scope.user.domain.name,
                            $scope.user.guid
                        ).then(function(response) {
                            console.log($scope.data, response);
                            if (angular.isDefined(response)) {
                                $scope.data.playerBalance[0].balance = response;
                            } else {
                                $scope.data.playerBalance[0].balance = 0;
                            }
                            $scope.data.changelogs.reload += 1;
                        });
                    });
                };

                controller.toggleAutoWithdrawalAllowed = function() {
                    $scope.referenceId = 'personal-overlay';
                    bsLoadingOverlayService.start({referenceId:$scope.referenceId});
                    UserRest.toggleAutoWithdrawalAllowed($scope.user.domain.name, $scope.user.id).then(function(response) {
                        $scope.user.autoWithdrawalAllowed = response.autoWithdrawalAllowed;
                        notify.success($scope.user.autoWithdrawalAllowed === true? "Successfully allowed auto withdrawals for player" : "Successfully disallowed auto withdrawals for player");
                    }).catch(
                        errors.catch("", false)
                    ).finally(function () {
                        bsLoadingOverlayService.stop({referenceId:$scope.referenceId});
                    });
                };

                controller.toggleTest = function() {
                    $scope.referenceId = 'personal-overlay';
                    bsLoadingOverlayService.start({referenceId:$scope.referenceId});
                    UserRest.setTest($scope.user.domain.name, $scope.user.id, !($scope.user.testAccount === true)).then(function(response) {
                        $scope.user.testAccount = response.testAccount;
                        $scope.data.changelogs.reload += 1;
                        notify.success($scope.user.testAccount === true ? "UI_NETWORK_ADMIN.PLAYER.TEST.MARK.SUCCESS" : "UI_NETWORK_ADMIN.PLAYER.TEST.UNMARK.SUCCESS");
                    }).catch(
                        errors.catch($scope.user.testAccount === true ? "UI_NETWORK_ADMIN.PLAYER.TEST.UNMARK.ERROR" : "UI_NETWORK_ADMIN.PLAYER.TEST.MARK.ERROR", false)
                    ).finally(function () {
                        bsLoadingOverlayService.stop({referenceId:$scope.referenceId});
                    });
                };


                controller.changeAddress = function(type) {
                    var modalInstance = $uibModal.open({
                        animation: true,
                        ariaLabelledBy: 'modal-title',
                        ariaDescribedBy: 'modal-body',
                        templateUrl: 'scripts/directives/player/address/changeaddress.html',
                        controller: 'ChangeAddressModal',
                        controllerAs: 'controller',
                        backdrop: 'static',
                        size: 'md',
                        resolve: {
                            type: function() {return type;},
                            user: function() {return angular.copy($scope.user);},
                            profile: function() {return null;},
                            loadMyFiles: function($ocLazyLoad) {
                                return $ocLazyLoad.load({
                                    name:'lithium',
                                    files: [ 'scripts/directives/player/address/changeaddress.js' ]
                                })
                            }
                        }
                    });

                    modalInstance.result.then(function (user) {
                        $scope.user[type] = user[type];
                        notify.success("Address details updated successfully");
                    });
                };

                controller.changeIBAN = function() {
                    var modalInstance = $uibModal.open({
                        animation: true,
                        ariaLabelledBy: 'modal-title',
                        ariaDescribedBy: 'modal-body',
                        templateUrl: 'scripts/directives/player/iban/changeIBAN.html',
                        controller: 'ChangeIBANModal',
                        controllerAs: 'controller',
                        backdrop: 'static',
                        size: 'md',
                        resolve: {
                            user: function() {return $scope.user;},
                            domainSettings: $scope.data.domainSettings,
                            loadMyFiles: function($ocLazyLoad) {
                                return $ocLazyLoad.load({
                                    name:'lithium',
                                    files: [ 'scripts/directives/player/iban/changeIBAN.js' ]
                                })
                            }
                        }
                    });

                    modalInstance.result.then(function (result) {
                        $scope.user,
                        $scope.data.comment.changelogs.reload += 1;
                        notify.success("UI_NETWORK_ADMIN.PLAYER.QUICK_ACTIONS.UPDATE.SUCCESS")
                    });
                };

                controller.changePlaceOfBirth = function() {
                    let modalInstance = $uibModal.open({
                        animation: true,
                        ariaLabelledBy: 'modal-title',
                        ariaDescribedBy: 'modal-body',
                        templateUrl: 'scripts/directives/player/personal/changeplaceofbirth.html',
                        controller: 'EditPlaceOfBirthModal',
                        controllerAs: 'controller',
                        backdrop: 'static',
                        size: 'md',
                        resolve: {
                            user: function() {return angular.copy($scope.user);},
                            domainSettings: $scope.data.domainSettings,
                            loadMyFiles: function($ocLazyLoad) {
                                return $ocLazyLoad.load({
                                    name:'lithium',
                                    files: [ 'scripts/directives/player/personal/changeplaceofbirth.js' ]
                                })
                            }
                        }
                    });

                    modalInstance.result.then(function (response) {
                        notify.success('UI_NETWORK_ADMIN.PLAYER.QUICK_ACTIONS.UPDATE.SUCCESS');
                        $scope.user.placeOfBirth = response.placeOfBirth;
                    });
                }

                controller.adjust = function (id) {
                    var balanceModalInstance = $uibModal.open({
                        animation: true,
                        ariaLabelledBy: 'modal-title',
                        ariaDescribedBy: 'modal-body',
                        templateUrl: 'scripts/controllers/dashboard/players/player/accounting/adjustments.html',
                        controller: 'BalanceModal',
                        controllerAs: 'controller',
                        backdrop: 'static',
                        size: 'lg',
                        resolve: {
                            domainName: function(){return $scope.user.domain.name},
                            balance: function(){return $scope.data.playerBalance[id].balance},
                            currency: function(){return $scope.data.playerBalance[id].currency.code},
                            symbol: function(){return $scope.data.playerBalance[id].currency.symbol},
                            ownerGuid: function(){return $scope.user.guid},
                            authorGuid: function(){return $rootScope.principal.guid},
                            accountType: function(){return $scope.data.playerBalance[id].accountType},
                            accountCode: function(){return $scope.data.playerBalance[id].accountType}
                        }
                    });
                    balanceModalInstance.result.then(function (response) {
                        notify.success("GLOBAL.RESPONSE.FORM_SUCCESS");
                        restAcc.balance(
                            $scope.data.playerBalance[id].accountType,
                            $scope.data.playerBalance[id].accountType,
                            $scope.data.playerBalance[id].currency.code,
                            $scope.user.domain.name,
                            $scope.user.guid
                        ).then(function(response) {
                            console.log($scope.data, response);
                            if (angular.isDefined(response)) {
                                $scope.data.playerBalance[id].balance = response;
                            } else {
                                $scope.data.playerBalance[id].balance = 0;
                            }
                            $scope.data.changelogs.reload += 1;
                        });
                    });
                };

                controller.direct_withdrawal = function (id) {
                    var directWithdrawalModalInstance = $uibModal.open({
                        animation: true,
                        ariaLabelledBy: 'modal-title',
                        ariaDescribedBy: 'modal-body',
                        templateUrl: 'scripts/controllers/dashboard/players/player/accounting/direct_withdrawal/direct_withdrawal.html',
                        controller: 'DirectWithdrawalModal',
                        controllerAs: 'controller',
                        backdrop: 'static',
                        size: 'lg',
                        resolve: {
                            domain: function(){return $scope.data.domain},
                            domainName: function(){return $scope.user.domain.name},
                            balance: function(){return $scope.data.playerBalance[id].balance},
                            currency: function(){return $scope.data.playerBalance[id].currency.code},
                            symbol: function(){return $scope.data.playerBalance[id].currency.symbol},
                            ownerGuid: function(){return $scope.user.guid},
                            authorGuid: function(){return $rootScope.principal.guid},
                            accountType: function(){return $scope.data.playerBalance[id].accountType},
                            accountCode: function(){return $scope.data.playerBalance[id].accountType}
                        }
                    });
                    directWithdrawalModalInstance.result.then(function (response) {
                        notify.success("GLOBAL.RESPONSE.FORM_SUCCESS");
                        restAcc.balance(
                            $scope.data.playerBalance[id].accountType,
                            $scope.data.playerBalance[id].accountType,
                            $scope.data.playerBalance[id].currency.code,
                            $scope.user.domain.name,
                            $scope.user.guid
                        ).then(function(response) {
                            console.log($scope.data, response);
                            if (angular.isDefined(response)) {
                                $scope.data.playerBalance[id].balance = response;
                            } else {
                                $scope.data.playerBalance[id].balance = 0;
                            }
                            $scope.data.changelogs.reload += 1;
                        });
                    });
                };

                controller.writeoff = function (id) {
                    console.log("writeoff for value:", $scope.data.playerBalance[id].balance);
                    restAcc.balanceadjust(
                        $scope.data.playerBalance[id].balance * -1,
                        new Date(),
                        $scope.data.playerBalance[id].accountType,
                        $scope.data.playerBalance[id].accountType,
                        'BALANCE_ADJUST',
                        'PLAYER_BALANCE_WRITEOFF',
                        'MANUAL_BALANCE_ADJUST',
                        'write-off player balance',
                        $scope.data.playerBalance[id].currency.code,
                        $scope.user.domain.name,
                        $scope.user.guid,
                        $rootScope.principal.guid
                    ).then(function(response) {
                        notify.success("GLOBAL.RESPONSE.FORM_SUCCESS");
                        $scope.data.playerBalance[id].balance = 0;
                        $scope.data.changelogs.reload += 1;
                    }).catch(
                        errors.catch("UI_NETWORK_ADMIN.USER.ERRORS.BALANCEUPDATE", false)
                    )
                };

	            controller.withdrawalOnHold = function() {
		            var modalInstance = $uibModal.open({
			            animation: true,
			            ariaLabelledBy: 'modal-title',
			            ariaDescribedBy: 'modal-body',
			            templateUrl: 'scripts/directives/cashier/transactions/onhold/onhold.html',
			            controller: 'OnHoldModal',
			            controllerAs: 'controller',
			            backdrop: 'static',
			            size: 'md',
			            resolve: {
				            ownerGuid: function () {
					            return $scope.user.guid;
				            },
				            loadMyFiles: function ($ocLazyLoad) {
					            return $ocLazyLoad.load({
						            name: 'lithium',
						            files: ['scripts/directives/cashier/transactions/onhold/onhold.js']
					            })
				            }
			            }
		            });
	            };

	            controller.withdrawalReprocess = function() {
		            var modalInstance = $uibModal.open({
			            animation: true,
			            ariaLabelledBy: 'modal-title',
			            ariaDescribedBy: 'modal-body',
			            templateUrl: 'scripts/directives/cashier/transactions/reprocess/reprocess.html',
			            controller: 'ReprocessModal',
			            controllerAs: 'controller',
			            backdrop: 'static',
			            size: 'md',
			            resolve: {
				            ownerGuid: function () {
					            return $scope.user.guid;
				            },
				            loadMyFiles: function ($ocLazyLoad) {
					            return $ocLazyLoad.load({
						            name: 'lithium',
						            files: ['scripts/directives/cashier/transactions/reprocess/reprocess.js']
					            })
				            }
			            }
		            });
	            };

	            //Time Frame - Start
                controller.changeTimeFrame = function() {
                    $uibModal.open({
                        animation: true,
                        ariaLabelledBy: 'modal-title',
                        ariaDescribedBy: 'modal-body',
                        templateUrl: 'scripts/directives/player/timeframe/changetimeframe.html',
                        controller: 'ChangeTimeFrameModal',
                        controllerAs: 'controller',
                        backdrop: 'static',
                        size: 'md',
                        resolve: {
                            user: function() {
                                return $scope.user;
                            },
                            loadMyFiles: function($ocLazyLoad) {
                                return $ocLazyLoad.load({
                                    name:'lithium',
                                    files: [ 'scripts/directives/player/timeframe/changetimeframe.js' ]
                                })
                            },
                        }
                    });
                };
                //Time Frame - End

                controller.changeBalanceLimit = function() {
                    var modalInstance = $uibModal.open({
                        animation: true,
                        ariaLabelledBy: 'modal-title',
                        ariaDescribedBy: 'modal-body',
                        templateUrl: 'scripts/directives/player/balance-limit/change-limit.html',
                        controller: 'ChangeBalanceLimitModal',
                        controllerAs: 'controller',
                        backdrop: 'static',
                        size: 'md',
                        resolve: {
                            user: function() {
                                return $scope.user;
                            },
                            domain: function () {
                                return $scope.data.domain;
                            },
                            newLimit: function() {
                                return ($scope.data.balanceLimits.current === null) ? undefined : $scope.data.balanceLimits.current.amount / 100;
                            },
                            currentLimit: function() {
                                return ($scope.data.balanceLimits.current === null) ? undefined : $scope.data.balanceLimits.current.amount / 100;
                            },
                            loadMyFiles: function($ocLazyLoad) {
                                return $ocLazyLoad.load({
                                    name: 'lithium',
                                    files: [ 'scripts/directives/player/balance-limit/change-limit.js' ]
                                })
                            }
                        }
                    });

                    modalInstance.result.then(function(result) {
                        if (result) {
                            console.log("Changed balance limit to : ", result);
                            $scope.data.balanceLimits = result;
                            $scope.data.comment.changelogs.reload += 1;
                        }
                    });
                };

                controller.changeLimits = function(granularity) {
                    var modalInstance = $uibModal.open({
                        animation: true,
                        ariaLabelledBy: 'modal-title',
                        ariaDescribedBy: 'modal-body',
                        templateUrl: 'scripts/directives/player/limits/changelimits.html',
                        controller: 'ChangeLimitsModal',
                        controllerAs: 'controller',
                        backdrop: 'static',
                        size: 'md',
                        resolve: {
                            user: function() {
                                return $scope.user;
                            },
                            dailyLossLimit: function() {
                                if (granularity === userLimitsRest.GRANULARITY_DAY) {
                                    return $scope.data.limits.dailyLossLimit;
                                } else {
                                    return null;
                                }
                            },
                            weeklyLossLimit: function() {
                                if (granularity === userLimitsRest.GRANULARITY_WEEK) {
                                    return $scope.data.limits.weeklyLossLimit;
                                } else {
                                    return null;
                                }
                            },
                            monthlyLossLimit: function() {
                                if (granularity === userLimitsRest.GRANULARITY_MONTH) {
                                    return $scope.data.limits.monthlyLossLimit;
                                } else {
                                    return null;
                                }
                            },
                            granularity: function() { return granularity; },
                            loadMyFiles: function($ocLazyLoad) {
                                return $ocLazyLoad.load({
                                    name:'lithium',
                                    files: [ 'scripts/directives/player/limits/changelimits.js' ]
                                })
                            }
                        }
                    });

                    modalInstance.result.then(function (result) {
                        if (result) {
                            controller.loadCurrentLosses();
                            if (granularity === userLimitsRest.GRANULARITY_DAY) {
                                $scope.data.limits.dailyLossLimit = result.amount / 100;
                            } else if (granularity === userLimitsRest.GRANULARITY_WEEK) {
                                $scope.data.limits.weeklyLossLimit = result.amount / 100;
                            } else if (granularity === userLimitsRest.GRANULARITY_MONTH) {
                                $scope.data.limits.monthlyLossLimit = result.amount / 100;
                            }
                            $scope.data.comment.changelogs.reload += 1;
                        }
                    });
                };

                controller.changeDepositLimits = function(granularity) {
                    var modalInstance = $uibModal.open({
                        animation: true,
                        ariaLabelledBy: 'modal-title',
                        ariaDescribedBy: 'modal-body',
                        templateUrl: 'scripts/directives/player/depositlimits/changelimits.html',
                        controller: 'ChangeDepositLimitsModal',
                        controllerAs: 'controller',
                        backdrop: 'static',
                        size: 'md',
                        resolve: {
                            user: function() {
                                return $scope.user;
                            },
                            limit: function() {
                                switch (granularity) {
                                    case userLimitsRest.GRANULARITY_DAY:
                                        return $scope.data.depositLimits.dailyLimit;
                                    case userLimitsRest.GRANULARITY_WEEK:
                                        return $scope.data.depositLimits.weeklyLimit;
                                    case userLimitsRest.GRANULARITY_MONTH:
                                        return $scope.data.depositLimits.monthlyLimit;
                                }
                            },
                            limitUsed: function() {
                                switch (granularity) {
                                    case userLimitsRest.GRANULARITY_DAY:
                                        return $scope.data.depositLimits.dailyLimitUsed;
                                    case userLimitsRest.GRANULARITY_WEEK:
                                        return $scope.data.depositLimits.weeklyLimitUsed;
                                    case userLimitsRest.GRANULARITY_MONTH:
                                        return $scope.data.depositLimits.monthlyLimitUsed;
                                }
                            },
                            granularity: function() { return granularity; },
                            loadMyFiles: function($ocLazyLoad) {
                                return $ocLazyLoad.load({
                                    name: 'lithium',
                                    files: [ 'scripts/directives/player/depositlimits/changelimits.js' ]
                                })
                            }
                        }
                    });
                    
                    modalInstance.result.then(function(result) {
                        if (result) {
                            controller.getCurrentDepositLimits();
                            var user = angular.copy($scope.user);
                            user.forceChangelogReload = (user.forceChangelogReload != undefined)? user.forceChangelogReload + 1: 1;
                            $scope.user = user;
                        }
                    });
                };

                controller.removeDepositLimit = function(granularity) {
                    userLimitsRest.removePlayerLimit($scope.user.guid, $scope.user.id, $scope.user.domain.name, granularity, userLimitsRest.DEPOSIT_LIMIT).then(function(response) {
                        if (granularity === userLimitsRest.GRANULARITY_DAY) {
                            $scope.data.depositLimits.dailyLimit = undefined;
                            $scope.data.depositLimits.dailyLimitUsed = undefined;
                        } else if (granularity === userLimitsRest.GRANULARITY_WEEK) {
                            $scope.data.depositLimits.weeklyLimit = undefined;
                            $scope.data.depositLimits.weeklyLimitUsed = undefined;
                        } else if (granularity === userLimitsRest.GRANULARITY_MONTH) {
                            $scope.data.depositLimits.monthlyLimit = undefined;
                            $scope.data.depositLimits.monthlyLimitUsed = undefined;
                        }
                        $scope.data.comment.changelogs.reload += 1;
                        notify.success('UI_NETWORK_ADMIN.LIMITS.PLAYER.REMOVED');
                    });
                }

                controller.getCurrentDepositLimits = function() {
                    bsLoadingOverlayService.start({referenceId:$scope.referenceId});
                    // restDomain.findByName($scope.user.domain.name).then(function(domain) {
                        if (!$scope.symbol) {
                            $scope.symbol = $scope.data.domain.currencySymbol+' ';
                        }
                    // }).catch(
                    //     errors.catch("UI_NETWORK_ADMIN.DEPOSITLIMITS.ERRORS.GETLIMITS", false)
                    // );

                    $scope.disabledMessage = undefined;

                    $scope.data.depositLimits.dailyLimit = undefined;
                    $scope.data.depositLimits.dailyLimitUsed = undefined;
                    $scope.data.depositLimits.weeklyLimit = undefined;
                    $scope.data.depositLimits.weeklyLimitUsed = undefined;
                    $scope.data.depositLimits.monthlyLimit = undefined;
                    $scope.data.depositLimits.monthlyLimitUsed = undefined;

                    $scope.dailyLimitPending = undefined;
                    $scope.dailyLimitPendingCreated = undefined;
                    $scope.weeklyLimitPending = undefined;
                    $scope.weeklyLimitPendingCreated = undefined;
                    $scope.monthlyLimitPending = undefined;
                    $scope.monthlyLimitPendingCreated = undefined;

                    $scope.dailyLimitSupposed = undefined;
                    $scope.dailyLimitSupposedCreated = undefined;
                    $scope.weeklyLimitSupposed = undefined;
                    $scope.weeklyLimitSupposedCreated = undefined;
                    $scope.monthlyLimitSupposed = undefined;
                    $scope.monthlyLimitSupposedCreated = undefined;

                    userLimitsRest.depositLimitsPending($scope.user.guid).then(function(response) {
                        if (!response._successful && (response._status === 481)) {
                            $scope.disabledMessage = response._message;
                        }
                        angular.forEach(response.plain(), function(v,k) {
                            if (v.granularity === userLimitsRest.GRANULARITY_DAY) {
                                $scope.dailyLimitPending = v.amount / 100;
                                $scope.dailyLimitPendingCreated = v.createdDate;
                            } else if (v.granularity === userLimitsRest.GRANULARITY_WEEK) {
                                $scope.weeklyLimitPending = v.amount / 100;
                                $scope.weeklyLimitPendingCreated = v.createdDate;
                            } else if (v.granularity === userLimitsRest.GRANULARITY_MONTH) {
                                $scope.monthlyLimitPending = v.amount / 100;
                                $scope.monthlyLimitPendingCreated = v.createdDate;
                            }
                        });
                    }).catch(
                        errors.catch("UI_NETWORK_ADMIN.DEPOSITLIMITS.ERRORS.GETLIMITS", false)
                    );
                    userLimitsRest.depositLimitsSupposed($scope.user.guid).then(function(response) {
                        if (!response._successful && (response._status === 481)) {
                            $scope.disabledMessage = response._message;
                        }
                        angular.forEach(response.plain(), function(v,k) {
                            if (v.granularity === userLimitsRest.GRANULARITY_DAY) {
                                $scope.dailyLimitSupposed = v.amount / 100;
                                $scope.dailyLimitSupposedCreated = v.createdDate;
                            } else if (v.granularity === userLimitsRest.GRANULARITY_WEEK) {
                                $scope.weeklyLimitSupposed = v.amount / 100;
                                $scope.weeklyLimitSupposedCreated = v.createdDate;
                            } else if (v.granularity === userLimitsRest.GRANULARITY_MONTH) {
                                $scope.monthlyLimitSupposed = v.amount / 100;
                                $scope.monthlyLimitSupposedCreated = v.createdDate;
                            }
                        });
                    }).catch(
                        errors.catch("UI_NETWORK_ADMIN.DEPOSITLIMITS.ERRORS.GETLIMITS", false)
                    );
                    userLimitsRest.depositLimits($scope.user.guid).then(function(response) {
                        if (!response._successful && (response._status === 481)) {
                            $scope.disabledMessage = response._message;
                        }
                        angular.forEach(response.plain(), function(v,k) {
                            if (v.granularity === userLimitsRest.GRANULARITY_DAY && v.id !== null) {
                                $scope.data.depositLimits.dailyLimit = v.amount / 100;
                                $scope.data.depositLimits.dailyLimitUsed = v.amountUsed / 100;
                            } else if (v.granularity === userLimitsRest.GRANULARITY_WEEK && v.id !== null) {
                                $scope.data.depositLimits.weeklyLimit = v.amount / 100;
                                $scope.data.depositLimits.weeklyLimitUsed = v.amountUsed / 100;
                            } else if (v.granularity === userLimitsRest.GRANULARITY_MONTH && v.id !== null) {
                                $scope.data.depositLimits.monthlyLimit = v.amount / 100;
                                $scope.data.depositLimits.monthlyLimitUsed = v.amountUsed / 100;
                            }
                        });
                    }).catch(
                        errors.catch("UI_NETWORK_ADMIN.DEPOSITLIMITS.ERRORS.GETLIMITS", false)
                    ).finally(function () {
                        bsLoadingOverlayService.stop({referenceId:$scope.referenceId});
                    });
                }

                controller.removeLimit = function(granularity) {
                    userLimitsRest.removePlayerLimit($scope.user.guid, $scope.user.id, $scope.user.domain.name, granularity, userLimitsRest.LIMIT_TYPE_LOSS).then(function(response) {
                        if (response === true) {
                            if (granularity === userLimitsRest.GRANULARITY_DAY) {
                                $scope.data.limits.dailyLossLimit = null;
                            } else if (granularity === userLimitsRest.GRANULARITY_WEEK) {
                                $scope.data.limits.weekLossLimit = null;
                            } else if (granularity === userLimitsRest.GRANULARITY_MONTH) {
                                $scope.data.limits.monthlyLossLimit = null;
                            }
                            $scope.data.comment.changelogs.reload += 1;
                            notify.success('UI_NETWORK_ADMIN.LIMITS.PLAYER.REMOVED');
                        }
                    }).catch(function() {
                        errors.catch('', false);
                    });
                }

                controller.arePlayTimeLimitSettingsActive = function () {
                    return $scope.data.domain.playtimeLimit != null ? $scope.data.domain.playtimeLimit : false;
                }
                controller.ptlSettingsActive = controller.arePlayTimeLimitSettingsActive();

                if (controller.ptlSettingsActive) {
                    controller.ptlUserData = {};
                    controller.ptlUserData.currentConfigRevision = undefined;
                    controller.ptlUserData.currentPtlDays = undefined;
                    controller.ptlUserData.currentPtlHours = undefined;
                    controller.ptlUserData.currentPtlMinutes = undefined;
                    controller.ptlUserData.pendingConfigRevision = undefined;

                    controller.editPlayTimeLimits = function () {
                        controller.updatePtlInfo();
                        var modalInstance = $uibModal.open({
                            animation: true,
                            ariaLabelledBy: 'modal-title',
                            ariaDescribedBy: 'modal-body',
                            templateUrl: 'scripts/directives/player/play-time-limits/edit-play-time-limits.html',
                            controller: 'EditPlayTimeLimitsModal',
                            controllerAs: 'controller',
                            backdrop: 'static',
                            size: 'md',
                            resolve: {
                                playTimeLimitUserData: function () {
                                    return controller.ptlUserData;
                                },
                                user: function () {
                                    return $scope.user;
                                },
                                loadMyFiles: function ($ocLazyLoad) {
                                    return $ocLazyLoad.load({
                                        name: 'lithium',
                                        files: ['scripts/directives/player/play-time-limits/edit-play-time-limits.js']
                                    })
                                }
                            }
                        });

                        modalInstance.result.then(function (result) {
                            if (result) {
                                notify.success("GLOBAL.RESPONSE.SUCCESS_FORMS.ADDED_NEW_PLAY_TIME_LIMIT");
                                controller.updatePtlInfo();
                            }
                        });
                    };

                    controller.updatePtlInfo = function () {
                        controller.arePlayTimeLimitSettingsActive();
                        controller.getPlayerPlayTimeLimits();
                        bsLoadingOverlayService.stop({referenceId: controller.referenceId});
                    }

                    controller.getPlayerPlayTimeLimits = function () {
                        UserRest.getPlayerPlayTimeLimitConfigHttp($scope.user.guid, $scope.user.domain.name).then(
                            function (response) {
                                if (response !== undefined && response.data !== '' && response.status === 200) {
                                    controller.ptlUserData = response.data;
                                    controller.initCurrentPtlDaysHoursAndMinutes();
                                    if (response.data.pendingConfigRevision === null) controller.ptlUserData.pendingConfigRevision = undefined;

                                    UserRest.updateAndGetPlayerEntryHttp($scope.user.guid).then(
                                        function (result) {
                                            if (result.status === 200) {
                                                controller.ptlUserData.secondsAccumulated = result.data.secondsAccumulated;
                                            }
                                        });
                                } else {
                                    controller.ptlUserData.currentConfigRevision = undefined;
                                    controller.ptlUserData.pendingConfigRevision = undefined;
                                    controller.ptlUserData.currentPtlDays = undefined;
                                    controller.ptlUserData.currentPtlHours = undefined;
                                    controller.ptlUserData.currentPtlMinutes = undefined;
                                    errors.catch(
                                        'UI_NETWORK_ADMIN.PLAY_TIME_LIMITS.ERRORS.RETRIEVE_LIMIT',
                                        false);
                                }
                            });
                    };

                    controller.initCurrentPtlDaysHoursAndMinutes = function () {
                        let diff = new moment.duration(controller.ptlUserData.currentConfigRevision.secondsAllocated, 'seconds');
                        controller.ptlUserData.currentPtlDays = diff.days();
                        controller.ptlUserData.currentPtlHours = diff.hours();
                        controller.ptlUserData.currentPtlMinutes = diff.minutes();
                    }
                    controller.getPlayerPlayTimeLimits();
                }

                controller.loadCurrentLosses = function() {
                    userLimitsRest.findNetLossToHouse($scope.user.domain.name, $scope.user.guid, $scope.data.domain.currency, userLimitsRest.GRANULARITY_DAY).then(function(response) {
                        $scope.data.limits.dailyNetLossToHouse = (response != undefined)? response / 100: null;
                    }).catch(function() {
                        errors.catch('', false);
                    });

                    userLimitsRest.findNetLossToHouse($scope.user.domain.name, $scope.user.guid, $scope.data.domain.currency, userLimitsRest.GRANULARITY_WEEK).then(function(response) {
                        $scope.data.limits.weeklyNetLossToHouse = (response != undefined)? response / 100: null;
                    }).catch(function() {
                        errors.catch('', false);
                    });

                    userLimitsRest.findNetLossToHouse($scope.user.domain.name, $scope.user.guid, $scope.data.domain.currency, userLimitsRest.GRANULARITY_MONTH).then(function(response) {
                        $scope.data.limits.monthlyNetLossToHouse = (response != undefined)? response / 100: null;
                    }).catch(function() {
                        errors.catch('', false);
                    });
                }

                controller.cooloffPlayer = function() {
                    var modalInstance = $uibModal.open({
                        animation: true,
                        ariaLabelledBy: 'modal-title',
                        ariaDescribedBy: 'modal-body',
                        templateUrl: 'scripts/directives/player/cooloff/cooloff-player.html',
                        controller: 'CooloffPlayerModal',
                        controllerAs: 'controller',
                        backdrop: 'static',
                        size: 'md',
                        resolve: {
                            user: function() {
                                return $scope.user;
                            },
                            loadMyFiles: function($ocLazyLoad) {
                                return $ocLazyLoad.load({
                                    name:'lithium',
                                    files: [ 'scripts/directives/player/cooloff/cooloff-player.js' ]
                                })
                            }
                        }
                    });

                    modalInstance.result.then(function (result) {
                        $scope.data.coolOff = result;
                    });
                }

                controller.setRealityCheckTime = function() {
                    var modalInstance = $uibModal.open({
                        animation: true,
                        ariaLabelledBy: 'modal-title',
                        ariaDescribedBy: 'modal-body',
                        templateUrl: 'scripts/directives/player/reality-check/reality-check-player.html',
                        controller: 'RealityCheckPlayerModal',
                        controllerAs: 'controller',
                        backdrop: 'static',
                        size: 'md',
                        resolve: {
                            user: function() {
                                return $scope.user;
                            },
                            loadMyFiles: function($ocLazyLoad) {
                                return $ocLazyLoad.load({
                                    name:'lithium',
                                    files: [ 'scripts/directives/player/reality-check/reality-check-player.js' ]
                                })
                            }
                        }
                    });

                    modalInstance.result.then(function (result) {
                        $scope.data.realityCheck = result;
                    });
                }

                controller.uploadAndSubmitDocument = function() {
                    var modalInstance = $uibModal.open({
                        animation: true,
                        ariaLabelledBy: 'modal-title',
                        ariaDescribedBy: 'modal-body',
                        templateUrl: 'scripts/directives/player/document-verification/upload-and-submit.html',
                        controller: 'DocumentVerificationModal',
                        controllerAs: 'controller',
                        backdrop: 'static',
                        size: 'md',
                        resolve: {
                            user: function() {
                                return $scope.user;
                            },
                            loadMyFiles: function($ocLazyLoad) {
                                return $ocLazyLoad.load({
                                    name:'lithium',
                                    files: [ 'scripts/directives/player/document-verification/upload-and-submit.js' ]
                                })
                            }
                        }
                    });

                    modalInstance.result.then(function (result) {
                        console.log('on documentation verification modal close')
                        // $scope.data.verificationStatus = result;
                    });
                }


                controller.checkBvn = function() {
                    var modalInstance = $uibModal.open({
                        animation: true,
                        ariaLabelledBy: 'modal-title',
                        ariaDescribedBy: 'modal-body',
                        templateUrl: 'scripts/directives/player/bvn/check-bvn.html',
                        controller: 'CheckBvnModal',
                        controllerAs: 'controller',
                        backdrop: 'static',
                        size: 'md',
                        resolve: {
                            user: function() {
                                return $scope.user;
                            },
                            loadMyFiles: function($ocLazyLoad) {
                                return $ocLazyLoad.load({
                                    name:'lithium',
                                    files: [ 'scripts/directives/player/bvn/check-bvn.js' ]
                                })
                            }
                        }
                    });

                    modalInstance.result.then(function (result) {
                        $scope.data.verificationStatus = result;
                    });
                }

	            controller.checkNinPhone = function() {
		            $scope.userCopy = angular.copy($scope.user);
		            var modalInstance = $uibModal.open({
			            animation: true,
			            ariaLabelledBy: 'modal-title',
			            ariaDescribedBy: 'modal-body',
			            templateUrl: 'scripts/directives/player/nin/check-nin-phone.html',
			            controller: 'CheckNinPhoneModal',
			            controllerAs: 'controller',
			            backdrop: 'static',
			            size: 'md',
			            resolve: {
				            user: function() {return $scope.userCopy;},
				            loadMyFiles: function($ocLazyLoad) {
					            return $ocLazyLoad.load({
						            name:'lithium',
						            files: [ 'scripts/directives/player/nin/check-nin-phone.js' ]
					            })
				            }
			            }
		            });

		            modalInstance.result.then(function (response) {
			            $scope.user.verificationStatus = response.verificationStatusId;
		            });
	            }

	            controller.checkByBankAccount = function() {
		            $scope.userCopy = angular.copy($scope.user);
		            var modalInstance = $uibModal.open({
			            animation: true,
			            ariaLabelledBy: 'modal-title',
			            ariaDescribedBy: 'modal-body',
			            templateUrl: 'scripts/directives/player/bank-account/check-by-bank-account.html',
			            controller: 'CheckBankAccountModal',
			            controllerAs: 'controller',
			            backdrop: 'static',
			            size: 'md',
			            resolve: {
			            	banks: function() {
					            return kycVerifyRest.banks($scope.userCopy.guid, "service-kyc-provider-smileidentity").then(function (response) {
						            return response.plain();
					            });
				            },
				            user: function() {return $scope.userCopy;},
				            loadMyFiles: function($ocLazyLoad) {
					            return $ocLazyLoad.load({
						            name:'lithium',
						            files: [ 'scripts/directives/player/bank-account/check-by-bank-account.js' ]
					            })
				            }
			            }
		            });

		            modalInstance.result.then(function (response) {
			            $scope.user.verificationStatus = response.verificationStatusId;
		            });
	            }

                controller.clearPlayerCooloff = function() {
                    restCoolOff.clear($scope.user.guid, $scope.user.domain.name).then(function(response) {
                        if (response === true || response.data === true) {
                            $scope.data.coolOff = undefined;
                            notify.success('UI_NETWORK_ADMIN.PLAYER.COOLOFF.CLEAR.SUCCESS');
                        } else {
                            notify.error('UI_NETWORK_ADMIN.PLAYER.COOLOFF.CLEAR.ERROR');
                        }
                    }).catch(function() {
                        errors.catch('', false);
                    });
                }

                controller.changePromotionalOptout = function() {
                    var modalInstance = $uibModal.open({
                        animation: true,
                        ariaLabelledBy: 'modal-title',
                        ariaDescribedBy: 'modal-body',
                        size: 'md',
                        templateUrl: 'scripts/directives/player/promooptout/changepromooptout.html',
                        controller: 'ChangePromoOptoutModal',
                        controllerAs: 'controller',
                        backdrop: 'static',
                        resolve: {
                            user: function () {
                                return $scope.user;
                            },
                            data: function () {
                                return {
                                    box: "info",
                                    title: "UI_NETWORK_ADMIN.PLAYER.PROMOOPTOUT.UPDATE_MARKETING_PREFERENCES",
                                    domainSettings: $scope.data.domainSettings
                                };
                            },
                            ecosysRest: function() {
                                return EcosysRest;
                            },
                            loadMyFiles: function($ocLazyLoad) {
                                return $ocLazyLoad.load({
                                    name:'lithium',
                                    files: [ 'scripts/directives/player/promooptout/changepromooptout.js' ]
                                })
                            }
                        }
                    });
                }

                controller.documentAdd = function () {
                    var modalInstance = $uibModal.open({
                        animation: true,
                        ariaLabelledBy: 'modal-title',
                        ariaDescribedBy: 'modal-body',
                        templateUrl: 'scripts/controllers/dashboard/players/player/document/add/add.html',
                        controller: 'documentUpload',
                        controllerAs: 'controller',
                        backdrop: 'static',
                        size: 'md',
                        resolve: {
                            domainName: function () {
                                return $scope.user.domain.name;
                            },
                            userguid: function () {
                                return $scope.user.guid;
                            },
                            loadMyFiles:function($ocLazyLoad) {
                                return $ocLazyLoad.load({
                                    name:'lithium',
                                    files: [
                                        'scripts/controllers/dashboard/players/player/document/add/add.js'
                                    ]
                                })
                            }
                        }
                    });

                    modalInstance.result.then(function() {
                        $rootScope.$emit("reloadDocumentList", {});
                    })
                }

                controller.changeSelfExclusion = function() {
                    var modalInstance = $uibModal.open({
                        animation: true,
                        ariaLabelledBy: 'modal-title',
                        ariaDescribedBy: 'modal-body',
                        templateUrl: 'scripts/directives/player/self-exclusion-v2/self-exclude-player.html',
                        controller: 'SelfExcludePlayerModal',
                        controllerAs: 'controller',
                        backdrop: 'static',
                        size: 'md',
                        resolve: {
                            user: function() {
                                return $scope.user;
                            },
                            loadMyFiles: function($ocLazyLoad) {
                                return $ocLazyLoad.load({
                                    name:'lithium',
                                    files: [ 'scripts/directives/player/self-exclusion-v2/self-exclude-player.js' ]
                                })
                            }
                        }
                    });

                    modalInstance.result.then(function (result) {
                        $scope.data.selfExclusion = result;
                    });
                }

                controller.removeSelfExclusion = function() {
                    exclusionRest.clear($scope.user.guid, $scope.user.domain.name).then(function(response) {
                        if (response === true || response.data === true) {
                            $scope.data.selfExclusion = undefined;
                            notify.success('UI_NETWORK_ADMIN.PLAYER.EXCLUSION.CLEAR.SUCCESS');
                        } else {
                            notify.error('UI_NETWORK_ADMIN.PLAYER.EXCLUSION.CLEAR.ERROR');
                        }
                    }).catch(function() {
                        errors.catch('', false);
                    });
                }

                controller.sendNotification = function() {
                    var modalInstance = $uibModal.open({
                        animation: true,
                        ariaLabelledBy: 'modal-title',
                        ariaDescribedBy: 'modal-body',
                        templateUrl: 'scripts/controllers/dashboard/players/player/sendnotification.html',
                        controller: 'SendPlayerNotification',
                        controllerAs: 'controller',
                        size: 'md cascading-modal',
                        backdrop: 'static',
                        resolve: {
                            user: function() {return $scope.user;},
                            loadMyFiles:function($ocLazyLoad) {
                                return $ocLazyLoad.load({
                                    name:'lithium',
                                    files: [
                                        'scripts/controllers/dashboard/players/player/sendnotification.js'
                                    ]
                                })
                            }
                        }
                    });

                    modalInstance.result.then(function(category) {
                    });
                }

                controller.sendSMS = function() {
                    var modalInstance = $uibModal.open({
                        animation: true,
                        ariaLabelledBy: 'modal-title',
                        ariaDescribedBy: 'modal-body',
                        templateUrl: 'scripts/controllers/dashboard/players/player/sendsms.html',
                        controller: 'SendPlayerSMS',
                        controllerAs: 'controller',
                        size: 'md cascading-modal',
                        backdrop: 'static',
                        resolve: {
                            user: function() {return $scope.user;},
                            loadMyFiles:function($ocLazyLoad) {
                                return $ocLazyLoad.load({
                                    name:'lithium',
                                    files: [
                                        'scripts/controllers/dashboard/players/player/sendsms.js'
                                    ]
                                })
                            }
                        }
                    });

                    modalInstance.result.then(function(category) {
                    });
                }

                controller.sendEmail = function() {
                    var modalInstance = $uibModal.open({
                        animation: true,
                        ariaLabelledBy: 'modal-title',
                        ariaDescribedBy: 'modal-body',
                        templateUrl: 'scripts/directives/player/email/sendemail.html',
                        controller: 'SendEmailTemplateModal',
                        controllerAs: 'controller',
                        backdrop: 'static',
                        size: 'md',
                        resolve: {
                            user: function() {
                                return $scope.user;
                            },
                            loadMyFiles: function($ocLazyLoad) {
                                return $ocLazyLoad.load({
                                    name:'lithium',
                                    files: [ 'scripts/directives/player/email/sendemail.js' ]
                                })
                            }
                        }
                    });

                    modalInstance.result.then(function (result) {
                        controller.refresh();
                    });
                }

                controller.addRestriction = function() {
                    var modalInstance = $uibModal.open({
                        animation: true,
                        ariaLabelledBy: 'modal-title',
                        ariaDescribedBy: 'modal-body',
                        templateUrl: 'scripts/directives/player/user-restrictions/set-user-restriction.html',
                        controller: 'SetUserRestrictionModal',
                        controllerAs: 'controller',
                        backdrop: 'static',
                        size: 'md',
                        resolve: {
                            user: function() {
                                return $scope.user;
                            },
                            loadMyFiles: function($ocLazyLoad) {
                                return $ocLazyLoad.load({
                                    name:'lithium',
                                    files: [ 'scripts/directives/player/user-restrictions/set-user-restriction.js' ]
                                })
                            }
                        }
                    });

                    modalInstance.result.then(function (result) {
                        $scope.data.userRestrictions.push(result);
                    });
                }

                controller.liftRestriction = function($index) {
                    var modalInstance = $uibModal.open({
                        animation: true,
                        ariaLabelledBy: 'modal-title',
                        ariaDescribedBy: 'modal-body',
                        templateUrl: 'scripts/directives/player/user-restrictions/lift-user-restriction.html',
                        controller: 'LiftUserRestrictionModal',
                        controllerAs: 'controller',
                        backdrop: 'static',
                        size: 'md',
                        resolve: {
                            user: function() {
                                return $scope.user;
                            },
                            restriction: function () {
                                return $scope.data.userRestrictions[$index];
                            },
                            loadMyFiles: function($ocLazyLoad) {
                                return $ocLazyLoad.load({
                                    name:'lithium',
                                    files: [ 'scripts/directives/player/user-restrictions/lift-user-restriction.js' ]
                                })
                            }
                        }
                    });
                    modalInstance.result.then(function (result) {
                        $scope.data.userRestrictions.splice($index, 1);
                    })
                }

                controller.addNote = function() {
                    var modalInstance = $uibModal.open({
                        animation: true,
                        ariaLabelledBy: 'modal-title',
                        ariaDescribedBy: 'modal-body',
                        templateUrl: 'scripts/directives/changelog/addnote.html',
                        controller: 'AddChangeLogNoteModal',
                        controllerAs: 'controller',
                        backdrop: 'static',
                        size: 'md',
                        resolve: {
                            domainName: function () {
                                return $scope.user.domain.name;
                            },
                            entityId: function () {
                                return $scope.user.id;
                            },
                            restService: function () {
                                return UserRest;
                            },
                            loadMyFiles: function ($ocLazyLoad) {
                                return $ocLazyLoad.load({
                                    name: 'lithium',
                                    files: ['scripts/directives/changelog/addnote.js']
                                })
                            }
                        }
                    });
                    modalInstance.result.then(function(response) {
                        $scope.data.comment.changelogs.reload += 1;
                        controller.loadLastComment();
                        notify.success("UI_NETWORK_ADMIN.PLAYER.NOTE.ADD.SUCCESS");
                    });
                };

	            controller.emptyOutPendingWithdrawBalance = function (updatePlayerBalance) {
		            $scope.referenceId = 'empty-out-pending-withdraw-balance';
		            bsLoadingOverlayService.start({referenceId: $scope.referenceId});
		            devToolRest.emptyOutPendingWithdrawBalance($scope.user.domain.name, $scope.user.guid, updatePlayerBalance).then(function (response) {
			            $scope.data.playerBalance[0].balance = response.playerBalance;
			            $scope.$parent.controller.pendingWithdrawals = response.pendingWithdrawBalance;
			            notify.success(response._message);
		            }).catch(function (error) {
			            notify.error("UI_NETWORK_ADMIN.PLAYER.DEV_TOOLS.FIELDS.ERROR_CLEAR_PENDING_WITHDRAW_BALANCE.LABEL");
			            errors.catch('', false)(error)
		            }).finally(function () {
			            bsLoadingOverlayService.stop({referenceId: $scope.referenceId});
		            });
	            };

	            controller.addPlayerLink = function(isModified) {
                    $scope.playerlink = null;
                    $scope.referenceId = 'playerlink-overlay';
                    var modalInstance = $uibModal.open({
                        animation: true,
                        ariaLabelledBy: 'modal-title',
                        ariaDescribedBy: 'modal-body',
                        templateUrl: 'scripts/directives/player/player-links/adduserlink.html',
                        controller: 'adduserLinkModal',
                        controllerAs: 'controller',
                        backdrop: 'static',
                        size: 'md',
                        resolve: {
                            type: function() {return $scope.data.type;},
                            user: function() {return angular.copy($scope.user);},
                            profile: function() {return $scope.data.profile;},
                            isModify: function() { return isModified;},
		    				updatePlayerLinkData: function() {return null;},
                            loadMyFiles: function($ocLazyLoad) {
                                return $ocLazyLoad.load({
                                    name:'lithium',
                                    files: [ 'scripts/directives/player/player-links/adduserlink.js' ]
                                })
                            }
                        }
                    });

	                modalInstance.result.then(function (result) {
		                $scope.data.comment.changelogs.reload += 1;
		                //Restangular response returns undefined data.
		                //TO DO: Modify Restangular AddResponse function to return expected results App.js.
	                });
                };

                // Vue new menu

                // EXPERIMENTAL FEATURES ENABLED
                controller.experimentalFeatures =  $userService.isExperimentalFeatures()

                $rootScope.provide.quickActionProvider['menuItems'] = [
                    {
                        label: "Account",
                        items: [
                            {
                                label: $translate.instant('UI_NETWORK_ADMIN.PLAYER.QUICK_ACTIONS.MODIFY.STATUS'),
                                method: () => controller.changeStatus(),
                                visible: true,
                                permission: 'PLAYER_STATUS',
                                domainName: $scope.user.domain.name

                            },
                            {
                                label: $translate.instant('UI_NETWORK_ADMIN.PLAYER.QUICK_ACTIONS.MODIFY.VERIFICATION_STATUS'),
                                method: () => controller.changeVerificationStatus(),
                                 visible: true,
                                 permission: 'PLAYER_VERIFICATION_STATUS',
                                 domainName: $scope.user.domain.name

                            },
                            {
                                label: $translate.instant('UI_NETWORK_ADMIN.PLAYER.QUICK_ACTIONS.TEST.MARK'),
                                method: () => controller.toggleTest(),
                                visible: $scope.user.testAccount === undefined || $scope.user.testAccount === null || $scope.user.testAccount === false,
                                permission: 'PLAYER_MARK_TEST',
                                domainName: $scope.user.domain.name

                            },
                            {
                                label: $translate.instant('UI_NETWORK_ADMIN.PLAYER.QUICK_ACTIONS.TEST.UNMARK'),
                                method: () => controller.toggleTest(),
                                visible: $scope.user.testAccount === true,
                                permission: 'PLAYER_UNMARK_TEST',
                                domainName: $scope.user.domain.name

                            },
                            {
                                label: $translate.instant('UI_NETWORK_ADMIN.PLAYER.QUICK_ACTIONS.CHANGE.AGE_VERIFIED.UNVERIFIED'),
                                method: () => controller.toggleAgeVerification(),
                                visible: $scope.user.ageVerified === true,
                                permission: 'PLAYER_VALIDATE_AGE',
                                domainName: $scope.user.domain.name
                            },
                            {
                                label: $translate.instant('UI_NETWORK_ADMIN.PLAYER.QUICK_ACTIONS.CHANGE.AGE_VERIFIED.VERIFIED'),
                                method: () => controller.toggleAgeVerification(),
                                visible: $scope.user.ageVerified === false,
                                permission: 'PLAYER_VALIDATE_AGE',
                                domainName: $scope.user.domain.name

                            },
                            {
                                label: $translate.instant('UI_NETWORK_ADMIN.PLAYER.QUICK_ACTIONS.CHANGE.ADDRESS_VERIFIED.UNVERIFIED'),
                                method: () => controller.toggleAddressVerification(),
                                visible: $scope.user.addressVerified === true,
                                permission: 'PLAYER_VALIDATE_ADDRESS',
                                domainName: $scope.user.domain.name

                            },
                            {
                                label: $translate.instant('UI_NETWORK_ADMIN.PLAYER.QUICK_ACTIONS.CHANGE.ADDRESS_VERIFIED.VERIFIED'),
                                method: () => controller.toggleAddressVerification(),
                                visible: $scope.user.addressVerified === false,
                                permission: 'PLAYER_VALIDATE_ADDRESS',
                                domainName: $scope.user.domain.name

                            },
                            {
                                label: $translate.instant('UI_NETWORK_ADMIN.PLAYER.QUICK_ACTIONS.MODIFY.TAG'),
                                method: () => controller.changeTag(),
                                 visible: true,
                                 permission: 'PLAYER_TAG_EDIT',
                                domainName: $scope.user.domain.name

                            },
                            {
                                label: $translate.instant('UI_NETWORK_ADMIN.PLAYER.QUICK_ACTIONS.CHANGE.PASSWORD'),
                                method: () => controller.changePassword(),
                                 visible: true,
                                 permission: 'PLAYER_PASSWD',
                                domainName: $scope.user.domain.name

                            },
                            {
                                label: $translate.instant('UI_NETWORK_ADMIN.PLAYER.QUICK_ACTIONS.RESET.PASSWORD'),
                                method: () => controller.resetPassword(),
                                 visible: true,
                                 permission: 'PLAYER_PASSWORD_RESET',
                                domainName: $scope.user.domain.name

                            },
                            {
                                label: $translate.instant('UI_NETWORK_ADMIN.PLAYER.QUICK_ACTIONS.ADD.NOTE'),
                                method: () => controller.addNote(),
                                 visible: true,
                                 permission: 'PLAYER_NOTE_ADD',
                                domainName: $scope.user.domain.name

                            },
                            {
                                label: $translate.instant('UI_NETWORK_ADMIN.PLAYER.QUICK_ACTIONS.ADD.COMMENT'),
                                method: () => controller.addComment(),
                                 visible: true,
                                 permission: 'PLAYER_COMMENT_ADD',
                                domainName: $scope.user.domain.name

                            },
                            {
                                label: $translate.instant('GLOBAL.ACTION.INVALIDATE_MOBILE'),
                                method: () => controller.toggleMobileValidation(),
                                visible: $scope.user.cellphoneValidated === true,
                                permission: 'PLAYER_VALIDATE_MOBILE',
                                domainName: $scope.user.domain.name

                            },
                            {
                                label: $translate.instant('GLOBAL.ACTION.VALIDATE_MOBILE'),
                                method: () => controller.toggleMobileValidation(),
                                visible: $scope.user.cellphoneValidated === false,
                                permission: 'PLAYER_VALIDATE_MOBILE',
                                domainName: $scope.user.domain.name

                            },
                            {
                                label: $translate.instant('GLOBAL.ACTION.INVALIDATE_EMAIl'),
                                method: () => controller.toggleEmailValidation(),
                                visible: $scope.user.emailValidated === true,
                                permission: 'PLAYER_VALIDATE_EMAIL',
                                domainName: $scope.user.domain.name

                            },
                            {
                                label: $translate.instant('GLOBAL.ACTION.VALIDATE_EMAIL'),
                                method: () => controller.toggleEmailValidation(),
                                visible: $scope.user.emailValidated === false,
                                permission: 'PLAYER_VALIDATE_EMAIL',
                                domainName: $scope.user.domain.name

                            },
                            {
                                label: $translate.instant('UI_NETWORK_ADMIN.PLAYER.PROMOOPTOUT.UPDATE_MARKETING_PREFERENCES'),
                                method: () => controller.changePromotionalOptout(),
                                 visible: true,
                                 permission: 'PLAYER_OPTOUT_EDIT',
                                domainName: $scope.user.domain.name

                            },
                            {
                                label: $translate.instant('UI_NETWORK_ADMIN.PLAYER.QUICK_ACTIONS.CHANGE.PERSONAL_INFO'),
                                method: () => controller.changePersonalInfo(),
                                 visible: true,
                                 permission: 'PLAYER_EDIT',
                                domainName: $scope.user.domain.name

                            },
                            {
                                label: $translate.instant('UI_NETWORK_ADMIN.PLAYER.QUICK_ACTIONS.CHANGE.DOB'),
                                method: () => controller.changeDateOfBirth(),
                                 visible: true,
                                 permission: 'PLAYER_DOB_EDIT',
                                domainName: $scope.user.domain.name

                            },
                            {
                                label: $translate.instant('UI_NETWORK_ADMIN.PLAYER.QUICK_ACTIONS.CHANGE.RESIDENTIAL_ADDRESS'),
                                method: () => controller.changeAddress('residentialAddress'),
                                 visible: true,
                                 permission: 'PLAYER_ADDRESS_EDIT',
                                domainName: $scope.user.domain.name

                            },
                            {
                                label: $translate.instant('UI_NETWORK_ADMIN.PLAYER.QUICK_ACTIONS.CHANGE.POSTAL_ADDRESS'),
                                method: () => controller.changeAddress('postalAddress'),
                                 visible: true,
                                 permission: 'PLAYER_ADDRESS_EDIT',
                                domainName: $scope.user.domain.name

                            },
                        ]
                    },
                    {
                        label: "Balance",
                        items: [
                            {
                                label: $translate.instant('UI_NETWORK_ADMIN.PLAYER.QUICK_ACTIONS.CHANGE.ADJUST_BALANCE'),
                                method: () => controller.adjust($scope.data.playerBalance[0].id),
                                 visible: true,
                                 permission: 'PLAYER_BALANCE_ADJUST',
                                domainName: $scope.user.domain.name

                            },
                            {
                                label: $translate.instant('UI_NETWORK_ADMIN.PLAYER.QUICK_ACTIONS.CHANGE.DIRECT_WITHDRAWAL'),
                                method: () => controller.direct_withdrawal($scope.data.playerBalance[0].id),
                                 visible: true,
                                 permission: 'CASHIER_DIRECT_WITHDRAWAL',
                                domainName: $scope.user.domain.name

                            },
                            {
                                label: $translate.instant('UI_NETWORK_ADMIN.PLAYER.QUICK_ACTIONS.CHANGE.WRITE_OFF_BALANCE'),
                                method: () => controller.writeoff($scope.data.playerBalance[0].id),
                                 visible: true,
                                 permission: 'PLAYER_BALANCE_ADJUST_WRITE_OFF',
                                domainName: $scope.user.domain.name

                            },
                            {
                                label: $translate.instant('UI_NETWORK_ADMIN.PLAYER.DEV_TOOLS.FIELDS.REVERT_PENDING_WITHDRAW_BALANCE.LABEL'),
                                method: () => controller.emptyOutPendingWithdrawBalance(false),
                                visible: true,
                                permission: 'CASHIER_DEV_TOOLS',
                                domainName: $scope.user.domain.name

                            },

                            {
                                label: $translate.instant('UI_NETWORK_ADMIN.PLAYER.DEV_TOOLS.FIELDS.CLEAR_PENDING_WITHDRAW_BALANCE.LABEL'),
                                method: () => controller.emptyOutPendingWithdrawBalance(true),
                                visible: true,
                                permission: 'CASHIER_DEV_TOOLS',
                                domainName: $scope.user.domain.name

                            },

                        ]
                    },
                    {
                        label: "Limit",
                        items: [
                            {
                                label: $translate.instant('UI_NETWORK_ADMIN.PLAYER.QUICK_ACTIONS.MODIFY.DAILY_LOSS_LIMIT'),
                                method: () => controller.changeLimits(3),
                                 visible: true,
                                 permission: 'PLAYER_LIMIT_EDIT',
                                domainName: $scope.user.domain.name
                            },
                            {
                                label: $translate.instant('UI_NETWORK_ADMIN.PLAYER.QUICK_ACTIONS.DELETE.DAILY_LOSS_LIMIT'),
                                method: () => controller.removeLimit(3),
                                visible: $scope.data.limits.dailyLossLimit,
                                permission: 'PLAYER_LIMIT_EDIT',
                                domainName: $scope.user.domain.name

                            },
                            {
                                label: $translate.instant('UI_NETWORK_ADMIN.PLAYER.QUICK_ACTIONS.MODIFY.WEEKLY_LOSS_LIMIT'),
                                method: () => controller.changeLimits(4),
                                 visible: true,
                                 permission: 'PLAYER_LIMIT_EDIT',
                                domainName: $scope.user.domain.name

                            },
                            {
                                label: $translate.instant('UI_NETWORK_ADMIN.PLAYER.QUICK_ACTIONS.DELETE.WEEKLY_LOSS_LIMIT'),
                                method: () => controller.removeLimit(4),
                                visible: $scope.data.limits.weeklyLossLimit,
                                permission: 'PLAYER_LIMIT_EDIT',
                                domainName: $scope.user.domain.name

                            },
                            {
                                label: $translate.instant('UI_NETWORK_ADMIN.PLAYER.QUICK_ACTIONS.MODIFY.MONTHLY_LOSS_LIMIT'),
                                method: () => controller.changeLimits(2),
                                 visible: true,
                                 permission: 'PLAYER_LIMIT_EDIT',
                                domainName: $scope.user.domain.name

                            },
                            {
                                label: $translate.instant('UI_NETWORK_ADMIN.PLAYER.QUICK_ACTIONS.DELETE.MONTHLY_LOSS_LIMIT'),
                                method: () => controller.removeLimit(2),
                                visible: $scope.data.limits.monthlyLossLimit,
                                permission: 'PLAYER_LIMIT_EDIT',
                                domainName: $scope.user.domain.name

                            },
                            {
                                label: $translate.instant('UI_NETWORK_ADMIN.PLAYER.QUICK_ACTIONS.ADD.SELF_EXCLUSION'),
                                method: () => controller.changeSelfExclusion(),
                                visible: $scope.data.selfExclusion === undefined || $scope.data.selfExclusion === null,
                                permission: 'EXCLUSION_ADD',
                                domainName: $scope.user.domain.name

                            },
                            {
                                label: $translate.instant('UI_NETWORK_ADMIN.PLAYER.QUICK_ACTIONS.DELETE.SELF_EXCLUSION'),
                                method: () => controller.removeSelfExclusion(),
                                visible: $scope.data.selfExclusion !== undefined && $scope.data.selfExclusion !== null,
                                permission: 'EXCLUSION_REVOKE',
                                domainName: $scope.user.domain.name

                            },
                            {
                                label: $translate.instant('UI_NETWORK_ADMIN.PLAYER.QUICK_ACTIONS.DOCUMENT_UPLOAD_VERIFY'),
                                method: () => controller.uploadAndSubmitDocument(),
                                 visible: true,
                                 permission: 'ONFIDO_DOCUMENT_MANUAL_VERIFY',
                                domainName: $scope.user.domain.name

                            },
                            {
                                label: $translate.instant('UI_NETWORK_ADMIN.PLAYER.QUICK_ACTIONS.BVN_VERIFY'),
                                method: () => controller.checkBvn(),
                                 visible: true,
                                 permission: 'PAYSTACK_BVN_MANUAL_VERIFY',
                                domainName: $scope.user.domain.name

                            },
                            {
                                label: $translate.instant('UI_NETWORK_ADMIN.PLAYER.QUICK_ACTIONS.VERIFY_BY_BANK_ACCOUNT'),
                                method: () => controller.checkByBankAccount(),
                                 visible: true,
                                 permission: 'PAYSTACK_BVN_MANUAL_VERIFY',
                                domainName: $scope.user.domain.name

                            },
                            {
                                label: $translate.instant('UI_NETWORK_ADMIN.PLAYER.QUICK_ACTIONS.VERIFY_NIN_PHONE'),
                                method: () => controller.checkNinPhone(),
                                 visible: true,
                                 permission: 'NIN_PHONE_MANUAL_VERIFY',
                                domainName: $scope.user.domain.name

                            },
                            {
                                label: $translate.instant('UI_NETWORK_ADMIN.PLAYER.QUICK_ACTIONS.ADD.COOL_OFF'),
                                method: () => controller.cooloffPlayer(),
                                visible: !$scope.data.coolOff,
                                permission: 'COOLOFF_ADD',
                                domainName: $scope.user.domain.name

                            },
                            {
                                label: $translate.instant('UI_NETWORK_ADMIN.PLAYER.QUICK_ACTIONS.DELETE.COOL_OFF'),
                                method: () => controller.clearPlayerCooloff(),
                                visible: $scope.data.coolOff,
                                permission: 'COOLOFF_CLEAR',
                                domainName: $scope.user.domain.name

                            },



                        ]
                    },
                    {
                        label: $translate.instant('UI_NETWORK_ADMIN.PLAYER.QUICK_ACTIONS.REALITY_CHECK_UPDATE.TITLE'),
                        method: () => controller.setRealityCheckTime(),
                         visible: true,
                         permission: 'USER_REALITYCHECK_EDIT',
                        domainName: $scope.user.domain.name

                    },
                    {
                        label: $translate.instant('UI_NETWORK_ADMIN.PLAYER.QUICK_ACTIONS.ADD.RESTRICTION'),
                        method: () => controller.addRestriction(),
                         visible: true,
                         permission: 'USER_RESTRICTIONS_SET',
                        domainName: $scope.user.domain.name

                    },
                    {
                        label: "Bonus",
                        items: [
                            {
                                label: $translate.instant('UI_NETWORK_ADMIN.PLAYER.QUICK_ACTIONS.REGISTER_BONUS'),
                                method: () => controller.registerBonus(),
                                 visible: true,
                                 permission: 'MANUAL_BONUS_ALLOCATION',
                                domainName: $scope.user.domain.name

                            },
                            {
                                label: $translate.instant('UI_NETWORK_ADMIN.PLAYER.QUICK_ACTIONS.GRANT_BONUS'),
                                method: () => controller.grantBonus(),
                                 visible: true,
                                 permission: 'GRANT_BONUS_ALLOCATION',
                                domainName: $scope.user.domain.name

                            },
                            {
                                label: $translate.instant('UI_NETWORK_ADMIN.PLAYER.ALLOW_AUTO_WITHDRAWALS'),
                                method: () => controller.toggleAutoWithdrawalAllowed(),
                                visible: $scope.user.autoWithdrawalAllowed !== undefined && $scope.user.autoWithdrawalAllowed !== null && $scope.user.autoWithdrawalAllowed === false,
                                permission: 'PLAYER_AUTO_WITHDRAWAL_EDIT',
                                domainName: $scope.user.domain.name

                            },
                            {
                                label: $translate.instant('UI_NETWORK_ADMIN.PLAYER.DISALLOW_AUTO_WITHDRAWALS'),
                                method: () =>  controller.toggleAutoWithdrawalAllowed(),
                                visible: ($scope.user.autoWithdrawalAllowed === undefined || $scope.user.autoWithdrawalAllowed === null) ||
                                    ($scope.user.autoWithdrawalAllowed === true),
                                permission: 'PLAYER_AUTO_WITHDRAWAL_EDIT',
                                domainName: $scope.user.domain.name

                            },

                        ]
                    },
                    {
                        label: $translate.instant('UI_NETWORK_ADMIN.PLAYER.QUICK_ACTIONS.UPLOAD.DOCUMENT'),
                        method: () => controller.documentAdd(),
                         visible: true,
                         permission: ['DOCUMENT_REGULAR_EDIT', 'DOCUMENT_SENSITIVE_EDIT'],
                        domainName: $scope.user.domain.name

                    },
                    {
                        label: $translate.instant('UI_NETWORK_ADMIN.PLAYER.QUICK_ACTIONS.SEND_NOTIFICATION'),
                        method: () => controller.sendNotification(),
                         visible: true,
                         permission: 'NOTIFICATIONS_EDIT',
                        domainName: $scope.user.domain.name

                    },
                    {
                        label: $translate.instant('UI_NETWORK_ADMIN.PLAYER.QUICK_ACTIONS.SEND_SMS'),
                        method: () => controller.sendSMS(),
                        visible: $scope.user.cellphoneNumber !== undefined && $scope.user.cellphoneNumber !== null && $scope.user.cellphoneNumber !== '',
                        permission: 'NOTIFICATIONS_EDIT',
                        domainName: $scope.user.domain.name

                    },
                    {
                        label: $translate.instant('UI_NETWORK_ADMIN.PLAYER.QUICK_ACTIONS.SEND_TEMPLATE.TITLE'),
                        method: () => controller.sendEmail(),
                        visible: $scope.user.email !== undefined && $scope.user.email !== null && $scope.user.email !== '',
                        permission: 'SEND_PLAYER_TEMPLATE',
                        domainName: $scope.user.domain.name

                    },
                    {
                        label: $translate.instant('UI_NETWORK_ADMIN.PLAYER_LINKING.ADD.LABEL'),
                        method: () => controller.addPlayerLink(true),
                         visible: true,
                         permission: 'PLAYER_LINK_ADD',
                        domainName: $scope.user.domain.name

                    },
                ];
                window.VuePluginRegistry.loadByPage("QuickActions")
                
                controller.unlockAllFreeGamesForPlayer = function () {
                    gameRest.unlockFreeGamesForUser($scope.user.guid).then(() => {
                        $scope.data.freeGamesLocked = false;
                    }).catch(function(error) {
                    errors.catch("", false)(error)
                });
                };

                controller.lockAllFreeGamesForPlayer = function () {
                    gameRest.lockFreeGamesForUser($scope.user.guid).then(() => {
                        $scope.data.freeGamesLocked = true;
                    })
                }

            }
        ]
    }
});
