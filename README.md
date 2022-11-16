## frame-back
研发费用辅助管理系统_后端

###GIT源码地址
地址：http://192.168.40.200/rdexpensegroup/rdexpense-back

##一:项目模块
1.rdexpense-common：通用模块，包括通用工具类、数据库及服务层各种接口定义等;
  1.1.com.common.base包定义了数据库、异常处理、线程池等信息；
  1.2.com.common.entity包定义了入参和出参等接口相应
  1.3.com.common.util包定义了常用的一些工具类
  
2.rdexpense-manager：业务模块，包含系统的主要业务实现代码(开发人员主要再次模块里编写代码);
  2.1.com.rdexpense.manager.config包定义了swagger配置、web拦截、rabbitMq配置、Druid连接池配置等
  2.2.com.rdexpense.manager.controller包是业务的control层的代码逻辑
  2.3.com.rdexpense.manager.dto包定义了入参和出参的dto文件
  2.5.com.rdexpense.manager.service包定义了业务的service层的代码逻辑
  2.6.com.rdexpense.manager.util包定义了常用的一些工具类

3.rdexpense-redis：redis配置模块;

##二：框架中集成的技术点
1、redis缓存，相关配置及方法rdexpense-redis模块中
2、批量插入sql，使用batchInsert方法时
3、导出word及PDF，参照RingDailyPaperController类的exportContract()
4、POI导入、导出excel，参照RiskDataController类的upload()及exportExcel()方法
5、对象存储，相关配置在.properties文件中
6、流水号各模块唯一，调用SequenceUtil类中的generateSerialNo()方法，区别在于是否有传参，
    不传参表示流水号全局唯一，传参表示每个模块唯一
7、swragger2 + knife4j



##三:配置文件(数据库、redis配置等)
1.dev:本地开发环境配置文件
2.test:公司内网测试环境配置文件
3.prod:金山云正式环境配置文件

##四：系统涉及到的工具类
1、AESUtil：AES的加密和解密
2、CalDaysUtils：计算两个时间相差几小时，获取当前日期或指定日期的前N天的日期集合等方法
3、AwsUtil：AWS对象存储使用的工具类
4、Base64Util：Base64加密和解密等
5、BaseUtil：基础对象功能帮助类
6、BigDecimalUtil：BigDecimal精确计算工具类
7、CheckParameter：校验接口参数通用类
8、CommonConfigUtil：对象功能常用工具类
9、ConstantMsgUtil：系统枚举类值域
10、ConstantValUtil：系统常用量值域
11、FlowIsAbleEnum：工作流是否启用枚举类
12、GenderEnum：男女性别枚举类
13、ErrorCodeEnum：系统错误CODE枚举类
14、CreateNoUtil：系统里生成编号工具类
15、DateUtil：日期处理工具类
16、DownloadUtil：文件下载的工具类
17、ExcelUtil：poi导出excel工具类
18、FileUtil：文件上传、删除、获取路径等工具类
19、HttpGetWithEntity：http请求体构造类
20、HTTPURL：http工具类
21、HttpUtils：通用http发送方法
22、IpAddressUtil：获取ip工具类
23、JodaTimeUtil：日期转化工具类
24、JsonUtil：JSON对象帮助类
25、JwtTokenUtil：token生成/验证帮助类
26、ListHandleUtil：将一个list分割为多个list方法类
27、log4jUtil：加载配置log4j日志
28、MD5：MD5处理类
29、MessagesUtil：发送短信共通类
30、Mobile3EleAPICheckUtil：运营商API接口验证帮助类
31、NettyUtil：odb解析工具类
32、PDFUtil：导出PDF工具类
33、PingYinUtils：获取汉字的拼音工具类
34、PropertyUtil：字符转化成驼峰格式的方法
35、ReflectHelper：对象反射工具类
36、RSAUtils：RSA加密解密工具类
37、SmsUtil：发送短信共通类
38、SourceHanFontUtil：思源字体工具类
39、SpringUtil：Spring获取bean工具类
40、ToolsUtils：常用工具类
41、xwpfTUtil：xwpf工具类
42、UrlConnectionUtil：浏览器浏览网页工具类
43、BaiduUtils：调用百度云人脸库接口工具类
44、Word2pdfUtil：输出pdf文件工具类
45、Doc2Pdf：导出word转成pdf工具类
46、HttpInvoker：http请求工具类
47、UploadMediaToWeiXinServe：上传文件到微信服务器工具类
48、WeChatUtil：推送文件到企业微信工具类
49、WeChatAccessTokenUtil：企业微信认证，获取access_token工具类
50、UUIDUtil：生成UUID工具类
51、SerialNumberUtil：基于redis实现序号的生成
52、SequenceUtil：基于redis实现流水号的生成
53、ChineseYuanUtil：数字转换成中文大写工具类
54、DealStatus:操作日志状态码
55、DingDingTalkUtil：对接钉钉考勤工具类
56、LockTimeOutUtil：redission分布式锁自动释放锁的时间间隔
57、DateCheckUtil：日期判断工具类
58、CheckTimeUtil：开始和结束日期的校验并加上时分秒
59、PageHelperUtils：人工实现分页工具类
60、OutRequestUtil：请求外部系统通用方法
61、ExportWordHelper：导出word工具类
62、ThreadPoolConfigUtil：获取线程池配置的工具类
63、EasyExcelUtil：easyExcel导入导出工具类