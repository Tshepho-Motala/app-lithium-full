'use strict';

angular.module('lithium-rest-cashier-dm', ['restangular'])
    .factory('rest-cashier-dm', ['$rootScope', 'Restangular', '$security',
        function ($rootScope, Restangular, security) {
            try {
                var service = {};
                var config = Restangular.withConfig(function (RestangularConfigurer) {
                    RestangularConfigurer.setBaseUrl('services/service-cashier');
                });

                service.changelogs = function (domainName, deposit, page) {
                    return config.all('cashier').one('dm').one('changelogs').get({
                        deposit: deposit,
                        domainName: domainName,
                        p: page
                    });
                }

                return service;
            } catch (err) {
                console.error(err);
                throw err;
            }
        }
    ]);