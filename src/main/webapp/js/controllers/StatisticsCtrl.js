'use strict';

var controllers = angular.module("controllers");

controllers.controller('StatisticsCtrl', function ($scope, $http, $log, TeamService) {

    $scope.teams = TeamService.getTeams();

    $scope.getStatistics = function (team) {
        $http.get('rest/home/statistic/' + team).then(function (result) {
            $log.log('statistic/' + team + ': ' + JSON.stringify(result));
            $scope.labels = _.range(90);
            $log.log('$scope.labels: ' + $scope.labels);
            $scope.data = result.data.homeGoals;
        })
    };

    //$scope.labels = ['2006', '2007', '2008', '2009', '2010', '2011', '2012'];
    //$scope.labels = [2006, 2007, 2008, 2009, 2010, 2011, 2012];
    //$scope.series = ['Series A', 'Series B'];
    $scope.series = ['Series A'];
    //$scope.data = [
    //    [65, 59, 80, 81, 56, 55, 40],
    //    [28, 48, 40, 19, 86, 27, 90]
    //];

});