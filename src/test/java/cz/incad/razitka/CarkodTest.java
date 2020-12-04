package cz.incad.razitka;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

public class CarkodTest {
    @Test
    public void testVDK(){
        try {
            URL searchURL = new URL("http://195.113.132.165:8080/vdk/api/search/query?q=signatura:"+"\\\"RF F 005814"+"\\\"");
            HttpURLConnection conn = (HttpURLConnection) searchURL.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("GET");
            conn.setUseCaches(false);
            conn.addRequestProperty("Content-Type", "application/json;charset=utf-8");
            long start = System.currentTimeMillis();
            conn.connect();
            long connectEnd = System.currentTimeMillis();

            BufferedReader reader = null;
            if ("gzip".equals(conn.getContentEncoding())) {
                reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(conn.getInputStream()), "UTF-8"));
            } else {
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            }



            StringBuffer result = new StringBuffer();
            String line = "";
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            reader.close();
            conn.disconnect();

            System.out.println("Searched "  + " in " + (System.currentTimeMillis() - connectEnd) + " Connect time: " + (connectEnd - start));
            System.out.println(result);
            Pattern pattern = Pattern.compile("carkod\":\"(.*?)\",");
            Matcher matcher = pattern.matcher(result.toString());
            if (matcher.find())
            {
                System.out.println(matcher.group(1));
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
