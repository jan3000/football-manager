'use strict';

var controllers = angular.module("controllers");

controllers.controller('StatisticsCtrl', function ($scope, $http, $log, TeamService) {

    var minuteLabel = [15, 30, 45, 60, 75, 90];

    $scope.teams = TeamService.getTeams();

    function reduceGoalsPerMinutes(goalsPerMinute) {
        var data = [];
        for (var i = 0; i < $scope.labels.length; i++) {
            var minute = $scope.labels[i];
            data.push(goalsPerMinute.slice(minute - 15, minute).reduce(function (previous, current) {
                return previous + current;
            }, 0));
        }
        return data;
    }

    $scope.getStatistics = function (team) {
        $http.get('rest/home/statistic/' + team).then(function (result) {
            $log.log('statistic/' + team + ': ' + JSON.stringify(result));
            $scope.labels = minuteLabel;
            $log.log('$scope.labels: ' + $scope.labels);
            var data = reduceGoalsPerMinutes(result.data.totalGoals);
            $scope.data = [data];
            $log.log('$scope.data: ' + $scope.data)
        })
    };

});