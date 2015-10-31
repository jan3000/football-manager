'use strict';

var controllers = angular.module("controllers", []);

controllers.controller('AppCtrl', function ($scope, $location, $log) {
    $scope.changeView = function (view) {
        $location.path(view);
    };

    $scope.isActive = function (view) {
        return $location.path().indexOf(view) > -1;
    }
});

controllers.controller("HomeController", function ($scope, $http, $log) {
    $http.get('rest/home/teams').then(function (result) {
        $log.log('data: ' + JSON.stringify(result.data));
        $scope.teams = result.data;

    });


    $scope.getNextMatchDay = function () {
        $http.get('rest/home/timeTable/1').then(function (result) {
            $log.log('data: ' + JSON.stringify(result.data));
            $scope.timeTable = result.data;
        });
    }
});