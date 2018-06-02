# Docker-maven-plugin
This maven plugin support for generate related docker resouces,including command: init,package

## Getting Started

### 1.在maven 工程pom中引入本插件

```
<plugin>
		<groupId>com.aibibang</groupId>
		<artifactId>docker-maven-plugin</artifactId>
		<version>1.0.0</version>
		<configuration>
				<imageName>TestDemo</imageName>
				<tag>1.0</tag>
		</configuration>
</plugin>
```

### 2.初始化docker相关资源

```
mvn docker:init
```
生成的资源默认位于

```
/src/main/resources/docker
```

生成三个文件

```
build.sh//生成镜像脚本
``` 

```
start.sh//容器中项目启动脚本
``` 

```
Dockerfile//Dockerfile文件
``` 

### 3.项目打包

将项目打包成jar，例如：```mvn clean package ```

### 4.docker资源打包

```
mvn docker:package
```
生成文件名为*****_docker.tar.gz

### 5.解压tar包，制作镜像

```
$ tar -xvf *****_docker.tar.gz
$ cd *****_docker
$ sh build.sh
```


## Configuration

imageName默认值是项目名称，tag默认值为版本号。

在该插件中可以定义镜像名称和tag,如果生成的不满意，还可以修改生成文件，达到修改目的。

```
<configuration>
	<imageName>TestDemo</imageName>
	<tag>1.0</tag>
</configuration>
```

还能修改生成docker resources 文件所在目录，默认为```/src/main/resources/docker```
```
<configuration>
	<dockerSourceDir>/docker</dockerSourceDir>
</configuration>
```

## Tip

- 执行docker:init命令会删除之前docker文件，请知晓！
- 关于docker iamge name 生成结果为小写字母