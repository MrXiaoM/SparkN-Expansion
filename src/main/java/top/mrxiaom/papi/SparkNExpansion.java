package top.mrxiaom.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.lucko.spark.api.Spark;
import me.lucko.spark.api.SparkProvider;
import me.lucko.spark.api.statistic.StatisticWindow.TicksPerSecond;
import me.lucko.spark.api.statistic.StatisticWindow.MillisPerTick;
import me.lucko.spark.api.statistic.StatisticWindow.CpuUsage;
import me.lucko.spark.api.statistic.misc.DoubleAverageInfo;
import me.lucko.spark.api.statistic.types.DoubleStatistic;
import me.lucko.spark.api.statistic.types.GenericStatistic;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"unused"})
public class SparkNExpansion extends PlaceholderExpansion {
    @NotNull
    @Override
    public String getIdentifier() {
        return "sparkn";
    }

    @NotNull
    @Override
    public String getAuthor() {
        return "MrXiaoM";
    }

    @NotNull
    @Override
    public String getVersion() {
        return "1.0.0";
    }

    private static void i(Runnable runnable) {
        try {
            runnable.run();
        } catch (Throwable ignored) {
        }
    }

    Spark spark;
    Map<String, TicksPerSecond> ticksPerSecondMap = new HashMap<String, TicksPerSecond>() {{
        i(() -> put("5s", TicksPerSecond.SECONDS_5));
        i(() -> put("10s", TicksPerSecond.SECONDS_10));
        i(() -> put("1m", TicksPerSecond.MINUTES_1));
        i(() -> put("5m", TicksPerSecond.MINUTES_5));
        i(() -> put("15m", TicksPerSecond.MINUTES_15));
    }};
    Map<String, MillisPerTick> millisPerTickMap = new HashMap<String, MillisPerTick>() {{
        i(() -> put("10s", MillisPerTick.SECONDS_10));
        i(() -> put("1m", MillisPerTick.MINUTES_1));
        i(() -> put("5m", MillisPerTick.MINUTES_5));
    }};
    Map<String, CpuUsage> cpuUsageMap = new HashMap<String, CpuUsage>() {{
        i(() -> put("10s", CpuUsage.SECONDS_10));
        i(() -> put("1m", CpuUsage.MINUTES_1));
        i(() -> put("15m", CpuUsage.MINUTES_15));
    }};

    @Override
    public boolean register() {
        this.spark = SparkProvider.get();
        return super.register();
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        return onRequest(player, params);
    }

    @Override
    public String onRequest(OfflinePlayer p, @NotNull String params) {
        String[] args = params.split("_");
        if (args.length >= 1) {
            if ("tps".equalsIgnoreCase(args[0])) {
                TicksPerSecond time = ticksPerSecondMap.get(args.length >= 2 ? args[1] : "10s");
                if (time == null) return null;
                String format = args.length >= 3 ? args[2].replace("％", "%") : "%.1f";
                DoubleStatistic<TicksPerSecond> tps = spark.tps();
                if (tps == null) return null;
                double raw = tps.poll(time);
                return String.format(format, raw);
            }
            if ("mspt".equalsIgnoreCase(args[0]) || "tickduration".equalsIgnoreCase(args[0])) {
                MillisPerTick time = millisPerTickMap.get(args.length >= 2 ? args[1] : "10s");
                if (time == null) return null;
                String type = args.length >= 3 ? args[2].toLowerCase() : "mean";
                String format = args.length >= 4 ? args[3].replace("％", "%") : "%.1f";
                GenericStatistic<DoubleAverageInfo, MillisPerTick> mspt = spark.mspt();
                if (mspt == null) return null;
                DoubleAverageInfo info = mspt.poll(time);
                double raw;
                switch (type) {
                    case "min":
                        raw = info.min();
                        break;
                    case "mean":
                        raw = info.mean();
                        break;
                    case "max":
                        raw = info.max();
                        break;
                    case "median":
                        raw = info.median();
                        break;
                    case "95%":
                        raw = info.percentile95th();
                        break;
                    default:
                        return null;
                }
                return String.format(format, raw);
            }
            boolean rawCpu = "rawcpu".equalsIgnoreCase(args[0]);
            if (("cpu".equalsIgnoreCase(args[0]) || rawCpu) && args.length >= 2) {
                DoubleStatistic<CpuUsage> cpu =
                        "system".equalsIgnoreCase(args[1]) ? spark.cpuSystem()
                                : "process".equalsIgnoreCase(args[1]) ? spark.cpuProcess()
                                : null;
                if (cpu == null) return null;
                CpuUsage time = cpuUsageMap.get(args.length >= 3 ? args[2] : "10s");
                if (time == null) return null;
                String format = args.length >= 4 ? args[3].replace("％", "%") : (rawCpu ? "%.2f" : "%.1f%%");
                double raw;
                if (rawCpu) {
                    raw = cpu.poll(time);
                } else {
                    raw = cpu.poll(time) * 100.0;
                }
                return String.format(format, raw);
            }
        }
        return null;
    }

    public static int parseInt(String s, int def) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return def;
        }
    }
}
