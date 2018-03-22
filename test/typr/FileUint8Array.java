package typr;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import elemental.html.ArrayBuffer;
import elemental.html.Uint8Array;

public class FileUint8Array implements Uint8Array
{
  public FileUint8Array(File f) throws IOException
  {
    file = new RandomAccessFile(f, "r");
  }
  final RandomAccessFile file;
  
  @Override
  public int getByteLength()
  {
    try {
      return (int)file.length();
    } 
    catch (IOException e)
    {
      throw new RuntimeException(e);
    }
  }

  @Override
  public ArrayBuffer getBuffer()
  {
    throw new IllegalArgumentException("Operation not supported");
  }

  @Override
  public int getByteOffset()
  {
    throw new IllegalArgumentException("Operation not supported");
  }

  @Override
  public int intAt(int index)
  {
    try {
      file.seek(index);
      return file.read();
    } 
    catch (IOException e)
    {
      throw new RuntimeException(e);
    }
  }

  @Override
  public int length()
  {
    return getByteLength();
  }

  @Override
  public double numberAt(int index)
  {
    return intAt(index);
  }

  @Override
  public int getLength()
  {
    return getByteLength();
  }

  @Override
  public void setElements(Object array)
  {
    throw new IllegalArgumentException("Operation not supported");
  }

  @Override
  public void setElements(Object array, int offset)
  {
    throw new IllegalArgumentException("Operation not supported");
  }

  @Override
  public Uint8Array subarray(int start)
  {
    throw new IllegalArgumentException("Operation not supported");
  }

  @Override
  public Uint8Array subarray(int start, int end)
  {
    throw new IllegalArgumentException("Operation not supported");
  }
  
}