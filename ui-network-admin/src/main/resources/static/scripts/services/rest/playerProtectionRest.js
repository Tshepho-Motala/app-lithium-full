'use strict';

angular.module('lithium')
.factory('playerProtectionRest', ['$log', 'Restangular',
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

            service.findByAmountAndGranurality=function(amount, granurality){
                return rest().all("amount").one(amount+'').all("granurality").one(granurality +'').get();
            }

            return service;
        } catch (err) {
            $log.error(err);
            throw err;
        }
    }
]);
