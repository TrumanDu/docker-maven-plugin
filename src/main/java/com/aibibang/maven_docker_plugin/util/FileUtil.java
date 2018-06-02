package com.aibibang.maven_docker_plugin.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;

/**
 * @author：Truman.P.Du
 * @createDate: 2018年1月26日 下午5:24:16
 * @version:1.0
 * @description:
 */
public class FileUtil {
	public static void copyFile(File originfile, File targetfile) throws Exception {

		if (!targetfile.exists()) {
			try {
				targetfile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		InputStream inStream = new FileInputStream(originfile);
		FileOutputStream fs = new FileOutputStream(targetfile);
		int len = 0;
		byte[] buffer = new byte[1024];

		while ((len = inStream.read(buffer)) != -1) {
			fs.write(buffer, 0, len);
		}
		inStream.close();
		fs.close();
	}

	public static void copyFile(InputStream is, FileOutputStream os) throws Exception {
		int len = 0;
		byte[] buffer = new byte[1024];

		while ((len = is.read(buffer)) != -1) {
			os.write(buffer, 0, len);
		}
	}
	/**
	 * 压缩文件成Gzip格式，Linux上可使用 压缩文件夹生成后缀名为".gz"的文件并下载
	 * 
	 * @param folderPath,要压缩的文件夹的路径
	 * @param zipFilePath,压缩后文件的路径
	 * @param zipFileName,压缩后文件的名称
	 * @throws BizException
	 */
	public static void CompressedFiles_Gzip(String folderPath, String targzipFilePath) {
		File srcPath = new File(folderPath);
		String baseDirName = srcPath.getName();
		int length = srcPath.listFiles().length;
		byte[] buf = new byte[1024]; // 设定读入缓冲区尺寸
		File[] files = srcPath.listFiles();
		try {
			File targzipFile = new File(targzipFilePath);
			if (!targzipFile.exists()) {
				targzipFile.createNewFile();
			}
			// 建立压缩文件输出流
			FileOutputStream fout = new FileOutputStream(targzipFilePath);
			// 建立tar压缩输出流
			TarArchiveOutputStream tout = new TarArchiveOutputStream(fout);
			for (int i = 0; i < length; i++) {
				String filename = srcPath.getPath() + File.separator + files[i].getName();
				File childFile = new File(filename);
				if (childFile.isDirectory()) {// 仅支持双层目录
					File[] childsfiles = childFile.listFiles();
					for (int j = 0; j < childsfiles.length; j++) {
						String childfilename = filename + File.separator + childsfiles[j].getName();
						childFileToTar(baseDirName,childFile.getName(),childfilename, childsfiles[j], tout);
						tout.closeArchiveEntry();
					}
				} else {
					fileToTar(baseDirName,filename, files[i], tout);
					tout.closeArchiveEntry();
				}
			}
			tout.close();
			fout.close();

			// 建立压缩文件输出流
			FileOutputStream gzFile = new FileOutputStream(targzipFilePath + ".gz");
			// 建立gzip压缩输出流
			GZIPOutputStream gzout = new GZIPOutputStream(gzFile);
			// 打开需压缩文件作为文件输入流
			FileInputStream tarin = new FileInputStream(targzipFilePath); // targzipFilePath是文件全路径
			int len;
			while ((len = tarin.read(buf, 0, 1024)) != -1) {
				gzout.write(buf, 0, len);
			}
			gzout.close();
			gzFile.close();
			tarin.close();
			targzipFile.deleteOnExit();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void fileToTar(String baseDirName,String filename, File file, TarArchiveOutputStream tout) throws Exception {
		byte[] buf = new byte[1024]; // 设定读入缓冲区尺寸
		// 打开需压缩文件作为文件输入流
		FileInputStream fin = new FileInputStream(filename); // filename是文件全路径
		TarArchiveEntry tarEn = new TarArchiveEntry(file); // 此处必须使用new TarEntry(File file);
		tarEn.setName(baseDirName+File.separator+file.getName()); // 此处需重置名称，默认是带全路径的，否则打包后会带全路径
		tout.putArchiveEntry(tarEn);
		int num;
		while ((num = fin.read(buf, 0, 1024)) != -1) {
			tout.write(buf, 0, num);
		}
		fin.close();
	}
	
	private static void childFileToTar(String baseDirName,String parentName,String filename, File file, TarArchiveOutputStream tout) throws Exception {
		byte[] buf = new byte[1024]; // 设定读入缓冲区尺寸
		// 打开需压缩文件作为文件输入流
		FileInputStream fin = new FileInputStream(filename); // filename是文件全路径
		TarArchiveEntry tarEn = new TarArchiveEntry(file); // 此处必须使用new TarEntry(File file);
		tarEn.setName(baseDirName+File.separator+parentName+File.separator+file.getName()); // 此处需重置名称，默认是带全路径的，否则打包后会带全路径
		tout.putArchiveEntry(tarEn);
		int num;
		while ((num = fin.read(buf, 0, 1024)) != -1) {
			tout.write(buf, 0, num);
		}
		fin.close();
	}

	public static void main(String[] args) {
		FileUtil.CompressedFiles_Gzip("D:\\neonworkspace\\TestDemo\\target\\docker",
				"D:\\neonworkspace\\TestDemo\\target\\docker_test.tar");
	}
}
