package com.yiyun.sijianguan.common;

public class Common_data {

	public final static String share_name = "common_info";
	public final static String username = "username";
	public final static String password = "password";
	public final static String image_path = "image_path";
	public final static String image_name = "image_name";
	// 命名空间
	public final static String nameSpace = "http://tempuri.org/";
	public final static String IP = "http://123.232.119.119:8091";
	public final static String FILE_IP = "http://123.232.119.119:8090";
	// EndPoint三和
	// public final static String endPoint =
	// "http://192.168.2.103:8081/lyc_as/Service1.asmx";
	// public static String endPoint =
	// "http://123.132.252.2:9883/Service1.asmx";
	// public static String image_url = "http://123.132.252.2:9899/Upload/oas/";
	public static String url_server = IP + "/apk/version_info.xml";// apk目前都在赛捷服务器上进行更新

	// 现场
	public static String endPoint = IP + "/Service1.asmx";
	// public static String url_server =
	// "http://221.2.68.102:8090/As/apk/version_info.xml";--no
	public static String image_url = FILE_IP + "/upload/oas/";// 附件存放文件夹地址
}
