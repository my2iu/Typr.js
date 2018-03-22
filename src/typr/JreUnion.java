package typr;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

import com.google.gwt.core.shared.GwtIncompatible;

import elemental.html.Uint8Array;
import typr.bin.Union;

@GwtIncompatible public class JreUnion extends Union
{
  byte [] data = new byte[8];
  ByteBuffer buffer = ByteBuffer.wrap(data);
  public JreUnion()
  {
    buffer.order(ByteOrder.LITTLE_ENDIAN);
  }
  @Override void writeUint8(int offset, byte b)
  {
    buffer.put(offset, b);
  }
  @Override int readInt32()
  {
    return buffer.getInt(0);
  }
  @Override int readUint32()
  {
    return buffer.getInt(0);
  }
  @Override int readInt8()
  {
    return buffer.get(0);
  }
  @Override int readInt16()
  {
    return buffer.getShort(0);
  }
  @Override
  String readUTF8(Uint8Array buff, int p, int l)
  {
    byte[] data = new byte[buff.getByteLength()];
    for (int n = 0; n < buff.getByteLength(); n++)
      data[n] = (byte)buff.intAt(p + n);
    return new String(data, StandardCharsets.UTF_8);
  }
}