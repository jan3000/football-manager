'use strict';

var controllers = angular.module("controllers", []);
var app = angular.module('app', [
    'ngRoute',
    'controllers'
]);


app.config(['$routeProvider',
    function ($routeProvider) {
        $routeProvider
            .when('/', {
                templateUrl: 'partials/index.html',
                controller: 'AppCtrl'
            })
            .when('/team', {
                templateUrl: 'partials/team.html',
                controller: 'TeamCtrl'
            })
            .when('/timeTable', {
                templateUrl: 'partials/timeTable.html',
                controller: 'TimeTableCtrl'
            })
            .when('/statistics', {
                templateUrl: 'partials/statistics.html',
                controller: 'StatisticsCtrl'
            })
            .when('/go', {
                templateUrl: 'partials/runMatchDay.html',
                controller: 'RunMatchDayCtrl'
            })
            .otherwise({
                redirectTo: '/'
            });
    }]);