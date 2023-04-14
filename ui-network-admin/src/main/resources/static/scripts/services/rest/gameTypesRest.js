'use strict';

angular.module('lithium').factory('GameTypesRest', ['Restangular',
    function(Restangular) {
        try {
            var service = {};
            var config = function(domainName) {
                return Restangular.withConfig(function(RestangularConfigurer) {
                    RestangularConfigurer.setBaseUrl('services/service-games/backoffice/' + domainName + '/');
                });
            }

            service.get = function(domainName, id) {
                return config(domainName).one('game-types', id).get();
            }

            service.update = function(domainName, id, update) {
                return config(domainName).one('game-types', id).customPUT(update);
            }

            service.delete = function(domainName, id) {
                return config(domainName).all('game-types').customDELETE(id);
            }

            service.findByDomain = function(domainName) {
                return config(domainName).all('game-types').all('find-by-domain').getList();
            }

            service.findByDomainAndType = function (domainName, type) {
                return config(domainName).all('game-types').customGET('find-by-domain-and-type', {type: type});
            }

            service.add = function(domainName, gameType) {
                return config(domainName).all('game-types').all('add').post(gameType);
            }

            service.changelogs = function(domainName, entityId, page) {
                return config(domainName).all('game-types').one(entityId+'').one('changelogs').get({ p: page });
            }

            return service;
        } catch (err) {
            console.error(err);
            throw err;
        }
    }
]);
