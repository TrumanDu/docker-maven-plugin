package com.aibibang.maven_docker_plugin;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.aibibang.maven_docker_plugin.util.FileUtil;

/**
 * @author：Truman.P.Du
 * @createDate: 2018年1月26日 下午4:54:23
 * @version:1.0
 * @description:将docker相关资源打成一个tar包
 */
@Mojo(name = "package")
public class PackageMojo extends AbstractMojo {
	@Parameter(defaultValue = "${basedir}")
	private String basedir;
	@Parameter(defaultValue = "${project.build.finalName}", property = "jarName", required = true)
	private  String jarName;
	@Parameter(defaultValue = "/src/main/resources/docker", property = "dockerSourceDir", required = true)
	private String dockerSourceDirStr;
	@Parameter(defaultValue = "${project.build.directory}", property = "target", required = true)
	private String target;
	private String separator = System.getProperty("file.separator");
	private String dockerPath;
	private String appPath;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().info("docker package begain:");
		dockerPath = target + separator + jarName + "_" + "docker";
		File docker = new File(dockerPath);
		if (docker.exists()) {
			docker.delete();
		}
		docker.mkdir();

		appPath = dockerPath + separator + "app";
		File app = new File(appPath);
		app.mkdir();
		copyFile("start.sh");
		copyFile("Dockerfile");
		copyFile("build.sh");
		copyFile(jarName + ".jar");
		String gzipPath = target + separator + jarName + "_" + "docker.tar";
		FileUtil.CompressedFiles_Gzip(dockerPath, gzipPath);
		getLog().info("docker package success:" + gzipPath);
		getLog().info("docker package end");
	}

	/**
	 * 
	 * @param filePath
	 */
	public void copyFile(String fileName) {
		String resourcesDockerPath = basedir + dockerSourceDirStr;
		String originPath = null;
		String targetPath = null;
		switch (fileName) {
		case "start.sh":
			originPath = resourcesDockerPath + System.getProperty("file.separator") + fileName;
			targetPath = appPath + System.getProperty("file.separator") + fileName;
			break;
		case "Dockerfile":
			originPath = resourcesDockerPath + System.getProperty("file.separator") + fileName;
			targetPath = dockerPath + System.getProperty("file.separator") + fileName;
			break;
		case "build.sh":
			originPath = resourcesDockerPath + System.getProperty("file.separator") + fileName;
			targetPath = dockerPath + System.getProperty("file.separator") + fileName;
			break;
		default:
			originPath = target + System.getProperty("file.separator") + fileName;
			targetPath = appPath + System.getProperty("file.separator") + fileName;
		}

		try {
			FileUtil.copyFile(new File(originPath), new File(targetPath));
		} catch (Exception e) {
			getLog().error("copy " + fileName + " to target error", e);
		}
	}

}
