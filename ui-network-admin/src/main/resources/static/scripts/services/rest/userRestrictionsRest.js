'use strict';

angular.module('lithium').factory('UserRestrictionsRest', ['Restangular',
    function(Restangular) {
        try {
            var service = {};
            var config = function(domainName) {
                return Restangular.withConfig(function(RestangularConfigurer) {
                    RestangularConfigurer.setBaseUrl('services/service-limit/backoffice/user-restrictions/'+domainName+'/');
                });
            }

            service.get = function(domainName, userGuid) {
                return config(domainName).one('').get({userGuid: userGuid});
            }

            service.getEligibleRestrictionSetsForUser = function(domainName, userGuid) {
                return config(domainName).one('getEligibleRestrictionSetsForUser').get({userGuid: userGuid});
            }

            service.set = function (domainName, userGuid, domainRestrictionSetId, userId, comment, subType = null) {
                return config(domainName).one('set').customPOST('', '', {
                    userGuid: userGuid,
                    domainRestrictionSetId: domainRestrictionSetId,
                    userId: userId,
                    comment: comment,
                    subType: subType
                }, {});
            }

            service.lift = function (domainName, userGuid, userRestrictionSetId, userId, comment) {
                return config(domainName).one('lift').customPOST('', '', {
                    userGuid: userGuid,
                    userRestrictionSetId: userRestrictionSetId,
                    userId: userId,
                    comment: comment
                }, {});
            }

            return service;
        } catch (err) {
            console.error(err);
            throw err;
        }
    }
]);
