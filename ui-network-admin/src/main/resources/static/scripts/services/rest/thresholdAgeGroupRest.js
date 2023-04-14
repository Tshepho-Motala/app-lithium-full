'use strict';

angular.module('lithium')
.factory('userThresholdAgeGroupRest', ['$log', 'Restangular',
    function($log, Restangular) {
        try {
            var service = {};

            var rest = function() {
                return Restangular.withConfig(function(RestangularConfigurer) {
                    RestangularConfigurer.setBaseUrl("services/service-user-provider-threshold/backoffice/threshold-age-group");
                });
            }
            service.findByDomainMaxAndMinAge=function(domainName,maxAge,minAge){
                return rest().all("find-by-age-group").one("p").get({domainName:domainName,maxAge:maxAge,minAge:minAge});

            }
            service.saveAgeRanges = function (data) {
                return rest().one('set-age-limit-group').post('', data, {}, {});
            }
            service.editAgeRanges = function (data) {
                return rest().one('edit-age-limit-group').post('', data, {}, {});
            }
            service.editMinMaxAgeRanges = function (data) {
                return rest().one('edit-age-limit-min-max').post('', data, {}, {});
            }
            service.deactivateAgeRanges = function (data) {
                return rest().one('deactivate-age-limit-group').post('', data, {}, {});
            }
            service.deactivateSingleRevision = function (data) {
                return rest().one('deactivate-single-revision').post('', data, {}, {});
            }
            return service;
        } catch (err) {
            $log.error(err);
            throw err;
        }
    }
]);
