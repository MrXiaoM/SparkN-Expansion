# SparkN-Expansion

提供比 [spark](https://github.com/lucko/spark/blob/master/spark-common/src/main/java/me/lucko/spark/common/util/SparkPlaceholder.java) 官方更细化的无格式数值变量

```
%sparkn_tps%
%sparkn_tps_<时间>%
%sparkn_tps_<时间>_<格式>%
%sparkn_mspt%
%sparkn_mspt_<时间>%
%sparkn_mspt_<时间>_<取值>_<格式>%
%sparkn_cpu_system%
%sparkn_cpu_system_<时间>%
%sparkn_cpu_system_<时间>_<格式>%
%sparkn_cpu_process%
%sparkn_cpu_process_<时间>%
%sparkn_cpu_process_<时间>_<格式>%
%sparkn_rawcpu_system%
%sparkn_rawcpu_system_<时间>%
%sparkn_rawcpu_system_<时间>_<格式>%
%sparkn_rawcpu_process%
%sparkn_rawcpu_process_<时间>%
%sparkn_rawcpu_process_<时间>_<格式>%
```

时间分别可取以下选项

| tps | mspt | cpu |
| --- | --- | --- |
| `5s` | `10s` | `10s` |
| `10s` | `1m` | `1m` |
| `1m` | `5m` | `15m` |
| `5m` |  |  |
| `15m` |  |  |

mspt 的取值可用以下选项
+ `min` 最小值
+ `mean` 平均值
+ `max` 最大值
+ `median` 中值
+ `95%` 在95%时间里的值

格式使用 [`String.format`](https://docs.oracle.com/javase/8/docs/api/java/lang/String.html#format-java.lang.String-java.lang.Object...-) 相同格式，如 `%.1f` 保留一位小数。  
由于变量特性，在此处可以使用全角的 `％` 来代替半角 `%`。

rawcpu 和 cpu 的区别是，rawcpu 的 `100%=1.0`，cpu 的 `100%=100.0`，是原始数值和百分比的区别。
