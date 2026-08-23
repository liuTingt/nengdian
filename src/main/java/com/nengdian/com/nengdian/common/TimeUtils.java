package com.nengdian.com.nengdian.common;

import java.time.Duration;
import java.time.LocalDateTime;

public class TimeUtils {
    /**
     * 计算当前时间与 updateTime 的差值，并格式化返回
     * @param updateTime 更新时间
     * @return 格式化后的时间差字符串
     */
    public static String formatTimeAgo(LocalDateTime updateTime) {
        if (updateTime == null) {
            return "";
        }

        // 1. 计算当前时间与 updateTime 之间的时间差
        Duration duration = Duration.between(updateTime, LocalDateTime.now());

        // 获取总秒数（如果是未来时间，秒数可能为负，这里取绝对值或做判断）
        long totalSeconds = duration.getSeconds();

        if (totalSeconds < 0) {
            return "刚刚"; // 或者处理未来时间的逻辑
        }

        // 2. 根据秒数判断显示格式
        if (totalSeconds < 60) {
            return totalSeconds + "秒";
        } else if (totalSeconds < 3600) {
            // 大于等于60秒，转换为分钟
            return duration.toMinutes() + "分钟";
        } else if (totalSeconds < 86400) {
            return duration.toHours() + "小时";
        } else {
            return duration.toDays() + "天";
        }
    }

    public static void main(String[] args) {
        LocalDateTime time0 = LocalDateTime.now();
        System.out.println(TimeUtils.formatTimeAgo(time0)); // 输出: 30秒前

        LocalDateTime time1 = LocalDateTime.now().minusSeconds(30);
        System.out.println(TimeUtils.formatTimeAgo(time1)); // 输出: 30秒前

        LocalDateTime time2 = LocalDateTime.now().minusMinutes(5);
        System.out.println(TimeUtils.formatTimeAgo(time2)); // 输出: 5分钟前

        LocalDateTime time3 = LocalDateTime.now().minusSeconds(90);
        System.out.println(TimeUtils.formatTimeAgo(time3)); // 输出: 1分钟前
    }
}
