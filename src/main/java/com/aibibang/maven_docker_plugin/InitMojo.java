package com.aibibang.maven_docker_plugin;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.MessageFormat;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * @author：Truman.P.Du
 * @createDate: 2018年1月26日 上午10:17:52
 * @version:1.0
 * @description: 初始化生成docker资源
 */
@Mojo(name = "init")
public class InitMojo extends AbstractMojo {

	@Parameter(defaultValue = "${basedir}")
	private String basedir;
	@Parameter(defaultValue = "/src/main/resources/docker", property = "dockerSourceDir", required = true)
	private String dockerSourceDirStr;
	@Parameter(defaultValue = "${project.build.finalName}", property = "jarName", required = true)
	private  String jarName;
	@Parameter(defaultValue = "${project.artifactId}", property = "imageName", required = true)
	private  String imageName;
	@Parameter(defaultValue = "${project.version}", property = "tag", required = true)
	private  String tag;
	private String buildShStr = "build.sh";
	private String startShStr = "start.sh";
	private String dockerfileStr = "Dockerfile";

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().info("begain to init docker resources file");

		String dockerPath = basedir + dockerSourceDirStr;
		File dockerSourceDir = new File(dockerPath);
		if (dockerSourceDir.exists()) {
			dockerSourceDir.delete();
		}
		dockerSourceDir.mkdirs();
		this.create(buildShStr);
		this.create(startShStr);
		this.create(dockerfileStr);
	}

	/**
	 * 根据文件名创建相应的文件
	 * 
	 * @param fileName
	 */
	public void create(String fileName) {
		ClassLoader classLoader = getClass().getClassLoader();
		String dockerPath = basedir + dockerSourceDirStr;
		String buildshPath = dockerPath + System.getProperty("file.separator") + fileName;
		File buildSh = new File(buildshPath);
		if (buildSh.exists()) {
			buildSh.delete();
		}

		try {
			buildSh.createNewFile();
		} catch (IOException e) {
			getLog().error("create " + fileName + " file error:", e);
		}

		InputStream is = classLoader.getResourceAsStream("docker/" + fileName);
		FileOutputStream os = null;
		try {
			os = new FileOutputStream(buildSh);
			copyFile(is, os, fileName);
		} catch (Exception e) {
			getLog().error("copy " + fileName + " file error:", e);
		} finally {
			try {
				is.close();
				if (os != null)
					os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 复制模板文件到指定目录下，并且修改相应公共变量
	 * 
	 * @param is
	 * @param os
	 * @param type
	 *            0:build.sh 1:start.sh 2:Dockerfile
	 * @throws Exception
	 */
	public void copyFile(InputStream is, FileOutputStream os, String fileName) throws Exception {
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
		String line = null;
		int index = 0;
		while ((line = br.readLine()) != null) {
			switch (fileName) {
			case "build.sh":
				if (line.indexOf("{0}:{1}") > 0) {
					MessageFormat messageFormat = new MessageFormat(line);
					Object[] array = new Object[] { imageName.toLowerCase(), tag };
					line = messageFormat.format(array);
				}
				break;
			case "start.sh":
				if (line.indexOf("{0}") > 0) {
					MessageFormat messageFormat = new MessageFormat(line);
					Object[] array = new Object[] { jarName };
					line = messageFormat.format(array);
				}
				break;
			case "Dockerfile":
				if (line.indexOf("{0}") > 0) {
					MessageFormat messageFormat = new MessageFormat(line);
					Object[] array = new Object[] { imageName.toLowerCase() };
					line = messageFormat.format(array);
				}
				break;
			}
			// 避免生成文件第一行产生空行
			if (index != 0) {
				//bw.newLine();
				bw.write('\n');
			}
			bw.write(line);
			index++;

		}
		br.close();
		bw.close();
	}

	public static void main(String[] args) {
		String line = "docker build -t docker.neg/ecbd/{0}:{1} .";
		MessageFormat messageFormat = new MessageFormat(line);
		Object[] array = new Object[] { "sss", "1.0" };
		line = messageFormat.format(array);
		System.out.println(line);
	}

}
