'use strict';

angular.module('lithium').factory('GameStudioRest', ['Restangular',
    function(Restangular) {
        try {
            var service = {};
            var config = function(domainName) {
                return Restangular.withConfig(function(RestangularConfigurer) {
                    RestangularConfigurer.setBaseUrl('services/service-games/backoffice/' + domainName + '/');
                });
            }

            service.findGameStudio = function(domainName, id) {
                return config(domainName).one('game-studio', id).post();
            }

            service.update = function(domainName, id, update) {
                return config(domainName).one('game-studio', id).customPUT(update);
            }

            service.findByDomain = function(domainName) {
                return config(domainName).all('game-studio').all('find-by-domain').post();
            }

            service.add = function(domainName, gameStudio) {
                return config(domainName).all('game-studio').all('add').post(gameStudio);
            }

            service.changelogs = function(domainName, entityId, page) {
                return config(domainName).all('game-studio').one(entityId+'').all('changelogs').post('', { p: page });
            }

            return service;
        } catch (err) {
            console.error(err);
            throw err;
        }
    }
]);
