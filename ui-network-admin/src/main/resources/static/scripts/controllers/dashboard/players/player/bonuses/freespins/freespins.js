'use strict';

angular.module('lithium')
    .controller('PlayerBonusesFreeSpinsController', ['user', 'bonusCodes', 'currencySymbol',
        function(user, bonusCodes, currencySymbol) {
            var controller = this;
            controller.data = { user: user, bonusCodes: bonusCodes, currencySymbol: currencySymbol};
        }]);
