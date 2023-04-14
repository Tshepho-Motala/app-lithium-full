'use strict';

angular.module('lithium').factory('RestrictionsRest', ['Restangular',
    function(Restangular) {
        try {
            var service = {};
            var config = Restangular.withConfig(function(RestangularConfigurer) {
                RestangularConfigurer.setBaseUrl('services/service-limit/backoffice/restrictions/');
            });

            service.restrictionTypes = function() {
                return config.all("").getList();
            }

            service.findAllOutcomeActions = function() {
                return config.all("restriction-outcome-action-codes").getList();
            }

            service.findSetById = function(id) {
                return config.one(id).get();
            }

            service.domainRestrictionSetCreate = function(dictionary) {
                return config.all("create").post(dictionary);
            }

            service.domainRestrictionSetChangeName = function(id, newName) {
                return config.one(id+"").all("changename").customPOST('', '', {newName: newName}, {});
            }

	        service.domainRestrictionSetMailTemplate = function(id, templateName, isPlace) {
		        return config.one(id+"").all("change-template").customPOST('', '', {templateName: templateName, isPlace: isPlace}, {});
	        }

	        service.domainRestrictionSetChangeLiftActions = function(id, actions) {
		        return config.one(id+"").all("change-lift-actions").customPOST('', '',{actions: actions}, {});
	        }

	        service.domainRestrictionSetChangePlaceActions = function(id, actions) {
		        return config.one(id+"").all("change-place-actions").customPOST('', '',{actions: actions}, {});
	        }

            service.domainRestrictionSetToggleEnabled = function(id) {
                return config.one(id+"").all("toggle").all("enabled").post();
            }

            service.domainRestrictionSetToggleDwhVisibility = function(id) {
                return config.one(id+"").all("toggle").all("dwh-visible").post();
            }

	        service.domainRestrictionSetToggleCommunicateToPlayer = function(id) {
		        return config.one(id+"").all("toggle").all("communicate-to-player").post();
	        }
            service.domainRestrictionSetDelete = function(id) {
                return config.one(id+"").all("delete").post();
            }

            service.domainRestrictionSetRestrictionAdd = function(id, restriction) {
                return config.one(id+"").all("restriction").all("add").post(restriction);
            }

            service.domainRestrictionSetRestrictionUpdate = function(id, restrictionId, restrictionUpdate) {
                return config.one(id+"").one("restriction", restrictionId).all("update").post(restrictionUpdate);
            }

            service.domainRestrictionSetRestrictionDelete = function(id, restrictionId) {
                return config.one(id+"").one("restriction", restrictionId).all("delete").post();
            }

            service.domainRestrictionSets = function(domainName) {
                return config.one("sets").all(domainName).getList();
            }

            service.changelogs = function(domainName, entityId, page) {
                return config.one(entityId+"").one('changelogs').get({ p: page });
            }

            service.updateAltMessageCount = (restrictionSetId, action) => {
                return config.one(`${restrictionSetId}`).one('update-altmessage-count').one(action).post();
            }
            service.list = (domains, enabled) => {
                return config.one("list").get({ domains, enabled })
            }

            service.updateExcludeTagId = (restrictionSetId, excludeTagId) => {
                return config.one(`${restrictionSetId}`).one('update-exclude-tag-id')
                    .customPOST('', '',{excludeTagId}, {});
            }

            /* service.table = (commaSeparatedDomains) => {
                return config.table("table").get({ domains: commaSeparatedDomains })
            } */

            return service;
        } catch (err) {
            console.error(err);
            throw err;
        }
    }
]);
