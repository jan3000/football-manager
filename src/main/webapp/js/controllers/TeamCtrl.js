'use strict';

var controllers = angular.module("controllers");

controllers.controller("TeamCtrl", function ($scope, $http, $log) {
    $http.get('rest/home/teams').then(function (result) {
        $log.log('data: ' + JSON.stringify(result.data));
        $scope.teams = result.data;

    });
});