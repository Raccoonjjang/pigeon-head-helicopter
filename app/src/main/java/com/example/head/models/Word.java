package com.example.head.models;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class Word {
    private Context context;
    private List<String> data;

    public Word(Context context) {
        this.context = context;
        this.data = new ArrayList<>();
        readExcel();  // 생성자에서 엑셀 파일을 읽어 데이터 초기화
    }

    private void readExcel() {
        try {
            InputStream is = context.getResources().getAssets().open("sentence.xls");
            Workbook wb = Workbook.getWorkbook(is);

            if (wb != null) {
                Sheet sheet = wb.getSheet("Sheet1");

                if (sheet != null) {
                    int rowTotal = sheet.getRows();
                    Log.d("TAG", "Reading sheet: Sheet1, total rows: " + rowTotal);

                    for (int row = 1; row < rowTotal; row++) { // 첫 번째 행은 생략
                        String contentsA = sheet.getCell(0, row).getContents(); // A 열

                        if (!contentsA.isEmpty()) {
                            data.add(contentsA);
                        }
                    }
                } else {
                    Log.d("TAG", "Sheet not found: Sheet1");
                }
            } else {
                Log.d("TAG", "Workbook is null");
            }
        } catch (IOException | BiffException e) {
            Log.e("TAG", "Error reading excel file", e);
        }
    }

    // 단어 리스트를 반환하는 메서드
    public List<String> getWords() {
        return data;
    }
}
