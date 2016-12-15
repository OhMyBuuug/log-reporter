package cn.yk.reporter;
// test for github
// test for github 2
// test for github 3
// test for github 4
// test for github 5
// test for github 6
import javax.websocket.Session;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by SiGen on 2016/12/14.
 *
 * 读取文件实体类
 */
public class LogReader extends Thread {
    // 获取 log 文件
    private File logFile;
    // 判断是否 继续读取 的标志
    private boolean tailing = false;
    // 获取 LogReporter 的 session 对象
    private Session session;
    // 获得当前系统时间
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    // 读取 log 文件
    private RandomAccessFile randomAccessFile;

    /**
     * LogReader构造器
     *
     * @param logFile 得到需读取的文件
     * @param tailing 设定是否立即开始执行按行读取文件到websocket的flag
     * @param session 得到当前websocket的session
     */
    public LogReader(File logFile, boolean tailing, Session session) {
        this.logFile = logFile;
        this.tailing = tailing;
        this.session = session;
    }

    @Override
    public void run() {
        // 初始化 RandomAccessFile 读取文件时的指针
        long filePointer = 0;

        try {
            // 以只读方式读取 logfile
            randomAccessFile = new RandomAccessFile(logFile, "r");

            // 当 logfile 字节数大于 10,000 时，只读取文件的最后 10,000 个字节
            if (this.logFile.length() > 100000) {
                filePointer = this.logFile.length() - 100000;
            }

            //当 tailing 为 true 时 ，执行 按行读取文件到 websocket 的任务
            while (this.tailing) {
                // 每当执行完依次循环，重新获得文件的大小，以供与 filepointer 比对
                long fileLength = this.logFile.length();

                // 当文件大小大于当前指针时，按行读取logFile
                if (fileLength > filePointer) {
                    // 找到 filepointer 对应文件的位置
                    randomAccessFile.seek(filePointer);
                    // 保存 当前读取到的行
                    String tempLine;
                    // 保存 转码为 UTF-8 后的当前读取到的行
                    String logLine;

                    do {
                        // 按行读取文件
                        tempLine = randomAccessFile.readLine();

                        // 若 读到的行不为 null ，则 转码并发送至 websocket
                        if (tempLine != null) {
                            // 将文件转码，防止乱码
                            logLine = new String(tempLine.getBytes("ISO-8859-1"), "utf-8");
                            System.out.println("[ " + simpleDateFormat.format(new Date()) + " ] " + logLine);

                            // 将 转码后的 log行 发送至 websocket
                            LogReporter.Report(logLine, this.session);
                        } else {
                            // 若 读到的行为 null ，则说明已经读取至文件尾部 ，跳出该 do...while...循环
                            break;
                        }
                    } while (true);

                    // 读取完成之后将 filePointer 赋值为 当前文件的大小
                    filePointer = randomAccessFile.getFilePointer();
                } else if (fileLength == filePointer) {
                    // 当文件没有变化时，等待两秒
                    sleep(2000);
                } else if (fileLength < filePointer) {
                    // 当 当前文件大小 小于 filePointer 时，说明文件内容被删除部分，将重置filePointer，以从头读取文件
                    randomAccessFile = new RandomAccessFile(logFile, "r");
                    filePointer = 0;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // 当任务执行结束后，释放读取文件的线程
                randomAccessFile.close();
                System.out.println("[ " + simpleDateFormat.format(new Date()) + " ] ->" + session.getId() + "已停止读取文件");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 设置 tailing
     *
     * @param tailing 是否继续读取的标志
     */
    public void setTailing(boolean tailing) {
        this.tailing = tailing;
    }
}
