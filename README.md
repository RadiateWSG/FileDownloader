# FileDownloader
整体上参考了 OkHttp 的设计思路：FileDownloader 负责全局配置，Dispatcher 负责 task 调度，通过 Interceptor 和 Interceptor Chain 来分步实现 check、retry、connection、fetch 等流程。

时间上没有把握好，demo 比较简单，还有很多不足：比如 “暂停取消” 还在纠结怎么实现好。
还有一些点可以完善，比如 “优先级”、“大文件的分段下载”、“设置独立的进程”、“wifi预约” 等等。
