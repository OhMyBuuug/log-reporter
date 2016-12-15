package cn.yk.reporter;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by SiGen on 2016/12/14.
 *
 * 发送信息实体类
 */
@ServerEndpoint(value = "/reporter")

public class LogReporter {
    // 存储 所有的会话信息
    private static Map<String, Session> sessionMap = new Hashtable<String, Session>();
    // 持有 logReader 实例
    private LogReader logReader;
    // 获得当前系统时间
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    /**
     * 待读取的文件路径
     * "/deploy/tomcatForProvider/logs/catalina.out"
     * "C:\\apache-tomcat-8.0.30\\logs\\catalina.out"
     */
    private String logFileRoot = "C:\\apache-tomcat-8.0.30\\logs\\catalina.out";

    // websocket 连接成功时，记录 session 信息，并且 为其准备一个 logreader
    @OnOpen
    public void OnOpen(Session session) {
        // 记录 session 信息
        sessionMap.put(session.getId(), session);

        System.out.println("[ " + simpleDateFormat.format(new Date()) + " ] -> Welcome , " + session.getId());

        // 从 指定文件中获取内容，并开始读取，初始化session
        // "/deploy/tomcatForProvider/logs/catalina.out"
        // "C:\\apache-tomcat-8.0.30\\logs\\catalina.out"
        this.logReader = new LogReader(new File(logFileRoot), true, session);
        logReader.start();
    }

    // websocket 关闭连接时，清理 session 信息
    @OnClose
    public void OnClose(Session session, CloseReason closeReason) {
        System.out.println("[ " + simpleDateFormat.format(new Date()) + " ] -> " + session.getId() + " is closed beacuseof " + closeReason);
        // 清理信息
        Clear(session);
    }

    // websocket 连接错误时，记录错误，并清理 session 信息
    @OnError
    public void OnError(Session session, Throwable throwable) {
        System.err.println("[ " + simpleDateFormat.format(new Date()) + " ] -> " + session.getId() + " error : " + throwable.toString());
        // 清理信息
        Clear(session);
    }

    /**
     * 向指定 session 发送读取到的内容
     *
     * @param message 待发送的内容
     * @param session 待接收的目的会话
     */
    public static void Report(String message, Session session) {
        try {
            // 向 session 发送 信息
            session.getBasicRemote().sendText("{text :'" + message + "'}");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 清理信息
    public void Clear(Session session) {
        // 清理 session 信息
        sessionMap.remove(session.getId());
        // 通知 logReader 结束读取文件
        logReader.setTailing(false);

        System.out.println("[ " + simpleDateFormat.format(new Date()) + " ] -> " + session.getId() + " session 清理成功");
    }
}
