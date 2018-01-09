package okio;

import org.junit.Test;

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
        for(int i = 0; i < 2000; i++){
            Buffer buffer = new Buffer();
            long startTime = System.currentTimeMillis();
            buffer.readFrom(new FileInputStream("D:\\workspace\\sss.txt"));
            okioReadTime += (System.currentTimeMillis() - startTime);



            String file = "D:\\workspace\\sss.txt";
            startTime = System.currentTimeMillis();
            DataInputStream reader = new DataInputStream(new FileInputStream(file));
            reader.read(new byte[reader.available()]);
            reader.close();
            ioReadTime += (System.currentTimeMillis() - startTime);
        }
        System.out.println(okioReadTime);
        System.out.println(ioReadTime);
    }
}
