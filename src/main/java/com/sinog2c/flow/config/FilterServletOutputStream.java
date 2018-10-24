package com.sinog2c.flow.config;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
/**
 * 
* @ClassName:：FilterServletOutputStream 
* @Description： TODO
* @author ：xujie 
* @date ：2018年10月23日 下午8:32:53 
*
 */
public class FilterServletOutputStream extends ServletOutputStream {

  private DataOutputStream stream;
  private WriteListener writeListener;

  public FilterServletOutputStream(OutputStream output) {
    stream = new DataOutputStream(output);
  }
  
  @Override
  public void write(int b) throws IOException {
    stream.write(b);
  }

  @Override
  public void write(byte[] b) throws IOException {
    stream.write(b);
  }
  
  @Override
  public void write(byte[] b, int off, int len) throws IOException {
    stream.write(b, off, len);
  }
  
  @Override
  public void setWriteListener(WriteListener writeListener) {
    this.writeListener =  writeListener;
  }

  @Override
  public boolean isReady() {
    return true;
  }
  
  
}
