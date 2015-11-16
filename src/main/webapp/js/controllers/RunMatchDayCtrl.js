var module = angular.module('controllers');

module.controller('RunMatchDayCtrl', function ($scope, $http, $log) {


    $scope.runNextMatchDay = function () {
        $http.get('rest/home/runNextMatchDay/').then(function (result) {
            $log.log('runNextMatchDay: ' + JSON.stringify(result))
            $scope.matches = result.data.matches;
        })
    };
    $scope.runNextMatchDay();

});