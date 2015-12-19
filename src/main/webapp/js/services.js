'use strict';
var services = angular.module('services');

services.factory('TimeTableService', function () {

    var currentMatchDay = 1;

    return {
        getCurrentMatchDay: function () {
            return currentMatchDay;
        },
        setCurrentMatchDay: function (matchDay) {
            currentMatchDay = matchDay;
        }
    }
});

services.factory('TeamService', function ($http, $log) {

    var teams;
    var getTeams = function () {
        $http.get('rest/home/teams').then(function (result) {
            $log.log('rest/home/teams: ' + JSON.stringify(result.data));
            teams = result.data;
        });
    }();

    return {
        getTeams: function () {
            $log.log('getTeams');
            return teams;
        }
    }
});