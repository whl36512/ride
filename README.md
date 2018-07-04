# ride


Development Configuration -IDE
1. Download Scala IDE for Eclipse which includes Scala, play2
2. Download and install supported Java SDK version
3. Insall Scala IDE for Eclipse

Development Configuration 
1. Download java 10
	
	wget --no-cookies --no-check-certificate --header "Cookie: oraclelicense=accept-securebackup-cookie"   \
		http://download.oracle.com/otn-pub/java/jdk/10.0.1+10/fb4372174a714e6b8c52526dc134031e/jdk-10.0.1_linux-x64_bin.tar.gz
	tar zxf jdk-10.0.1_linux-x64_bin.tar.gz -C /usr/local
	ln -s /usr/local/jdk-10.0.1 /usr/local/jdk
	alternatives --install /usr/bin/java java /usr/local/jdk/bin/java 2		
	alternatives --install /usr/bin/javac javac /usr/local/jdk/bin/javac 2		
	alternatives --install /usr/bin/jar jar /usr/local/jdk/bin/jar 2		
	alternatives --set java  /usr/local/jdk/bin/java
	alternatives --set javac /usr/local/jdk/bin/javac
	alternatives --set jar   /usr/local/jdk/bin/jar  
	echo 'export JAVA_HOME=/usr/local/jdk' >> /etc/profile
2. Download sbt 1.1.6
	wget https://piccolo.link/sbt-1.1.6.tgz
	tar vzxf sbt-1.1.6.tgz -C /usr/local
	alternatives --install /usr/bin/sbt sbt /usr/local/sbt/bin/sbt 2
	alternatives --set  sbt /usr/local/sbt/bin/sbt 


