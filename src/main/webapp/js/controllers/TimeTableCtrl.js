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
            $scope.getTable(matchDay);
            $log.log('getMatchDay: ' + JSON.stringify(result));
            $scope.shownMatchDay = result.data.matchDayNumber;
            $scope.matchDate = result.data.date;
            //$log.log('$scope.shownMatchDay: ' + $scope.shownMatchDay);
            $scope.matches = result.data.matches;
        });
    };
    getMatchDay($scope.shownMatchDay);

    //$scope.getCurrentTable = function () {
    //    $http.get('rest/home/currentTable/').then(function (result) {
    //        $log.log('getCurrentTable: ' + JSON.stringify(result));
    //        $scope.table = result.data;
    //    })
    //};


    $scope.getTable = function (day) {
        $http.get('rest/home/table/' + day).then(function (result) {
            //$log.log('getTable for day: ' + JSON.stringify(result));
            $scope.table = result.data;
        })
    };
    //$scope.getTable($scope.shownMatchDay);

    $scope.getScorers = function () {
        $http.get('rest/home/statistics/league').then(function (result) {
            $log.log('scorers: ' + JSON.stringify(result));
            $scope.scorers = result.data;
        })
    }()
});