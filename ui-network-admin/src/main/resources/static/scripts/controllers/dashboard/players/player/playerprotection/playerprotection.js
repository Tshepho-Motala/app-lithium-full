'use strict'

angular.module('lithium').controller('PlayerProtectionController', ["$q", "$timeout", "$state","$scope", '$translate', "$rootScope",
    function() {
        window.VuePluginRegistry.loadByPage("PlayerProtection")
    }
]);