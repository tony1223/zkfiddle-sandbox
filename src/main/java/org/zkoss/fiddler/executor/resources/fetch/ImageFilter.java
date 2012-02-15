package org.zkoss.fiddler.executor.resources.fetch;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zkoss.fiddler.executor.utils.ServletResponseWrapper;


public class ImageFilter implements Filter{

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {
		String url = (((HttpServletRequest)request).getRequestURI());
		if(url != null && url .indexOf("/zkau")!=-1){
			chain.doFilter(request, response);
			return ;
		}
		ServletResponseWrapper wrapper = new ServletResponseWrapper((HttpServletResponse) response);
		chain.doFilter(request, wrapper);
		wrapper.getOutputStream().flush();
		byte[] datas = wrapper.getBytes();
		String content = ungzip(datas);
		if(content == null) {
			content = new String(datas);
		}
		if(content != null && content.startsWith("img:::")){
			((HttpServletResponse) response).sendRedirect(content.split("img:::")[1]);
		}else{
			OutputStream writer = ((HttpServletResponse) response).getOutputStream();
			writer.write(datas);
			writer.flush();
			writer.close();
			
		}
	}
	
	public String ungzip(byte[] bytes){
		GZIPInputStream gis;
		try {
			gis = new GZIPInputStream(new ByteArrayInputStream(bytes));
			BufferedReader reader = new BufferedReader(new InputStreamReader(gis));
			StringBuffer sb = new StringBuffer();
			String input = reader.readLine();
			if(input!= null){
				sb.append(input+"\n");
				input = reader.readLine();
			}
			reader.close();
			gis.close();
			return sb.toString();
		} catch (IOException e) {
			//e.printStackTrace(); //might be Not in GZIP format, ignore it directly
		}
		return null;
	}
	
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	public void destroy() {
	}
}
