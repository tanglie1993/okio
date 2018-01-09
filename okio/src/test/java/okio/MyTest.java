package okio;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.*;

/**
 * Created by pc on 2018/1/6.
 */
public class MyTest {

    @Test
    public void segmentPollTakeTimeTest() throws Exception {
        Buffer bufferA = new Buffer();
        StringBuffer stringBuffer = new StringBuffer();
        for(int i = 0; i < 10000; i++){
            stringBuffer.append("aaaa");
        }
        for(int i =0; i < 100000; i++){
            bufferA.writeUtf8(stringBuffer.toString());
            bufferA.readUtf8();
        }
        System.out.println(""+(double) SegmentPool.totalPooledTakesDuration / (double) SegmentPool.totalPooledTakes);
        System.out.println(""+SegmentPool.totalPooledTakes);
        System.out.println(""+(double) SegmentPool.totalNewTakesDuration / (double) SegmentPool.totalNewTakes);
        System.out.println(""+SegmentPool.totalNewTakes);
    }

    @Test
    public void readEfficiencyTest() throws Exception {

        long okioReadTime = 0;
        long ioReadTime = 0;
        for(int i = 0; i < 200; i++){
            Buffer buffer = new Buffer();
            long startTime = System.currentTimeMillis();
            BufferedSource source = Okio.buffer(Okio.source(new File("D:\\workspace\\sss.txt")));
            source.readUtf8();
            source.close();
            okioReadTime += (System.currentTimeMillis() - startTime);


            String file = "D:\\workspace\\sss.txt";
            startTime = System.currentTimeMillis();
            DataInputStream reader = new DataInputStream(new FileInputStream(file));
            byte[] bytes = new byte[reader.available()];
            reader.read(bytes);
            String ioString = new String(bytes);
            reader.close();
            ioReadTime += (System.currentTimeMillis() - startTime);
        }
        System.out.println(okioReadTime);
        System.out.println(ioReadTime);
    }

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void writeEfficiencyTest() throws Exception {

        long okioWriteTime = 0;
        long ioWriteTime = 0;


        BufferedSource source = Okio.buffer(Okio.source(new File("D:\\workspace\\sss.txt")));
        String content = source.readUtf8();
        source.close();

        for(int i = 0; i < 20; i++){
            long startTime = System.currentTimeMillis();
            BufferedSink sink = Okio.buffer(Okio.sink(temporaryFolder.newFile()));
            sink.writeUtf8(content);
            sink.close();
            okioWriteTime += (System.currentTimeMillis() - startTime);

            startTime = System.currentTimeMillis();
            FileWriter fw = new FileWriter("D:\\workspace\\ssss.txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.append(content);
            bw.close();
            fw.close();
            ioWriteTime += (System.currentTimeMillis() - startTime);
            new File("D:\\workspace\\ssss.txt").delete();
        }
        System.out.println(okioWriteTime);
        System.out.println(ioWriteTime);
    }
}
