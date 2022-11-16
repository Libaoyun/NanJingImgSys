package com.rdexpense.manager.util;


import net.contentobjects.jnotify.JNotify;
import net.contentobjects.jnotify.JNotifyAdapter;
import net.contentobjects.jnotify.JNotifyException;

import java.text.SimpleDateFormat;
import java.util.Date;



public class FileMonitor extends JNotifyAdapter {
    // 需要监听的文件路径地址
    String path = "E:/tmp";
    /** 关注目录的事件 */
    int mask = JNotify.FILE_CREATED;// | JNotify.FILE_DELETED | JNotify.FILE_MODIFIED | JNotify.FILE_RENAMED;
    /** 是否监视子目录，即级联监视 */
    boolean watchSubtree = true;
    /** 监听程序Id */
    public int watchID;

    public static void main(String[] args) {
        new FileMonitor().beginWatch();
    }
    /**
     * 容器启动时启动监视程序
     *
     * @return
     */
    public void beginWatch() {
        /** 添加到监视队列中 */
        try {
            this.watchID = JNotify.addWatch(path, mask, watchSubtree, this);
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
            System.out.println("开始文件监听:" + df.format(new Date()) + "\t 文件目录:" + path);
        } catch (JNotifyException e) {
            e.printStackTrace();
        }
        // 死循环，线程一直执行，休眠一分钟后继续执行，主要是为了让主线程一直执行
        // 休眠时间和监测文件发生的效率无关（就是说不是监视目录文件改变一分钟后才监测到，监测几乎是实时的，调用本地系统库）
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {// ignore it
            }
        }
    }

    /**
     * 当监听目录下一旦有新的文件被创建，则即触发该事件
     *
     * @param wd
     *            监听线程id
     * @param rootPath
     *            监听目录
     * @param name
     *            文件名称
     */
    @Override
    public void fileCreated(int wd, String rootPath, String name) {
        System.err.println("文件被创建, 创建位置为： " + rootPath + "/" + name);
    }

    @Override
    public void fileRenamed(int wd, String rootPath, String oldName, String newName) {
        System.err.println("文件被重命名, 原文件名为：" + rootPath + "/" + oldName
                + ", 现文件名为：" + rootPath + "/" + newName);
    }
    @Override
    public void fileModified(int wd, String rootPath, String name) {
        System.err.println("文件内容被修改, 文件名为：" + rootPath + "/" + name);
    }
    @Override
    public void fileDeleted(int wd, String rootPath, String name) {
        System.err.println("文件被删除, 被删除的文件名为：" + rootPath + name);
    }


}



//
//public class FileMonitor extends JNotifyAdapter {
//    private static final String REQUEST_BASE_PATH = "E:\\tmp";
//
//    /** 被监视的目录 */
//    String path = REQUEST_BASE_PATH;
//    /** 关注目录的事件 */
//    int mask = JNotify.FILE_CREATED;// | JNotify.FILE_DELETED | JNotify.FILE_MODIFIED | JNotify.FILE_RENAMED;
//    /** 是否监视子目录，即级联监视 */
//    boolean watchSubtree = true;
//    /** 监听程序Id */
//    public int watchID;
//
//    @Autowired
//    @Resource(name = "redisDao")
//    private RedisDao redisDao;
//
//
//    public static void main(String[] args) {
//        //JSONObject token = getToken();
//        //postFileUpload();
//        //redisDao.vGet("");
//        new ThirdAppToken().updateToken();
//        new FileMonitor().beginWatch();
//    }
//
//    /**
//     * 容器启动时启动监视程序
//     *
//     * @return
//     */
//    public void beginWatch() {
//        /** 添加到监视队列中 */
//        try {
//            this.watchID = JNotify.addWatch(path, mask, watchSubtree, this);
//
//            System.err.println("jnotify -----------启动成功-----------");
//        } catch (JNotifyException e) {
//            e.printStackTrace();
//        }
//
//        // 死循环，线程一直执行，休眠一分钟后继续执行，主要是为了让主线程一直执行
//        // 休眠时间和监测文件发生的效率无关（就是说不是监视目录文件改变一分钟后才监测到，监测几乎是实时的，调用本地系统库）
//        while (true) {
//            try {
//                sleep(1000);
//            } catch (InterruptedException e) {// ignore it
//            }
//        }
//    }
//
//    /**
//     * 当监听目录下一旦有新的文件被创建，则即触发该事件
//     *
//     * @param wd
//     *            监听线程id
//     * @param rootPath
//     *            监听目录
//     * @param name
//     *            文件名称
//     */
//    public void fileCreated(int wd, String rootPath, String name) {
//        System.err.println("文件被创建, 创建位置为： " + rootPath +"\\" + name);
//
//        // 将新创建的文件上传至服务器
//        //PageData pageData = fileUpload(rootPath, name);
//
//        //ThirdAppToken thirdAppToken = new ThirdAppToken();
//        //thirdAppToken.updateToken();
//
//        //System.err.println(pageData.getString("filePath"));
//    }
//
//    public void fileRenamed(int wd, String rootPath, String oldName, String newName) {
//        System.err.println("文件被重命名, 原文件名为：" + rootPath + "/" + oldName
//                + ", 现文件名为：" + rootPath + "/" + newName);
//    }
//
//    public void fileModified(int wd, String rootPath, String name) {
//        System.err.println("文件内容被修改, 文件名为：" + rootPath + "/" + name);
//    }
//
//    public void fileDeleted(int wd, String rootPath, String name) {
//        System.err.println("文件被删除, 被删除的文件名为：" + rootPath + name);
//    }
//
//
//    public static MultipartFile createMfileByPath(String path) {
//        MultipartFile mFile = null;
//
//        try {
//            File file = new File(path);
//            FileInputStream fileInputStream = new FileInputStream(file);
//
//            String fileName = file.getName();
//            fileName = fileName.substring((fileName.lastIndexOf("/") + 1));
//            mFile =  new MockMultipartFile(fileName, fileName, ContentType.APPLICATION_OCTET_STREAM.toString(), fileInputStream);
//
//        } catch (Exception e) {
//            //log.error("封装文件出现错误：{}", e);
//            e.printStackTrace();
//        }
//        return mFile;
//    }
//
//    public PageData fileUpload(String rootPath, String name) {
//        // 1. 等带文件的占用进程释放文件
//        File f1 = new File(rootPath +"\\" + name);
//        File f2 = new File(rootPath +"\\" + "xxx-" + name);
//        while (!f1.renameTo(f2)) {
//            ;
//        }
//        f2.renameTo(f1);
//
//        // 2. 上传文件
//        // 2.1 准备multiPartFile
//        FileInputStream fis = null;
//        MultipartFile mpFile = createMfileByPath(rootPath + "\\" + name);
//
//        String fileName = mpFile.getOriginalFilename();
//        String suffix = fileName.substring(fileName.indexOf('.')+1, fileName.length());
//
//        // 2.2 链接对象存储服务器，上传文件
//        AwsUtil aswUtil = new AwsUtil();
//        aswUtil.setAwsKey("ocloud_access_key");
//        aswUtil.setSecretAccessKey("ocloud_secret_key");
//        aswUtil.setRegion("http://120.92.139.116:9000");
//        PageData pageData = aswUtil.upload(mpFile, ConstantValUtil.BUCKET_PRIVATE);
//
//        System.err.println("文件被上传, 创建位置为： " + rootPath +"\\" + name);
//
//        // 2.3 关闭文件流
//        try {
//            mpFile.getInputStream().close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return pageData;
//    }
//
////    public static void postFileUpload() {
////        JSONObject jsonObject = getToken();
////        String token = jsonObject.getString("access_token");
////
////        if (token.length() > 0) {
////            Long exp = 7200L;
////            String tokenKey ="access_token:"+token;
////
////            String macAddr = null;
////            InetAddress ipAddress = null;
////            try {
////                ipAddress = InetAddress.getLocalHost();
////                byte[] mac = NetworkInterface.getByInetAddress(ipAddress).getHardwareAddress();
////                StringBuffer sb = new StringBuffer("");
////                for (int i = 0; i < mac.length; i++) {
////                    if (i != 0) {
////                        sb.append("-");
////                    }
////                    //字节转换为整数
////                    int temp = mac[i] & 0xff;
////                    String str = Integer.toHexString(temp);
////                    if (str.length() == 1) {
////                        sb.append("0" + str);
////                    } else {
////                        sb.append(str);
////                    }
////                }
////
////                macAddr = sb.toString().toUpperCase();
////            } catch (UnknownHostException e) {
////                e.printStackTrace();
////            } catch (SocketException e) {
////                e.printStackTrace();
////            }
////
////
////            boolean result = redisDao.vSet(tokenKey, macAddr, exp);
////            String str = "helo";
////        }
////    }
//
//    private static JSONObject getToken() {
//        String url = "http://localhost:1001/rdexpense/oauth/token";
//
//        PageData pd = new PageData();
//        pd.put("grant_type","client_credentials");
//        pd.put("scope","select");
//        pd.put("client_id","mctech");
//        pd.put("client_secret","JaPEdO9X0JE0Qb5JFsTlLkNlx3rqi9sCCjU5104J");
//
//        int num = 0;
//        StringBuffer param = new StringBuffer("");
//        @SuppressWarnings("unchecked")
//        Set<Map.Entry<String, String>> set = pd.entrySet();
//
//        for (Map.Entry<String, String> entry : set) {
//            num++;
//            param.append(entry.getKey() + "=" + entry.getValue());
//            if (num < pd.size()) {
//                param.append("&");
//            }
//        }
//
//        HttpUtils httpUtils = new HttpUtils();
//        String token = httpUtils.sendPost(url, param.toString());
//        JSONObject jsonObject = JSON.parseObject(token);
//        return jsonObject;
//    }
//}