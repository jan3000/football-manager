var module = angular.module('controllers');

module.controller('RunMatchDayCtrl', function ($scope, $http, $log, $timeout, TimeTableService) {

    function isMatchDayFinished(matchDays) {
        var runningMatches = _.find(matchDays, function (matchDay) {
            return !matchDay.finished;
        });
        $log.log('runningMatches: ' + JSON.stringify(runningMatches));
        return _.isEmpty(runningMatches);
    }

    $scope.runNextMatchDay = function () {
        $http.get('rest/home/runNextMatchDayMinute/').then(function (result) {
            $log.log('runNextMatchDayMinute: ' + JSON.stringify(result));
            TimeTableService.setCurrentMatchDay(result.data.matchDayNumber);
            $scope.matches = result.data.matches;
            if (!isMatchDayFinished($scope.matches)) {
                $log.log('timeout!');
                $timeout($scope.runNextMatchDay, 100);
            } else {
                $log.log('match day finished')
            }
        })
    };
    $scope.runNextMatchDay();

});