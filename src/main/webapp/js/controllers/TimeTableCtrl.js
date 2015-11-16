'use strict';
var controllers = angular.module("controllers");

controllers.controller("TimeTableCtrl", function ($scope, $http, $log) {

    $scope.matchDayNumber = 1;

    $scope.getPreviousMatchDay = function () {
        $scope.matchDayNumber--;
        $scope.getMatchDay($scope.matchDayNumber);

    };

    $scope.getNextMatchDay = function () {
        $log.log($scope.matchDayNumber);
        $scope.matchDayNumber++;

        $log.log($scope.matchDayNumber);
        $scope.getMatchDay($scope.matchDayNumber);
    };

    $scope.getMatchDay = function (matchDay) {
        $log.log('getMatchDay: ' + matchDay);
        $http.get('rest/home/timeTable/' + matchDay).then(function (result) {
            $log.log('getMatchDay: ' + JSON.stringify(result))
            $scope.matchDayNumber = result.data.matchDayNumber;
            $log.log('$scope.matchDayNumber: ' + $scope.matchDayNumber);
            $scope.matches = result.data.matches;
        });
    };
    $scope.getMatchDay(1);

    $scope.runNextMatchDay = function () {
        $http.get('rest/home/runNextMatchDay/').then(function (result) {
            $log.log('runNextMatchDay: ' + JSON.stringify(result))

        })
    }
});