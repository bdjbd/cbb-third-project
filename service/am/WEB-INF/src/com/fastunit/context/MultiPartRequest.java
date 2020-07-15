package com.fastunit.context;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.RequestContext;
import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fastunit.Path;
import com.fastunit.framework.config.UploadSettings;
import com.fastunit.framework.dispatcher.DispatcherServlet;

/**
 * Multipart form data request adapter for Jakarta Commons Fileupload package.
 */
public class MultiPartRequest {

	private static final Logger log = LoggerFactory
			.getLogger(DispatcherServlet.class);

	private static final String PJPEG = "image/pjpeg";
	private static final String JPEG = "image/jpeg";

	private static final String SAVE_DIR = Path.getHomePath() + "/temp";
	public static final String FILE_NAME = "filename";
	public static final String CONTENT_TYPE = "contenttype";
	public static final String FILE_SIZE = "filesize";
	// maps parameter name -> List of FileItem objects
	private final Map<String, List<FileItem>> files = new HashMap<String, List<FileItem>>();

	// maps parameter name -> List of param values
	private final Map<String, List<String>> params = new HashMap<String, List<String>>();

	// any errors while processing this request
	private final List<String> errors = new ArrayList<String>();

	/**
	 * Creates a new request wrapper to handle multi-part data using methods
	 * adapted from Jason Pell's multipart classes (see class description).
	 * 
	 * @param saveDir
	 *          the directory to save off the file
	 * @param servletRequest
	 *          the request containing the multipart
	 * @throws java.io.IOException
	 *           is thrown if encoding fails.
	 */
	public void parse(HttpServletRequest servletRequest) throws IOException {
		DiskFileItemFactory fac = new DiskFileItemFactory();
		// Make sure that the data is written to file
		fac.setSizeThreshold(0);
		// 设置缓冲区目录
		File multipartSaveDir = new File(SAVE_DIR);
		if (!multipartSaveDir.exists()) {
			multipartSaveDir.mkdir();
		}
		fac.setRepository(multipartSaveDir);

		// Parse the request
		long maxSize = UploadSettings.getConfig().getMaxSize();
		try {
			ServletFileUpload upload = new ServletFileUpload(fac);
			upload.setSizeMax(maxSize);

			List items = upload.parseRequest(createRequestContext(servletRequest));

			for (Object item1 : items) {
				FileItem item = (FileItem) item1;
				String fieldName = item.getFieldName();
				if (item.isFormField()) {
					log.debug(fieldName + " : normal form field");
					List<String> values;
					if (params.get(fieldName) != null) {
						values = params.get(fieldName);
					} else {
						values = new ArrayList<String>();
					}

					// note: see http://jira.opensymphony.com/browse/WW-633
					// basically, in some cases the charset may be null, so
					// we're just going to try to "other" method (no idea if this
					// will work)
					String charset = servletRequest.getCharacterEncoding();
					if (charset != null) {
						values.add(item.getString(charset));
					} else {
						values.add(item.getString());
					}
					params.put(fieldName, values);
				} else {
					log.debug(fieldName + " : file upload");

					List<FileItem> values;
					if (files.get(fieldName) != null) {
						values = files.get(fieldName);
					} else {
						values = new ArrayList<FileItem>();
						files.put(fieldName, values);
					}

					if (item.getName() == null || item.getName().trim().length() < 1) {
						log.debug("No file has been uploaded for the field: " + fieldName);
						values.add(null);
					} else {
						values.add(item);
					}
				}
			}
			// 设置关联字段
			Enumeration<String> fileParameterNames = getFileParameterNames();
			while (fileParameterNames != null && fileParameterNames.hasMoreElements()) {
				String fieldName = fileParameterNames.nextElement();
				setFileFieldsValue(params, fieldName);
			}

		} catch (SizeLimitExceededException e) {
			// 此处不记录日志
			//DecimalFormat dateFormat=new DecimalFormat("#.0000");
			DecimalFormat df=new DecimalFormat("0.00");
			errors.add("上传的文件大小超出最大限制" + df.format((maxSize/1024d/1024d)) + "兆");
		} catch (FileUploadException e) {
			log.error("Unable to parse request", e);
			errors.add(e.getMessage());
		}
	}

	public Enumeration<String> getFileParameterNames() {
		return Collections.enumeration(files.keySet());
	}

	private void setFileFieldsValue(Map<String, List<String>> params,
			String fieldName) {
		List<FileItem> items = files.get(fieldName);
		if (items != null) {
			List<String> values = new ArrayList<String>(items.size());
			List<String> contentTypes = new ArrayList<String>(items.size());
			List<String> fileNames = new ArrayList<String>(items.size());
			List<String> fileSizes = new ArrayList<String>(items.size());
			for (FileItem fileItem : items) {
				if (fileItem == null) {
					values.add(null);
					contentTypes.add(null);
					fileNames.add(null);
					fileSizes.add(null);
				} else {
					File file = ((DiskFileItem) fileItem).getStoreLocation();
					values.add(file.getPath());
					contentTypes.add(formatContentType(fileItem.getContentType()));
					fileNames.add(getCanonicalName(fileItem.getName()));
					fileSizes.add(Long.toString(file.length()));
				}
			}
			params.put(fieldName, values);// 务必不为空，否则跳过验证
			params.put(fieldName + CONTENT_TYPE, contentTypes);
			params.put(fieldName + FILE_NAME, fileNames);
			params.put(fieldName + FILE_SIZE, fileSizes);
		}
	}

	public File[] getFile(String fieldName) {
		List<FileItem> items = files.get(fieldName);

		if (items == null) {
			return null;
		}

		List<File> fileList = new ArrayList<File>(items.size());
		for (FileItem fileItem : items) {
			if (fileItem == null) {
				fileList.add(null);
			} else {
				fileList.add(((DiskFileItem) fileItem).getStoreLocation());
			}
		}

		return fileList.toArray(new File[fileList.size()]);
	}

	public String[] getFilesystemName(String fieldName) {
		List<FileItem> items = files.get(fieldName);

		if (items == null) {
			return null;
		}

		List<String> fileNames = new ArrayList<String>(items.size());
		for (FileItem fileItem : items) {
			if (fileItem == null) {
				fileNames.add(null);
			} else {
				fileNames.add(((DiskFileItem) fileItem).getStoreLocation().getName());
			}
		}

		return fileNames.toArray(new String[fileNames.size()]);
	}

	public String getParameter(String name) {
		List<String> v = params.get(name);
		if (v != null && v.size() > 0) {
			return v.get(0);
		}

		return null;
	}

	public Enumeration<String> getParameterNames() {
		return Collections.enumeration(params.keySet());
	}

	public String[] getParameterValues(String name) {
		List<String> v = params.get(name);
		if (v != null && v.size() > 0) {
			return v.toArray(new String[v.size()]);
		}

		return null;
	}

	public List getErrors() {
		return errors;
	}

	/**
	 * Returns the canonical name of the given file.
	 * 
	 * @param filename
	 *          the given file
	 * @return the canonical name of the given file
	 */
	private String getCanonicalName(String filename) {
		int forwardSlash = filename.lastIndexOf("/");
		int backwardSlash = filename.lastIndexOf("\\");
		if (forwardSlash != -1 && forwardSlash > backwardSlash) {
			filename = filename.substring(forwardSlash + 1, filename.length());
		} else if (backwardSlash != -1 && backwardSlash >= forwardSlash) {
			filename = filename.substring(backwardSlash + 1, filename.length());
		}

		return filename==null?filename:filename.toLowerCase();
	}

	private String formatContentType(String contentType) {
		if (PJPEG.equals(contentType)) {
			return JPEG;
		} else {
			return contentType;
		}
	}

	/**
	 * Creates a RequestContext needed by Jakarta Commons Upload.
	 * 
	 * @param req
	 *          the request.
	 * @return a new request context.
	 */
	private RequestContext createRequestContext(final HttpServletRequest req) {
		return new RequestContext() {
			@Override
			public String getCharacterEncoding() {
				return req.getCharacterEncoding();
			}

			@Override
			public String getContentType() {
				return req.getContentType();
			}

			@Override
			public int getContentLength() {
				return req.getContentLength();
			}

			@Override
			public InputStream getInputStream() throws IOException {
				InputStream in = req.getInputStream();
				if (in == null) {
					throw new IOException("Missing content in the request");
				}
				return req.getInputStream();
			}
		};
	}

}
