'use strict';

var controllers = angular.module("controllers");

controllers.controller("TeamDetailsCtrl", function ($scope, $log, TeamService) {

    $scope.teams = TeamService.getTeams();
});