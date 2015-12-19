'use strict';

var controllers = angular.module("controllers");

controllers.controller('StatisticsCtrl', function ($scope, TeamService) {

    $scope.teams = TeamService.getTeams();
});