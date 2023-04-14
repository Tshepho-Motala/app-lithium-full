'use strict';

angular.module('lithium-rest-trans-type',['restangular'])
    .factory('rest-trans-type', ['$log', 'Restangular',
        function($log, Restangular) {
            try {
                var service = {};
                var config = Restangular.withConfig(function(RestangularConfigurer) {
                    RestangularConfigurer.setBaseUrl('services/service-accounting/admin/transactions');
                });

                service.all = function() {
                    return config.all("types").getList();
                }

                return service;
            } catch (err) {
                $log.error(err);
                throw err;
            }
        }
    ]);
