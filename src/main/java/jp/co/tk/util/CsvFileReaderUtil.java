package jp.co.tk.util;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * CSVファイル生成・読み込みの処理を定義します。
 */
@Slf4j
public final class CsvFileReaderUtil {

    /**
     * オフセットと最大ファイル読み込み値を設定しながらファイルを読みます。
     *
     * @param fileName  ファイル名
     * @param delimiter デリミタ
     * @param limit     最大値
     * @param offset    何行目から読むかの値
     * @return 行, カンマでスプリットされたマップデータ
     * @throws IOException
     */
    public static Map<Integer, List<String>> readLimitOffset(final String fileName, final String delimiter,
                                                             final int limit, final int offset) throws IOException {

        final Map<Integer, List<String>> returnVal = new HashMap<>();
        try (final LineNumberReader lr = new LineNumberReader(new FileReader(fileName))) {
            lr.setLineNumber(offset); //読み込む行を設定
            String line;
            int lineCnt = 0;
            while ((line = lr.readLine()) != null) {
                final String[] splitedData = line.split(delimiter);
                final List<String> readData = new ArrayList<>();
                readData.addAll(Arrays.asList(splitedData));
                returnVal.put(lineCnt, readData);
                lineCnt++;
                if (limit == lineCnt) { // 読み込んだ行数がlimitと同じになったらLoopを抜ける
                    break;
                }
            }
        }

        return returnVal;
    }

    /**
     * ファイルの行数をカウントします。
     *
     * @param fileName ファイル名
     * @return 行数
     * @throws IOException
     */
    public static long countLines(final String fileName) throws IOException {
        final Path file = Paths.get(fileName);
        return Files.lines(file).count();
    }

}
