package com.common.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** redission分布式锁自动释放锁的时间间隔
 * @author rdexpense
 * @date 2020/09/09 22:10
 */
@Component
public class LockTimeOutUtil {

    public static int Time;

    @Value("${lock.time.out}")
    public void setTime(int Time) {
        this.Time = Time;
    }
}
