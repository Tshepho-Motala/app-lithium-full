'use strict';

var app = angular.module("lithiumRoxor", ["ngRoute"]);
app.config(function($routeProvider) {
   $routeProvider
       .when("/", {
          templateUrl: 'js/game/game.html',
          controller: 'RoxorController'
       })
       .otherwise({
          redirectTo: '/'
       });
});

app.controller('RoxorController', ['$routeParams', '$scope', '$sce',
 function($routeParams, $scope, $sce) {
    $scope.msg = "ROXOR2";
     $scope.url = $sce.trustAsResourceUrl($routeParams.url);
    console.log($routeParams, $scope.url);
 }
]);
