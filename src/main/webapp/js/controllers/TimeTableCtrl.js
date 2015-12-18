'use strict';
var controllers = angular.module("controllers");

controllers.controller("TimeTableCtrl", function ($scope, $http, $log, TimeTableService) {

    $scope.shownMatchDay = TimeTableService.getCurrentMatchDay();

    $scope.getCurrentMatchDay = function () {
        return TimeTableService.getCurrentMatchDay();
    };
    
    $scope.getPreviousMatchDay = function () {
        if ($scope.shownMatchDay > 1) {
            $scope.shownMatchDay--;
        }
        getMatchDay($scope.shownMatchDay);
    };

    $scope.getNextMatchDay = function () {
        $scope.shownMatchDay++;
        getMatchDay($scope.shownMatchDay);
    };

    var getMatchDay = function (matchDay) {
        $log.log('getMatchDay: ' + matchDay);
        $http.get('rest/home/timeTable/' + matchDay).then(function (result) {
            $log.log('getMatchDay: ' + JSON.stringify(result));
            $scope.shownMatchDay = result.data.matchDayNumber;
            $log.log('$scope.shownMatchDay: ' + $scope.shownMatchDay);
            $scope.matches = result.data.matches;
        });
    };
    getMatchDay($scope.shownMatchDay);

    $scope.getCurrentTable = function () {
        $http.get('rest/home/currentTable/').then(function (result) {
            $log.log('getCurrentTable: ' + JSON.stringify(result));
            $scope.table = result.data;
        })
    };
    $scope.getCurrentTable();
});