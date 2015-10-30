'use strict';

var controllers = angular.module("controllers", []);

controllers.controller("HomeController", function ($scope, $http, $log) {
    $scope.title = 'Hello football!';
    $scope.teams;
    $scope.timeTable;
    $http.get('rest/home/teams').then(function (result) {
        $log.log('data: ' + JSON.stringify(result.data))
        $scope.teams = result.data;
        $http.get('rest/home/timeTable').then(function (result) {
            $log.log('data: ' + JSON.stringify(result.data))
            $scope.timeTable = result.data;
    });

    });
});