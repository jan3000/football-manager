'use strict';

var controllers = angular.module("controllers", []);

controllers.controller('AppCtrl', function ($scope, $location, $log) {
    $scope.changeView = function (view) {
        $location.path(view);
    };

    $scope.isActive = function (view) {
        return $location.path().indexOf(view) > -1;
    }
});


