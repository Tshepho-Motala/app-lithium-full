'use strict'

angular.module('lithium').controller('RestrictionsEditController', ['set', 'restrictionTypes', '$translate', '$uibModal', 'bsLoadingOverlayService', 'errors', 'notify', 'RestrictionsRest', '$state', 'EmailTemplateRest',
    function(set, restrictionTypes, $translate, $uibModal, bsLoadingOverlayService, errors, notify, rest, $state, emailTemplateRest) {
        var controller = this;
        controller.set = set.plain();
        controller.restrictionTypes = restrictionTypes.plain();

        console.debug("set", controller.set);
        console.debug("restrictionTypes", controller.restrictionTypes);

        // Removing restriction types that are already added
        controller.getEligibleRestrictionTypes = function() {
            var restrictionTypes = angular.copy(controller.restrictionTypes);
            for (var i = 0; i < controller.set.restrictions.length; i++) {
                for (var k = 0; k < restrictionTypes.length; k++) {
                    if (controller.set.restrictions[i].restriction.code == restrictionTypes[k].code) {
                        restrictionTypes.splice(k, 1);
                    }
                }
            }
            return restrictionTypes;
        }

        controller.getRestrictionTypeName = function(code) {
            for (var i = 0; i < controller.restrictionTypes.length; i++) {
                if (controller.restrictionTypes[i].code === code) return controller.restrictionTypes[i].name;
            }
            return code;
        }

        controller.toggleEnabled = function() {
            bsLoadingOverlayService.start({referenceId: "loading"});
            rest.domainRestrictionSetToggleEnabled(set.id)
                .then(function (response) {
                    if (response._status !== 0) {
                        notify.error(response._message);
                    } else {
                        var msg = (controller.set.enabled === true)
                            ? 'UI_NETWORK_ADMIN.RESTRICTIONS.EDIT.DISABLE.SUCCESS'
                            : 'UI_NETWORK_ADMIN.RESTRICTIONS.EDIT.ENABLE.SUCCESS';
                        notify.success(msg);
                        controller.set = response.plain();
                    }
                }).catch(
                errors.catch("Failed to toggle enabled flag on set.", false)
            ).finally(function () {
                bsLoadingOverlayService.stop({referenceId: "loading"});
            });
        }

        controller.delete = function() {
            bsLoadingOverlayService.start({referenceId: "loading"});
            rest.domainRestrictionSetDelete(set.id)
                .then(function (response) {
                    if (response._status !== 0) {
                        notify.error(response._message);
                    } else {
                        notify.success('UI_NETWORK_ADMIN.RESTRICTIONS.EDIT.DELETE.SUCCESS');
                        $state.go('dashboard.restrictions.dictionary');
                    }
                }).catch(
                errors.catch("", false)
            ).finally(function () {
                bsLoadingOverlayService.stop({referenceId: "loading"});
            });
        }

        controller.editName = function() {
            var modalInstance = $uibModal.open({
                animation: true,
                ariaLabelledBy: 'modal-title',
                ariaDescribedBy: 'modal-body',
                templateUrl: 'scripts/controllers/dashboard/restrictions/dictionary/components/set/set-name.html',
                controller: 'RestrictionSetNameModal',
                controllerAs: 'controller',
                size: 'md cascading-modal',
                backdrop: 'static',
                resolve: {
                    set: function() { return angular.copy(controller.set) },
                    loadMyFiles:function($ocLazyLoad) {
                        return $ocLazyLoad.load({
                            name:'lithium',
                            files: [
                                'scripts/controllers/dashboard/restrictions/dictionary/components/set/set-name.js'
                            ]
                        })
                    }

                }
            });

            modalInstance.result.then(function(response) {
                controller.set = response.plain();
            });
        }

	    controller.getPlaceTemplate = function () {
		    return controller.set.placeMailTemplate;
	    }

	    controller.getLiftTemplate= function () {
		    return controller.set.liftMailTemplate;
	    }

	    controller.editPlaceMailTemplate = function () {
		    var modalInstance = $uibModal.open({
			    animation: true,
			    ariaLabelledBy: 'modal-title',
			    ariaDescribedBy: 'modal-body',
			    templateUrl: 'scripts/controllers/dashboard/restrictions/dictionary/components/set/set-template.html',
			    controller: 'RestrictionSetPlaceTemplateModal',
			    controllerAs: 'controller',
			    size: 'md cascading-modal',
			    backdrop: 'static',
			    resolve: {
				    set: function() { return angular.copy(controller.set) },
				    loadMyFiles:function($ocLazyLoad) {
					    return $ocLazyLoad.load({
						    name:'lithium',
						    files: [
							    'scripts/controllers/dashboard/restrictions/dictionary/components/set/set-place-template.js'
						    ]
					    })
				    }
			    }
		    });

		    modalInstance.result.then(function(response) {
			    controller.set = response.plain();
		    });
	    };

	    controller.editLiftMailTemplate = function () {
		    var modalInstance = $uibModal.open({
			    animation: true,
			    ariaLabelledBy: 'modal-title',
			    ariaDescribedBy: 'modal-body',
			    templateUrl: 'scripts/controllers/dashboard/restrictions/dictionary/components/set/set-template.html',
			    controller: 'RestrictionSetLiftTemplateModal',
			    controllerAs: 'controller',
			    size: 'md cascading-modal',
			    backdrop: 'static',
			    resolve: {
				    set: function() { return angular.copy(controller.set) },
				    loadMyFiles:function($ocLazyLoad) {
					    return $ocLazyLoad.load({
						    name:'lithium',
						    files: [
							    'scripts/controllers/dashboard/restrictions/dictionary/components/set/set-lift-template.js'
						    ]
					    })
				    }
			    }
		    });

		    modalInstance.result.then(function(response) {
			    controller.set = response.plain();
		    });
	    };

	    controller.editPlaceOutcomeActions = function () {
		    var modalInstance = $uibModal.open({
			    animation: true,
			    ariaLabelledBy: 'modal-title',
			    ariaDescribedBy: 'modal-body',
			    templateUrl: 'scripts/controllers/dashboard/restrictions/dictionary/components/set/set-action.html',
			    controller: 'RestrictionSetActionPlaceModal',
			    controllerAs: 'controller',
			    size: 'md cascading-modal',
			    backdrop: 'static',
			    resolve: {
				    set: function() { return angular.copy(controller.set) },
				    outcomeActions: ["RestrictionsRest", "$stateParams", function(rest, $stateParams) {
					    return rest.findAllOutcomeActions();
				    }],
				    loadMyFiles:function($ocLazyLoad) {
					    return $ocLazyLoad.load({
						    name:'lithium',
						    files: [
							    'scripts/controllers/dashboard/restrictions/dictionary/components/set/set-place-action.js'
						    ]
					    })
				    }
			    }
		    });

		    modalInstance.result.then(function(response) {
			    controller.set = response.plain();
		    });
	    };

	    controller.editLiftOutcomeActions = function () {
		    var modalInstance = $uibModal.open({
			    animation: true,
			    ariaLabelledBy: 'modal-title',
			    ariaDescribedBy: 'modal-body',
			    templateUrl: 'scripts/controllers/dashboard/restrictions/dictionary/components/set/set-action.html',
			    controller: 'RestrictionSetActionLiftModal',
			    controllerAs: 'controller',
			    size: 'md cascading-modal',
			    backdrop: 'static',
			    resolve: {
				    set: function() { return angular.copy(controller.set) },
				    outcomeActions: ["RestrictionsRest", "$stateParams", function(rest, $stateParams) {
					    return rest.findAllOutcomeActions();
				    }],
				    loadMyFiles:function($ocLazyLoad) {
					    return $ocLazyLoad.load({
						    name:'lithium',
						    files: [
							    'scripts/controllers/dashboard/restrictions/dictionary/components/set/set-lift-action.js'
						    ]
					    })
				    }

			    }
		    });

		    modalInstance.result.then(function(response) {
			    controller.set = response.plain();
		    });
	    };

	    controller.getActionCode= function (action) {
		    let codes = "";
		    for (var i = 0; i < action.length; i++) {
			    if (i>0) { codes = codes + ","; }
			    codes = codes + action[i].code;
		    }
		    return codes;
	    }

	    controller.addRestriction = function() {
            var modalInstance = $uibModal.open({
                animation: true,
                ariaLabelledBy: 'modal-title',
                ariaDescribedBy: 'modal-body',
                templateUrl: 'scripts/controllers/dashboard/restrictions/dictionary/components/restriction/restriction.html',
                controller: 'RestrictionsRestrictionModal',
                controllerAs: 'controller',
                size: 'md cascading-modal',
                backdrop: 'static',
                resolve: {
                    set: function() { return set },
                    restriction: function() { return null; },
                    restrictionTypes: function() { return controller.getEligibleRestrictionTypes(); },
                    loadMyFiles:function($ocLazyLoad) {
                        return $ocLazyLoad.load({
                            name:'lithium',
                            files: [
                                'scripts/controllers/dashboard/restrictions/dictionary/components/restriction/restriction.js'
                            ]
                        })
                    }

                }
            });

            modalInstance.result.then(function(response) {
                controller.set = response.plain();
            });
        }

        controller.modifyRestriction = function($index) {
            var modalInstance = $uibModal.open({
                animation: true,
                ariaLabelledBy: 'modal-title',
                ariaDescribedBy: 'modal-body',
                templateUrl: 'scripts/controllers/dashboard/restrictions/dictionary/components/restriction/restriction.html',
                controller: 'RestrictionsRestrictionModal',
                controllerAs: 'controller',
                size: 'md cascading-modal',
                backdrop: 'static',
                resolve: {
                    set: function() { return set },
                    restriction: function() {
                        var restrictionCopy = angular.copy(controller.set.restrictions[$index]);
                        return restrictionCopy;
                    },
                    restrictionTypes: function() { return controller.restrictionTypes; },
                    loadMyFiles:function($ocLazyLoad) {
                        return $ocLazyLoad.load({
                            name:'lithium',
                            files: [
                                'scripts/controllers/dashboard/restrictions/dictionary/components/restriction/restriction.js'
                            ]
                        })
                    }

                }
            });

            modalInstance.result.then(function(response) {
                controller.set = response.plain();
            });
        }

        controller.removeRestriction = function(restrictionId) {
            bsLoadingOverlayService.start({referenceId: "loading"});
            rest.domainRestrictionSetRestrictionDelete(set.id, restrictionId)
                .then(function (response) {
                    if (response._status !== 0) {
                        notify.error(response._message);
                    } else {
                        notify.success('UI_NETWORK_ADMIN.RESTRICTIONS.EDIT.DELETERESTRICTION.SUCCESS');
                        controller.set = response.plain();
                    }
                }).catch(
                errors.catch("", false)
            ).finally(function () {
                bsLoadingOverlayService.stop({referenceId: "loading"});
            });
        }

        controller.getErrorMessageKey = function() {
            if (controller.set.systemRestriction) {
                return "ERROR_DICTIONARY.SYSTEM_RESTRICTION." + set.id;
            }
            return "ERROR_DICTIONARY.NORMAL_RESTRICTION." + set.id;
        }

        controller.editTranslation = function () {
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
                        return set.domain.name;
                    },
                    translationData: ['rest-translate', function(translateRest) {
                        return translateRest.getKeyIdByCode(controller.getErrorMessageKey(), set.domain.name).then(function(keyId) {
                            return {id: keyId};
                        });
                    }],
                    domainSpecific: function() {
                        return true;
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
        }

        controller.toggleDwhVisibility = function() {
		    bsLoadingOverlayService.start({referenceId: "loading"});
		    rest.domainRestrictionSetToggleDwhVisibility(set.id)
			    .then(function (response) {
				    if (response._status !== 0) {
					    notify.error(response._message);
				    } else {
					    var msg = (controller.set.dwhVisible === true)
						    ? 'UI_NETWORK_ADMIN.RESTRICTIONS.EDIT.DWH_VISIBLE.ENABLE.SUCCESS'
						    : 'UI_NETWORK_ADMIN.RESTRICTIONS.EDIT.DWH_VISIBLE.DISABLE.SUCCESS';
					    notify.success(msg);
					    controller.set = response.plain();
				    }
			    }).catch(
			    errors.catch("Failed to toggle DWH visible flag on set.", false)
		    ).finally(function () {
			    bsLoadingOverlayService.stop({referenceId: "loading"});
		    });
	    }

	    controller.toggleCommunicateToPlayer = function() {
            bsLoadingOverlayService.start({referenceId: "loading"});
            rest.domainRestrictionSetToggleCommunicateToPlayer(set.id)
                .then(function (response) {
                    if (response._status !== 0) {
                        notify.error(response._message);
                    } else {
                        var msg = (controller.set.communicateToPlayer === true)
                            ? 'UI_NETWORK_ADMIN.RESTRICTIONS.EDIT.COMMUNICATE_TO_PLAYER.ENABLE.SUCCESS'
                            : 'UI_NETWORK_ADMIN.RESTRICTIONS.EDIT.COMMUNICATE_TO_PLAYER.DISABLE.SUCCESS';
                        notify.success(msg);
                        controller.set = response.plain();
                    }
                }).catch(
                errors.catch("Failed to toggle communicate to player flag on set.", false)
            ).finally(function () {
                bsLoadingOverlayService.stop({referenceId: "loading"});
            });
        }
    }
]);