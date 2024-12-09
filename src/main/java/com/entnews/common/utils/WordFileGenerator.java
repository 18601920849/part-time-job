package com.entnews.common.utils;

import cn.hutool.core.io.resource.ClassPathResource;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.xmlbeans.XmlCursor;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class WordFileGenerator {
    public static void generateWord(String templatePath, String outputPath, List<Map<String, String>> dataList, Map<String, String> commonDataMap){
        try (InputStream fis = new ClassPathResource(templatePath).getStream();
             XWPFDocument document = new XWPFDocument(fis)) {
            // 填充模板中的固定部分（根据Map中的数据）
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                String paragraphText = paragraph.getText();
                for (Map.Entry<String, String> entry : commonDataMap.entrySet()) {
                    paragraphText = paragraphText.replace("${" + entry.getKey() + "}", entry.getValue());
                }
                List<XWPFRun> runs = paragraph.getRuns();
                if (runs!= null &&!runs.isEmpty()) {
                    for (int i = 0; i < runs.size(); i++) {
                        XWPFRun run = runs.get(i);
                        String runText = run.text();
                        for (Map.Entry<String, String> entry : commonDataMap.entrySet()) {
                            runText = runText.replace("${" + entry.getKey() + "}", entry.getValue());
                        }
                        run.setText(runText, 0);
                    }
                } else {
                    paragraph.createRun().setText(paragraphText);
                }
            }
            int index = 0;
            // 处理模板中的循环部分（根据List中的数据）
            XWPFParagraph testParagraph = document.getParagraphs().get(5);
            XWPFParagraph summaryParagraph = document.getParagraphs().get(6);
            for (int i = 0; i < dataList.size(); i++){
                if (i == 0){
                    String testText = testParagraph.getText();
                    String summaryText = summaryParagraph.getText();
                    List<XWPFRun> runs = testParagraph.getRuns();
                    if (runs!= null &&!runs.isEmpty()) {
                        for (int j = 0; j < runs.size(); j++) {
                            XWPFRun run = runs.get(j);
                            String runText = run.text();
                            for (Map.Entry<String, String> entry : dataList.get(i).entrySet()) {
                                runText = runText.replace("${" + entry.getKey() + "}", entry.getValue());
                            }
                            run.setText(runText, 0);
                        }
                    } else {
                        testParagraph.createRun().setText(testText);
                    }
                    List<XWPFRun> runs2 = summaryParagraph.getRuns();
                    if (runs2!= null &&!runs2.isEmpty()) {
                        for (int j = 0; j < runs2.size(); j++) {
                            XWPFRun run = runs2.get(j);
                            String runText = run.text();
                            for (Map.Entry<String, String> entry : dataList.get(i).entrySet()) {
                                runText = runText.replace("${" + entry.getKey() + "}", entry.getValue());
                            }
                            run.setText(runText, 0);
                        }
                    } else {
                        testParagraph.createRun().setText(summaryText);
                    }
                    index = 6;
                }else {
                    XmlCursor testCursor = document.getParagraphArray(index+1).getCTP().newCursor();
                    XWPFParagraph testParagraph2 = document.insertNewParagraph(testCursor);
                    copyParagraphStyle(testParagraph, testParagraph2);
                    XmlCursor  summaryCursor = document.getParagraphArray(index+2).getCTP().newCursor();
                    XWPFParagraph  summaryParagraph2 = document.insertNewParagraph(summaryCursor);
                    copyParagraphStyle(summaryParagraph, summaryParagraph2);
                    String testText = testParagraph2.getText();
                    String summaryText = summaryParagraph2.getText();
                    index = index+2;
                    List<XWPFRun> runs = testParagraph2.getRuns();
                    if (runs!= null &&!runs.isEmpty()) {
                        for (int j = 0; j < runs.size(); j++) {
                            XWPFRun run = runs.get(j);
                            String runText = run.text();
                            for (Map.Entry<String, String> entry : dataList.get(i).entrySet()) {
                                runText = runText.replace("${" + entry.getKey() + "}", entry.getValue());
                            }
                            run.setText(runText, 0);
                        }
                    } else {
                        testParagraph2.createRun().setText(dataList.get(i).get("title"));
                        //设置字体大小为小二
                        testParagraph2.getRuns().get(0).setFontSize(18);
                        testParagraph2.getRuns().get(0).setFontFamily("方正小标宋简体");
                    }
                    List<XWPFRun> runs2 = summaryParagraph2.getRuns();
                    if (runs2!= null &&!runs2.isEmpty()) {
                        for (int j = 0; j < runs2.size(); j++) {
                            XWPFRun run = runs2.get(j);
                            String runText = run.text();
                            for (Map.Entry<String, String> entry : dataList.get(i).entrySet()) {
                                runText = runText.replace("${" + entry.getKey() + "}", entry.getValue());
                            }
                            run.setText(runText, 0);
                        }
                    } else {
                        summaryText = dataList.get(i).get("summary")+"（来源：";
                        summaryParagraph2.createRun().setText(summaryText);
                        summaryParagraph2.getRuns().get(0).setFontFamily("仿宋_GB2312");
                        // 设置字号为三号（大约16磅）
                        summaryParagraph2.getRuns().get(0).setFontSize(16);
                        summaryParagraph2.createRun().setText(dataList.get(i).get("sourceurl"));
                        summaryParagraph2.getRuns().get(1).setFontFamily("Times New Roman");
                        // 设置字号为三号（大约16磅）
                        summaryParagraph2.getRuns().get(1).setFontSize(16);
                        summaryParagraph2.createRun().setText("）");
                        summaryParagraph2.getRuns().get(2).setFontFamily("仿宋_GB2312");
                        // 设置字号为三号（大约16磅）
                        summaryParagraph2.getRuns().get(2).setFontSize(16);
                        summaryParagraph2.setIndentationFirstLine(567);

                    }

                }
            }

            try (FileOutputStream fos = new FileOutputStream(outputPath)) {
                document.write(fos);
            }

            System.out.println("Word文件生成成功！");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 复制段落样式的方法
    private static void copyParagraphStyle(XWPFParagraph source, XWPFParagraph target) {
        target.setAlignment(source.getAlignment());
        target.setIndentationLeft(source.getIndentationLeft());
        target.setIndentationRight(source.getIndentationRight());
        target.setSpacingBefore(source.getSpacingBefore());
        target.setSpacingAfter(source.getSpacingAfter());
        target.setStyle(source.getStyle());
    }

}