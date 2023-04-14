'use strict';

angular.module('lithium')
.factory('userThresholdRevisionRest', ['$log', 'Restangular',
    function($log, Restangular) {
        try {
            var service = {};

            var rest = function() {
                return Restangular.withConfig(function(RestangularConfigurer) {
                    RestangularConfigurer.setBaseUrl("services/service-user-provider-threshold/backoffice/threshold-revision");
                });
            }
            service.findAll = function() {
                return rest().all("all").getList();
            }
            service.save=function(model){
                return rest().all("save").post(model);
            }

            service.findByDomainAndGranularity=function(domain, granularity){
                return rest().all("domain").one(domain+'').all("granularity").one(granularity +'').get();
            }

            service.findAgeBasedThresholdByDomainAndGranularity=function(domain, granularity,minAge,maxAge){
                return rest().all("age-based").all("domain").one(domain+'').all("granularity").one(granularity +'')
                .all("min-age").one(minAge+'').all("max-age").one(maxAge+'').get();
            }
            service.deactivateByDomainAndGranularity=function(domain,granularity){
                let data={};
                data.domain=domain;
                data.granularity=granularity;
                return rest().all("remove-threshold-revision").post(data);
            }

            service.saveAgeRanges = function (data) {
                return rest().one('set-age-limit-group').post('', data, {}, {});
            }
            return service;
        } catch (err) {
            $log.error(err);
            throw err;
        }
    }
]);
