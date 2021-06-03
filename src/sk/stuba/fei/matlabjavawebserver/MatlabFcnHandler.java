package sk.stuba.fei.matlabjavawebserver;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import matlabcontrol.MatlabInvocationException;
import matlabcontrol.MatlabProxy;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;


/**
 * This class handles web requests for matlab functions
 *
 */
public class MatlabFcnHandler extends AbstractHandler {

	private String documentRoot;
	private MatlabProxy matlabProxy;
//	private String welcomeFiles[];
	
	/**
	 * Default empty constructor.
	 */
	public MatlabFcnHandler() {
	}
	
	
	public MatlabFcnHandler(MatlabProxy matlabProxy, String docRoot) {
		this.documentRoot = docRoot;
		this.matlabProxy = matlabProxy;
	}
	
	
	/**
	 * Returns current document root.
	 * 
	 * @return current document root
	 */
	public String getDocumentRoot() {
		return documentRoot;
	}



	/**
	 * Set new document root.
	 * 
	 * @param docRoot - new document root
	 */
	public void setDocumentRoot(String docRoot) {
		this.documentRoot = docRoot;
	}



	/**
	 * Return current reference to MatlabProxy object.
	 * 
	 * @return current reference to MatlabProxy object
	 */
	public MatlabProxy getMatlabProxy() {
		return matlabProxy;
	}



	/**
	 * Set new reference to MatlabProxy object.
	 * 
	 * @param matlabProxy - new reference to MatlabProxy object
	 */
	public void setMatlabProxy(MatlabProxy matlabProxy) {
		this.matlabProxy = matlabProxy;
	}

//	public void setWelcomeFiles(String[] welcomeFiles) {
//		this.welcomeFiles = welcomeFiles;
//	}
//	
//	public String[] getWelcomeFiles() {
//		return welcomeFiles;
//	}

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
		File targetFile = new File(target);
//		// check if requested file is directory
//		if(targetFile.isDirectory()) {
//			// if it is, check if some welcome files are present in that directory
//			for (String welcomeFilePath : welcomeFiles) {
//				File welcomeFile = new File(target+welcomeFilePath);
//				System.out.println(welcomeFile.toString());
//				// first existing welcome file is chosen
//				if(welcomeFile.exists()) {
//					targetFile = welcomeFile;
//					break;
//				}
//			}
//		}
		// fcnParentDirName - full path to parent directory of matlab function
		String fcnParentDirName = (new File(documentRoot)).getCanonicalPath()+targetFile.getParent();
		// fcnName - name of matlab function
		String fcnFileName = targetFile.getName();
		if(!fcnFileName.endsWith(".m")) {
			return;
		}
		String fcnName = fcnFileName.substring(0,fcnFileName.length()-2);
		try {
			// change directory to parent direcotry of matlab function
			matlabProxy.eval("cd('"+fcnParentDirName+"')");
			// execute matlab function
			matlabProxy.feval(fcnName, request, response);
			baseRequest.setHandled(true);
		} catch (MatlabInvocationException e) {
			//throw new IOException(e);
			// better solution may be to generate custom 500 error page, like this:
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

}
