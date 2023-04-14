'use strict';

angular.module('lithium-roxor')
.controller('RoxorController', ['$routeParams',
    function($routeParams) {
        this.msg = "ROXOR2";
        console.log($routeParams);
    //http://localhost:9001/service-casino-provider-roxor/#?url=http://www.google.co.za&sound=true&homeButton=true
    }
]);