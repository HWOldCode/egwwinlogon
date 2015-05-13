/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package egwwinlogon.winapi.mpr;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

/**
 * http://msdn.microsoft.com/en-us/library/windows/desktop/aa385353(v=vs.85).aspx
 */
public class NETRESOURCEA extends Structure
{
  public static final java.util.List<String> fieldOrder = java.util.Arrays.asList(new String[]
  {
    "dwScope",
    "dwType",
    "dwDisplayType",
    "dwUsage",
    "lpLocalName",
    "lpRemoteName",
    "lpComment",
    "lpProvider"
  });

  public int dwScope;
  public int dwType;
  public int dwDisplayType;
  public int dwUsage;
  public String lpLocalName;
  public String lpRemoteName;
  public String lpComment;
  public String lpProvider;

  @Override
  protected java.util.List getFieldOrder()
  {
    return fieldOrder;
  }

  public NETRESOURCEA(Pointer mem)
  {
      super(mem);
      read();
  }

  public NETRESOURCEA()
  {
      super();
  }
}