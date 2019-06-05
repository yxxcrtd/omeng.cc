打包流程
pre:
1、修改开发模式为false
	/** 是否为开发模式 */
	public static final boolean DEVMODE = false;
	修改NGINX_PATH = "http://120.55.119.232"

2、修改provider和consumer的zookeeper地址
<dubbo:registry address="zookeeper://10.174.104.234:2181?backup=10.173.143.96:2181,10.251.254.119:2181" />

build：
1、创建一个干净目录，从svn中拉取最新的代码
2、在命令行执mvn clean install -P [cbt、zyb]
3、killall -9 java，杀掉所有服务器中所有java程序
4、删除服务器tomcat下webapps中ROOT目录和ROOT.war包；删除服务器中/usr/local/provider下的所有文件
5、到dubbo-consumer\target目录下将ROOT.war包copy到服务器192.168.1.58的tomcat上webapps目录
6、到dubbo-provider\target目录下将cbt.jar和dependency目录copy到服务器中/usr/local/provider下；如果需要打包多个provider，copy多个*.jar。覆盖服务器中的dependency目录。
7、复制omeng目录下的provider脚本文件到/usr/local/provider
8、到服务器中执行/usr/local/provider start
9、启动tomcat
10、清除缓存 ssh到192.168.1.55，连接redis执行 -->  flushall
11、用命令行到dubbo-consumer目录下，执行mvn test -P test 命令，跑自动化测试脚本。如果所有的case都能通过，表示打包发布成功。如果又失败的case，需要通知相关开发人员进行修改和代码提交，然后重复1-11步骤

cbt、dgf、fyb、hyt、hz、
jzx、lxz、swg、qzy、sxd、
syp、xhf、xlb、yd、ydc、
ydh、yxt、zsy、zyb、zyd、


ts、mst、ams、zdf、jrj