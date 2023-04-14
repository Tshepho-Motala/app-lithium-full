'use strict';

angular.module('lithium').factory('GameSuppliersRest', ['Restangular',
    function(Restangular) {
        try {
            var service = {};
            var config = function(domainName) {
                return Restangular.withConfig(function(RestangularConfigurer) {
                    RestangularConfigurer.setBaseUrl('services/service-games/backoffice/' + domainName + '/');
                });
            }

            service.get = function(domainName, id) {
                return config(domainName).one('game-supplier', id).get();
            }

            service.update = function(domainName, id, update) {
                return config(domainName).one('game-supplier', id).all('update').post(update);
            }

            service.findByDomain = function(domainName) {
                return config(domainName).all('game-suppliers').all('find-by-domain').getList();
            }

            service.add = function(domainName, gameSupplier) {
                return config(domainName).all('game-suppliers').all('add').post(gameSupplier);
            }

            service.changelogs = function(domainName, entityId, page) {
                return config(domainName).all('game-supplier').one(entityId+'').one('changelogs').get({ p: page });
            }

            return service;
        } catch (err) {
            console.error(err);
            throw err;
        }
    }
]);
