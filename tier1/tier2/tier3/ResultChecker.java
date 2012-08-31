import java.util.*;
import java.io.*;
import java.text.*;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.FileUtils;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;


public class ResultChecker {
  
  private static final String BACKUP = "result_backup";
  private static final String RESULT_NAME = "result_checker";
  private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss");
  private static final String lineSeparator = System.getProperty("line.separator");
  
	public static void main(String[] args) {
		try {
	  	if (args.length != 1) {
	  		System.out.println("Usage: ResultChecker <root_dir>");
	  	}
	  	
	  	File root = new File(args[0]);
	  	check(root);
	  } catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void check(File root) throws Exception {
    if (root == null || !root.exists()) {
      System.out.println("dir is not exists!!");
      return;
    }
    Collection<File> allFiles = FileUtils.listFiles(root, null, true);
    Collection<File> errLogs = FileUtils.listFiles(root, new String[]{"log"}, true);
    
    // xxx.log を取り除く
    Iterator<File> itr = errLogs.iterator();
    while (itr.hasNext()) {
      allFiles.remove(itr.next());
    }
    
    Date date = new Date();
    Writer writer = new OutputStreamWriter(new FileOutputStream("./" + RESULT_NAME + "_" + sdf.format(date) + ".dat"));
    ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream("./" + BACKUP + "_" + sdf.format(date) + ".zip"));
    
    Object[][] ob = new Object[][]{new Object[]{">> success(result files)", allFiles}, new Object[]{">> fail(error logs)", errLogs}};
    
    for (int i = 0 ; i < 2 ; i++) {
    	String header = (String)ob[i][0];
    	Collection<File> tmpCollection = (Collection<File>)ob[i][1];
      writer.write(header + "  size:" + Integer.valueOf(tmpCollection.size()) + lineSeparator);
      
      Iterator<File> tmpItr = tmpCollection.iterator();
      int cnt1 = 1;
      while (tmpItr.hasNext()) {
        File tmp = tmpItr.next();
        
        // メッセージファイルパス、サイズをログ書出し
        StringBuilder sb = new StringBuilder();
        sb.append(" ").append(cnt1).append(": ").append(tmp.getAbsolutePath()).append(", ").append(tmp.length()).append(lineSeparator);
        writer.write(sb.toString());
        cnt1 ++;
        
        // バックアップ (zip)
        String path = StringUtils.substringAfterLast(tmp.getPath().replaceAll("\\\\", "/"), "WMQ_WORK");
        System.out.println(path);
        ZipEntry target = new ZipEntry(path);
        zipOut.putNextEntry(target);
        byte buf[] = new byte[2048];
        int count;
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(tmp));
        while ((count = in.read(buf, 0, 2048)) != -1) {
          zipOut.write(buf, 0, count);
        }
        in.close();
        zipOut.closeEntry();
        
        // メッセージファイル削除
        tmp.delete();
      }
    }
    
    zipOut.close();
    writer.flush();
    writer.close();
  }
}