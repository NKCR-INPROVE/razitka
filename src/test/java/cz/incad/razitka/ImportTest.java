package cz.incad.razitka;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFPictureData;
import org.apache.poi.xssf.usermodel.XSSFShape;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

/**
 *
 */

public class ImportTest {
    @Test
    public void testImport() {
        try {
            FileInputStream file = new FileInputStream(new File("/Users/vlahoda/.razitka/import/razitka.xlsx"));

            //Create Workbook instance holding reference to .xlsx file
            XSSFWorkbook workbook = new XSSFWorkbook(file);

            //Get first/desired sheet from the workbook
            XSSFSheet sheet = workbook.getSheetAt(0);

            //get list of pictures
            List<XSSFPictureData> allPictures = workbook.getAllPictures();
            System.out.println("allPictures:"+allPictures.size());

            //Iterate through each rows one by one
            Iterator<Row> rowIterator = sheet.iterator();
            int rowNo = -2;
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                rowNo++;
                //if (rowNo >200) break;
                if (rowNo == -1) continue;
                //For each row, iterate through all the columns
                Iterator<Cell> cellIterator = row.cellIterator();

                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();

                    //Check the cell type and format accordingly
                    switch (cell.getColumnIndex()) {
                        case 0:  //SIGNATURA
                            String signatura = cell.getStringCellValue().replaceAll(";\\s*","\n").trim();
                            //System.out.print(cell.getColumnIndex()+":"+signatura);
                            break;
                        case 1: //SYS
                            String sys = cell.getStringCellValue().replaceAll(";\\s*","\n").trim();
                            //System.out.print(cell.getColumnIndex()+":"+sys);
                            break;
                        case 2: //NAPIS
                            String napis = cell.getStringCellValue();
                            //System.out.print(cell.getColumnIndex()+":"+napis);
                            break;

                        case 3: //OBRAZEK
                            String obr = cell.getStringCellValue();
                            System.out.print(cell.getColumnIndex()+":"+obr);
                            break;
                        case 4: //DRUH
                            String druh = cell.getStringCellValue().replaceAll(";","").trim();
                            //System.out.print(cell.getColumnIndex()+":"+druh);
                            break;
                        case 5: //PRIJMENI
                            String prijmeni = cell.getStringCellValue().replaceAll(";","").trim();
                            //System.out.print(cell.getColumnIndex()+":"+prijmeni);
                            break;
                        case 6: //INSTIRUCE
                            String instituce = cell.getStringCellValue().replaceAll(";","").trim();
                            //System.out.print(cell.getColumnIndex()+":"+instituce);
                            break;
                        case 7: //OBECNE
                            String obecne = cell.getStringCellValue().replaceAll(";","").trim();
                            //System.out.print(cell.getColumnIndex()+":"+obecne);
                            break;
                        case 8: //MESTO
                            String mesto = cell.getStringCellValue().replaceAll(";","").trim();
                            //System.out.print(cell.getColumnIndex()+":"+mesto);
                            break;
                    }
                }
                System.out.println("picture:"+rowNo);
                XSSFPictureData pict =  allPictures.get(rowNo);
                String ext = pict.suggestFileExtension();
                byte[] data = pict.getData();
                if (ext.equals("jpeg")) {
                    FileOutputStream out = new FileOutputStream("pict.jpg");
                    out.write(data);
                    out.close();
                }
                if (rowNo == allPictures.size()-1) break;

            }
            System.out.println("KONEC:"+rowNo);
            file.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private static class CellInfo{
        public Integer imageIndex;
        public Integer row1;
        public Integer row2;
    }


    @Test
    public void testImportPict() {
        try {
            FileInputStream file = new FileInputStream(new File("/Users/vlahoda/.razitka/import/razitkaopravena.xlsx"));

            //Create Workbook instance holding reference to .xlsx file
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            XSSFSheet sheet = workbook.getSheetAt(0);
            XSSFDrawing drawing = sheet.getDrawingPatriarch();
            TreeMap<Integer, Integer> rowsToIndexes = new TreeMap<Integer, Integer>();

            List<CellInfo> problematic = new ArrayList<CellInfo>();
            int i = 0;
            System.out.println("Shapes: "+drawing.getShapes().size());
            for (XSSFShape shape : drawing.getShapes()) {
                XSSFClientAnchor anchor = (XSSFClientAnchor) shape.getAnchor();

                CellInfo cellInfo = new CellInfo();
                cellInfo.imageIndex = i;
                cellInfo.row1 = anchor.getRow1();
                cellInfo.row2 = anchor.getRow2();
                if (cellInfo.row1.equals(cellInfo.row2)){
                    if(rowsToIndexes.containsKey(cellInfo.row1)){
                        System.out.println("Duplicate correct cell info "+cellInfo.row1);
                    } else{
                        rowsToIndexes.put(cellInfo.row1, cellInfo.imageIndex);
                    }
                }else{
                    problematic.add(cellInfo);
                    System.out.println("Problematic: "+cellInfo.row1+ " "+ cellInfo.row2);
                }
                i++;
            }
            for (CellInfo cellInfo : problematic) {
                if(rowsToIndexes.containsKey(cellInfo.row1)){
                    Integer existing = rowsToIndexes.put(cellInfo.row2, cellInfo.imageIndex);
                    if (existing != null){
                        System.out.println("Duplicate upper cell info "+cellInfo.row2);
                    }
                } else if(rowsToIndexes.containsKey(cellInfo.row2)){
                    Integer existing = rowsToIndexes.put(cellInfo.row1, cellInfo.imageIndex);
                    if (existing != null){
                        System.out.println("Duplicate lower cell info "+cellInfo.row1);
                    }
                } else {
                    System.out.println("Duplicate dual cell info "+cellInfo.row1);
                }
            }


            System.out.println("Mapped: "+rowsToIndexes.size());
            System.out.println("First: "+rowsToIndexes.firstKey() + " "+ rowsToIndexes.get(rowsToIndexes.firstKey()));
            System.out.println("Last: "+rowsToIndexes.lastKey());


            List<XSSFPictureData> allPictures = workbook.getAllPictures();
            System.out.println("ALLPictures: "+allPictures.size());

            for (int p = 0; p<allPictures.size();p++) {
                XSSFPictureData pict =  allPictures.get(p);

                String ext = pict.suggestFileExtension();
                byte[] data = pict.getData();
                File outFile = null;
                if (ext.equals("jpeg")) {
                    outFile = new File("files","pict"+p+".jpg");
                } else if (ext.equals("png")) {
                    outFile = new File("files","pict"+p+".png");
                } else {
                    outFile = new File("files","pict"+p+".emf");
                }
                if (outFile != null) {
                    FileOutputStream out = new FileOutputStream(outFile);
                    out.write(data);
                    out.close();
                }
            }
        } catch (Throwable t) {
            System.out.println(t);
        }
    }


}
