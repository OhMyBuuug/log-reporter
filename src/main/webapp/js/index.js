/**
 * Created by SiGen on 2016/12/14.
 */
$(function () {
    // 格式化对象
    function parseObject(stringData) {
        return (new Function("return" + stringData))();
    }

    //建立到指定 url 的 websocket
    // var websocket = new WebSocket("ws://10.10.8.82:8380/log-reporter/reporter");
    var websocket = new WebSocket("ws://127.0.0.1:8080/reporter");

    // websocket.onopen = function () {
    //     // alert("Connected successfully");
    // };
    // websocket.onerror = function () {
    //     alert("Error");
    // };
    // websocket.onclose = function () {
    //     alert("Closed");
    // }

    // 接收到信息后执行
    websocket.onmessage = function (message) {
        // 将接收到的信息格式化
        var data = parseObject(message.data);
        // 接收服务器端的实时日志并添加到HTML页面中
        $("#logs").append("<br><span style='display:inline-block;white-space: nowrap'>" + data.text + "</span>");
        // 滚动条滚动到最底部
        $("#log-container").scrollTop(
            $("#logs").height() - $("#log-container").height()
        );
    }
});