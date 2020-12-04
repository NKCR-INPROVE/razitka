package cz.incad.razitka;

import cz.incad.razitka.server.Structure;
import org.aplikator.client.shared.data.ClientContext;
import org.aplikator.client.shared.data.FunctionResult;
import org.aplikator.client.shared.data.FunctionResultStatus;
import org.aplikator.client.shared.descriptor.QueryDescriptorDTO;
import org.aplikator.server.data.Context;
import org.aplikator.server.data.Executable;
import org.aplikator.server.data.Record;
import org.aplikator.server.descriptor.WizardPage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;


public class KonverzeCarkod extends Executable {
    private static final String NAME = "KonverzeCarkod";

    Logger logger = Logger.getLogger(KonverzeCarkod.class.getName());


    private QueryDescriptorDTO queryDescriptor;

    @Override
    public FunctionResult execute(Record currentRecord, Record wizardParameters, ClientContext clientContext, Context context) {
        try {

            logger.info("STARTED READING RECORDS ");
            for (Record kniha : context.getRecords(Structure.Kniha).withQuery(Structure.Kniha.sys.NULL().OR(Structure.Kniha.carkod.NULL())).list()) {
                String sysno = kniha.getStringValue(Structure.Kniha.sys, context);
                String carkodOrig = kniha.getStringValue(Structure.Kniha.carkod, context);
                String signatura = kniha.getStringValue(Structure.Kniha.signatura, context);
                logger.info("RECORD:" + kniha.getPrimaryKey().getId() + " sys: " + sysno+ "carkod: "+carkodOrig + " signatura:"+signatura);
                String carkod = findCarkodSig(sysno);
//                    Record editovanaKniha = kniha.clone();
//                    editovanaKniha.setValue(Structure.Kniha.carkod, carkod);
//                    context.addUpdatedRecordToContainer(kniha, editovanaKniha);
//
//                context.processRecordContainer();
//                context.clearRecordContainer();
            }
            logger.info("FINISHED READING RECORDS");

            return new FunctionResult("Konvertovano", FunctionResultStatus.SUCCESS);
        } catch (Throwable t) {

            return new FunctionResult("Konverze carovych kodu nebyla spuštěna: " + t, FunctionResultStatus.ERROR);
        }

    }

    @Override
    public WizardPage getWizardPage(String currentPage, boolean forwardFlag, Record currentRecord, Record wizardParameters, ClientContext clientContext, Context context) {
        return null;
    }

    private String findCarkod(String sysno){
        try {
            URL searchURL = new URL("http://195.113.132.165:8080/vdk/api/search/query?q=id:*"+sysno);
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

            logger.info("Searched " +sysno + " in " + (System.currentTimeMillis() - connectEnd) + " Connect time: " + (connectEnd - start));
            //System.out.println(result);
            Pattern pattern = Pattern.compile("carkod\":\"(.*?)\",");
            Matcher matcher = pattern.matcher(result.toString());
            if (matcher.find())
            {
                String carkod = matcher.group(1);
                logger.info("Found"+carkod);
                return carkod;
            }


        } catch (Exception e) {
            logger.log(Level.INFO,"Error in barcode search:",e);
        }
        return "";
    }

    private String findCarkodSig(String sig){
        try {
            URL searchURL = new URL("http://195.113.132.165:8080/vdk/api/search/query?q=signatura:\""+sig+"\"");
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

            logger.info("Searched " +sig + " in " + (System.currentTimeMillis() - connectEnd) + " Connect time: " + (connectEnd - start));
            //System.out.println(result);
            Pattern pattern = Pattern.compile("carkod\":\"(.*?)\",");
            Matcher matcher = pattern.matcher(result.toString());
            if (matcher.find())
            {
                String carkod = matcher.group(1);
                logger.info("Found"+carkod);
                return carkod;
            }


        } catch (Exception e) {
            logger.log(Level.INFO,"Error in barcode search:",e);
        }
        return "";
    }


}

