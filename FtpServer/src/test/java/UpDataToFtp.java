import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.hzgc.ftpserver.util.Download;

import java.io.File;
import java.util.Random;


public class UpDataToFtp {
    private static MetricRegistry metric = new MetricRegistry();
    private final static Counter counter = metric.counter("counter");

    /**
     *
     * @param path 文件路径
     * @param loopNum 循环次数
     * @param IpcId 设备ID
     */
    public static void upDataTest(String path, int loopNum, String IpcId) {
        File file = new File(path);
        File[] tempList = file.listFiles();
        for (int i = 0; i < loopNum; i++) {
            Random random = new Random();
            int randNum = random.nextInt(10000000);
            String randPath = String.valueOf(randNum);
            for (int j = 0; j < (tempList != null ? tempList.length : 0); j++) {
                if (tempList[j].isFile()) {
                    String orginFileName = tempList[j].getAbsolutePath();
                    String fileName = tempList[j].getName();
                    System.out.println(fileName);
                    System.out.println(tempList[j].getAbsoluteFile());
                    StringBuilder filePath = new StringBuilder();
                    //拼装路径
                    filePath = filePath.append(IpcId).append("/").append(tempList[j].getName().substring(0, 13).replaceAll("_", "/")).append(randPath);
                    System.out.println(filePath);
                    Download.upLoadFromProduction("172.18.18.103", 2121, "admin", "123456", "", filePath.toString(), fileName, orginFileName);
                    counter.inc();
                    System.out.println(counter.getCount());
                }
            }
        }
        System.out.println("上传的文件数量：" + counter.getCount());
    }
}
