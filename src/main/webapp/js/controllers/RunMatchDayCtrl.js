var module = angular.module('controllers');

module.controller('RunMatchDayCtrl', function ($scope, $http, $log, $timeout, TimeTableService) {

    $scope.matchDayNumber = TimeTableService.getCurrentMatchDay();

    function isMatchDayFinished(matchDays) {
        var runningMatches = _.find(matchDays, function (matchDay) {
            return !matchDay.finished;
        });
        $log.log('runningMatches: ' + JSON.stringify(runningMatches));
        return _.isEmpty(runningMatches);
    }

    $scope.runNextMatchDay = function () {
        $http.get('rest/home/setNextMatchDayToRunnable/').then(function (result) {

            $log.log('setNextMatchDayToRunnable!!!!' + JSON.stringify(result));

            $http.get('rest/home/runNextMatchDayMinute/').then(function (result) {
                $log.log('runNextMatchDayMinute: ' + JSON.stringify(result.data.matches[1]));
                TimeTableService.setCurrentMatchDay(result.data.matchDayNumber);
                $scope.matches = result.data.matches;
                $scope.matchDayNumber = result.data.matchDayNumber;
                if (!isMatchDayFinished($scope.matches)) {
                    $log.log('timeout!');
                    $timeout($scope.runNextMatchDay, 100);
                } else {
                    $log.log('match day finished')
                }
            })
        });

    };
    $scope.runNextMatchDay();

});