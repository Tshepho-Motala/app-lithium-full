'use strict';

angular.module('lithium')
.factory('userThresholdHistoryRest', ['$log', 'Restangular',
    function($log, Restangular) {
        try {
            var service = {};

            var rest = function() {
                return Restangular.withConfig(function(RestangularConfigurer) {
                    RestangularConfigurer.setBaseUrl("services/service-user-provider-threshold/backoffice/player-threshold-history");
                });
            }
            service.findAll = function() {
                return rest().all("all").getList();
            }
            service.save=function(model){
                return rest().all("save").post(model);
            }

            service.findByDomainAndGranurality=function(domain, granurality){
                return rest().all("domain").one(domain+'').all("granurality").one(granurality +'').get();
            }
            service.deleteByDomainAndGranurality=function(domain,granurality){
                return rest().all("delete").all("domain").one(domain+'').all("granurality").one(granurality +'').get();
            }

            service.findByPlayerGuid=function(playerGuid){
                return rest().all("find-by-playerguid").one("p").get({playerGuid:playerGuid});
            }
            return service;
        } catch (err) {
            $log.error(err);
            throw err;
        }
    }
]);
