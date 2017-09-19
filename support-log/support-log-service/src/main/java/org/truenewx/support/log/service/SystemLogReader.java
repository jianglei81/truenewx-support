package org.truenewx.support.log.service;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;
import org.truenewx.support.log.data.model.SystemLogLine;

/**
 * 系统日志读取器
 *
 * @author jianglei
 * @since JDK 1.8
 */
@Service
public class SystemLogReader {

    public List<SystemLogLine> readLast(final File file, final int size) {
        final List<SystemLogLine> logs = new ArrayList<>();
        try {
            final RandomAccessFile raf = new RandomAccessFile(file, "r");
            long pos = raf.length() - 1;
            if (pos > 2) { // 不能是空文件，空文件至少有2个字节内容
                while (pos > 0) {
                    raf.seek(pos);
                    if (raf.readByte() == '\n') {
                        final String line = raf.readLine();
                        if (line != null) { // 读到文件末尾可能读到null
                            logs.add(new SystemLogLine(pos, line));
                            if (logs.size() >= size) {
                                break;
                            }
                        }
                    }
                    pos--;
                }
                // 最后读取第一行
                if (logs.size() < size) {
                    raf.seek(pos);
                    logs.add(new SystemLogLine(pos, raf.readLine()));
                }
            }
            raf.close();
        } catch (final IOException e) {
            // 忽略IO异常
        }
        Collections.reverse(logs); // 因为从后往前读文件，所以需要反转读取到的数据行清单
        return logs;
    }

    public List<SystemLogLine> readAfter(final File file, final long minPostion) {
        final List<SystemLogLine> logs = new ArrayList<>();
        try {
            final RandomAccessFile raf = new RandomAccessFile(file, "r");
            long pos = minPostion + 1; // 从最小位置的后一位开始查找
            while (pos < raf.length()) {
                raf.seek(pos);
                if (raf.readByte() == '\n') {
                    final String line = raf.readLine();
                    if (line != null) { // 读到文件末尾可能读到null
                        logs.add(new SystemLogLine(pos, line));
                        pos += line.length();
                    } else {
                        pos++;
                    }
                } else {
                    pos++;
                }
            }
            raf.close();
        } catch (final IOException e) {
            // 忽略IO异常
        }
        return logs;
    }

    public List<SystemLogLine> readBefore(final File file, final long maxPosition, final int size) {
        final List<SystemLogLine> logs = new ArrayList<>();
        try {
            final RandomAccessFile raf = new RandomAccessFile(file, "r");
            long pos = maxPosition - 1; // 从最小位置的前一位开始查找
            while (pos > 0) {
                raf.seek(pos);
                if (raf.readByte() == '\n') {
                    final String line = raf.readLine();
                    if (line != null) { // 读到文件末尾可能读到null
                        logs.add(new SystemLogLine(pos, line));
                        if (logs.size() >= size) {
                            break;
                        }
                    }
                }
                pos--;
            }
            // 最后读取第一行
            if (logs.size() < size) {
                raf.seek(pos);
                logs.add(new SystemLogLine(pos, raf.readLine()));
            }
            raf.close();
        } catch (final IOException e) {
            // 忽略IO异常
        }
        Collections.reverse(logs); // 因为从后往前读文件，所以需要反转读取到的数据行清单
        return logs;
    }

}
