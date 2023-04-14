'use strict'

angular.module('lithium').controller('RestrictionsViewController', ['set', 'RestrictionsRest','UserRest', '$uibModal', 'bsLoadingOverlayService', 'errors', 'notify',
    function(set, rest,userRest, $uibModal, bsLoadingOverlayService, errors, notify) {
        var controller = this;
        controller.set = set;

        controller.getErrorMessageKey = function() {
            if (controller.set.systemRestriction) {
                return "ERROR_DICTIONARY.SYSTEM_RESTRICTION." + set.id;
            }
            return "ERROR_DICTIONARY.NORMAL_RESTRICTION." + set.id;
        }

        controller.excludeTag = null;

        controller.fetchTag = () => {
            if(!angular.isUndefinedOrNull(set.excludeTagId)) {
                userRest.findTagById(`${set.excludeTagId}`).then(res => {
                    controller.excludeTag = res.plain()
                })
            }
        }

        controller.renderExcludeTag = () => {
            if(angular.isUndefinedOrNull(controller.excludeTag)) {
                return 'N/A'
            }

            return `<span class="label label-primary">${this.excludeTag.name}</span>`
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
        };

        controller.editAltMessage = function () {
            let modalInstance = $uibModal.open({
                animation: true,
                ariaLabelledBy: 'modal-title',
                ariaDescribedBy: 'modal-body',
                templateUrl: 'scripts/controllers/dashboard/restrictions/dictionary/edit-altmessage/edit.html',
                controller: 'EditAltMessageModal',
                controllerAs: 'controller',
                backdrop: 'static',
                size: 'md',
                resolve: {
                    domainRestrictionSet: () =>  controller.set,
                    parentRestrictionKey: () => this.getErrorMessageKey(),
                    loadMyFiles: function($ocLazyLoad) {
                        return $ocLazyLoad.load({
                            name: 'lithium',
                            files: ['scripts/controllers/dashboard/restrictions/dictionary/edit-altmessage/edit.js']
                        })
                    }
                }
            });


            modalInstance.result.then(function (response) {
                controller.set = response
            });
        };

        controller.updateRestrictionTag = () => {
            let modalInstance = $uibModal.open({
                animation: true,
                ariaLabelledBy: 'modal-title',
                ariaDescribedBy: 'modal-body',
                templateUrl: 'scripts/controllers/dashboard/restrictions/dictionary/tag/update.html',
                controller: 'UpdateRestrictionTagModal',
                controllerAs: 'controller',
                backdrop: 'static',
                size: 'md',
                resolve: {
                    domainRestrictionSet: () =>  controller.set,
                    parentRestrictionKey: () => this.getErrorMessageKey(),
                    loadMyFiles: function($ocLazyLoad) {
                        return $ocLazyLoad.load({
                            name: 'lithium',
                            files: ['scripts/controllers/dashboard/restrictions/dictionary/tag/update.js']
                        })
                    }
                }
            });


            modalInstance.result.then(function (tag) {
                if(angular.isUndefinedOrNull(tag)) {
                    controller.excludeTag = null 
                    set.excludeTagId = null
                    return
                }
                
                controller.excludeTag = tag 
                set.excludeTagId = tag.id
            });
        }

        controller.getActionCode= function (action) {
        let codes = "";
            for (var i = 0; i < action.length; i++) {
				if (i>0) { codes = codes + ","; }
				codes = codes + action[i].code;
            }
            return codes;
        }

	    controller.getPlaceTemplate= function () {
		    return set.placeMailTemplate;
	    }

	    controller.getLiftTemplate= function () {
		    return set.liftMailTemplate;
	    }

	    controller.isInterventionCompsBlock = () => {
            const name  = this.set.name.toUpperCase().replaceAll(new RegExp(/\s+/,"gm"), "_");
            return name === "INTERVENTION_COMPS_BLOCK";
        }

        controller.isSystemRestrictionCasinoBlock = () => {
            const name  = this.set.name.toUpperCase().replaceAll(new RegExp(/\s+/,"gm"), "_");
            return name === "INTERVENTION_CASINO_BLOCK" || "PLAYER_CASINO_BLOCK";
        }

        controller.canEditAltMessageCount = () => {
            if (this.set.altMessageCount) {
                return true;
            }
            if (controller.isInterventionCompsBlock()) {
                return true;
            }
            return controller.isSystemRestrictionCasinoBlock();
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
                            ?  'UI_NETWORK_ADMIN.RESTRICTIONS.EDIT.COMMUNICATE_TO_PLAYER.ENABLE.SUCCESS'
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

        controller.editPlaceMailTemplate = function (){
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
        }

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

        controller.changelogs = {
            domainName: set.domain.name,
            entityId: set.id,
            restService: rest,
            reload: 0
        }

        controller.fetchTag()
    }
]);