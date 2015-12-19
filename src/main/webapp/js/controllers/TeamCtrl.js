'use strict';

var controllers = angular.module("controllers");

controllers.controller("TeamCtrl", function ($scope, $log, TeamService) {

    $scope.teams = TeamService.getTeams();
});