'use strict';

var controllers = angular.module("controllers");

controllers.controller("TeamCtrl", function ($scope, $log, TeamService) {

    $scope.teams = TeamService.getTeams();

    $scope.getTeam = function (teamName) {
        console.log("getTeam!!!!!!!!!!!!!");
        for (var i = 0; i < $scope.teams.length; i++) {
            var team = $scope.teams[i];
            console.log("Teamy: " + team.name);
            if (team.name === teamName) {
                console.log("Teamy: " + team.name);
                $scope.selectedTeam = team;
                break
            }
        }
    };

    $scope.getPositionZone = function(position) {
        console.log('getPositionZone');
        switch(position) {
            case 'GOALY': return "green";
            case 'LEFT_STOPPER':
            case 'RIGHT_STOPPER':
            case 'LEFT_DEFENDER':
            case 'RIGHT_DEFENDER':
            case 'CENTRAL_STOPPER': return "limegreen";
            case 'CENTRAL_DEFENSIVE_MIDFIELDER':
            case 'LEFT_DEFENSIVE_MIDFIELDER':
            case 'RIGHT_DEFENSIVE_MIDFIELDER':
            case 'LEFT_MIDFIELDER':
            case 'LEFT_OFFENSIVE_MIDFIELDER':
            case 'CENTRAL_OFFENSIVE_MIDFIELDER':
            case 'RIGHT_OFFENSIVE_MIDFIELDER':
            case 'RIGHT_MIDFIELDER': return "greenyellow";
            case 'LEFT_WINGER':
            case 'RIGHT_WINGER':
            case 'LEFT_STRIKER':
            case 'CENTRAL_STRIKER':
            case 'RIGHT_STRIKER': return "plum";
            default: return "red"
        }
    }

    $scope.getPlayerDetails = function(position) {
        console.log('getPlayerDetails');
       // IMPLEMENT
    }
});