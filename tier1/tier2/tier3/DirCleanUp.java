import java.util.*;
import java.io.*;


public class DirCleanUp {
	public static void main(String[] args) {
		try {
	  	if (args.length != 1) {
	  		System.out.println("Usage: DirCleanUp <root_dir>");
	  	}
	  	
	  	File root = new File(args[0]);
	  	cleanUp(root);
	  } catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void cleanUp(File root) throws Exception {
    if (root == null || !root.exists()) {
      System.out.println("dir is not exists!!");
      return;
    }
    if (root.isFile()) {
      if (root.exists() && !root.delete()) {
          root.deleteOnExit();
      }
    } else {
      // ディレクトリは再帰
      File[] list = root.listFiles();
      for ( int i = 0 ; i < list.length ; i++ ) {
        cleanUp( list[i] );
      }
    }
	}
  //
  //

  //
}