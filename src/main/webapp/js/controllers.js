'use strict';

var controllers = angular.module("controllers", []);

controllers.controller("HomeController", function ($scope, $http, $log) {
    $scope.title = 'Hello world!';
    $http.get('rest/home').then(function (result) {
        $log.log('11111111 ' + JSON.stringify(result))
        $scope.title += result;
    })
});