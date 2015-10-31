'use strict';
var controllers = angular.module("controllers");

controllers.controller("TimeTableCtrl", function ($scope, $http, $log) {

    $scope.matchDayNumber = 0;

    $scope.getNextMatchDay = function () {
        $scope.matchDayNumber++;
        $log.log('getNextMatchDay: ' + $scope.matchDayNumber)
        $http.get('rest/home/timeTable/' + $scope.matchDayNumber).then(function (result) {
            $log.log('data: ' + JSON.stringify(result.data));
            $scope.matchDayNumber = result.data.matchDayNumber;
            $scope.matches = result.data.matches;
        });
    }
});