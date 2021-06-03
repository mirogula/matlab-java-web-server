package sk.stuba.fei.matlabjavawebserver;

import matlabcontrol.MatlabProxyFactory;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;

public class MJWebServer extends Server {
	
	private MatlabFcnHandler matlabFcnHandler;
	
	/**
	 * Create new instance of MJWebServer
	 * 
	 * @param port - port number on which server will be listening
	 * @param documentRoot - document root for web content and matlab functions
	 * @throws Exception
	 */
	public MJWebServer(int port, String documentRoot) throws Exception {
		super(port);

		// resourceHandler is default web handler, it handles web requests for static content (.html files)
		ResourceHandler resourceHandler = new ResourceHandler();
		resourceHandler.setDirectoriesListed(true);
		resourceHandler.setWelcomeFiles(new String[] {"index.html"});
		resourceHandler.setResourceBase(documentRoot);

		// matlabFcnHandler handles web requests for matlab functions
		matlabFcnHandler = new MatlabFcnHandler();
		matlabFcnHandler.setDocumentRoot(documentRoot);
		//matlabFcnHandler.setWelcomeFiles(new String[] {"index.m"});
		
		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] {matlabFcnHandler, resourceHandler});
		setHandler(handlers);
	}
	
	
	@Override
	protected void doStart() throws Exception {
		matlabFcnHandler.setMatlabProxy(new MatlabProxyFactory().getProxy());
		super.doStart();
		System.out.println("server is running ...");
	}
	
	
	@Override
	protected void doStop() throws Exception {
		super.doStop();
		matlabFcnHandler.getMatlabProxy().disconnect();
		matlabFcnHandler.setMatlabProxy(null);
		System.out.println("server has stopped");
	}
}
