'use strict';


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
                controller: 'HomeController'
            })
            .when('/timeTable', {
                templateUrl: 'partials/timeTable.html',
                controller: 'HomeController'
            })
            .when('/statistics', {
                templateUrl: 'partials/statistics.html',
                controller: 'HomeController'
            })
            .otherwise({
                redirectTo: '/'
            });
    }]);