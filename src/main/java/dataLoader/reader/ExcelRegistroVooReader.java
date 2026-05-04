package dataLoader.reader;

import com.github.pjfanning.xlsx.StreamingReader;
import com.sun.tools.javac.Main;
import dataLoader.util.RegistroVooColumns;
import entity.RegistroVoo;
import logger.AppLogger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static dataLoader.util.ExcelUtils.getInt;
import static dataLoader.util.ExcelUtils.getLong;
import static dataLoader.util.ExcelUtils.getString;


// >>>> explicar classe aqui <<<<
public class ExcelRegistroVooReader {

    public List<RegistroVoo> extrairRegistros(String nomeArquivo) {

        List<RegistroVoo> registros = new ArrayList<>();

        AppLogger.info("ETL", "Iniciando abertura do arquivo Excel.", "Arquivo: " + nomeArquivo);

        try (Workbook workbook = new StreamingReader.Builder()
                .rowCacheSize(100)
                .bufferSize(4096)
                .open(new File(nomeArquivo))) {

            AppLogger.info("ETL", "Arquivo Excel aberto - Iniciando leitura de linhas",
                    "Workbook instanciado. Acessando aba indice 0.");

            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;

                try {
                    RegistroVoo r = new RegistroVoo(
                            getInt(row, RegistroVooColumns.COL_ANO),
                            getInt(row, RegistroVooColumns.COL_MES),
                            getString(row, RegistroVooColumns.COL_ORIGEM_LOCALIDADE),
                            getString(row, RegistroVooColumns.COL_ORIGEM_REGIAO),
                            getString(row, RegistroVooColumns.COL_ORIGEM_UF),
                            getString(row, RegistroVooColumns.COL_DESTINO_UF),
                            getString(row, RegistroVooColumns.COL_DESTINO_REGIAO),
                            getString(row, RegistroVooColumns.COL_DESTINO_LOCALIDADE),
                            getString(row, RegistroVooColumns.COL_NATUREZA),
                            getString(row, RegistroVooColumns.COL_GRUPO_VOO),
                            getInt(row, RegistroVooColumns.COL_PASSAGEIROS_PAGOS),
                            getInt(row, RegistroVooColumns.COL_PASSAGEIROS_GRATIS),
                            getLong(row, RegistroVooColumns.COL_ASK),
                            getLong(row, RegistroVooColumns.COL_RPK),
                            getLong(row, RegistroVooColumns.COL_ATK),
                            getLong(row, RegistroVooColumns.COL_RTK),
                            getInt(row, RegistroVooColumns.COL_DECOLAGENS),
                            getInt(row, RegistroVooColumns.COL_ASSENTOS)
                    );

                    registros.add(r);

                } catch (Exception e) {
                    AppLogger.warning("ETL", "Linha ignorada por erro de mapeamento",
                            "Linha " + row.getRowNum() + " — " + e.getMessage());

                }
            }
            AppLogger.info("ETL", "Leitura de Excel concluída.",
                    "Workbook fechado. Total de registros extraídos: " + registros.size());

        } catch (Exception e) {
            AppLogger.error("ETL", "Falha crítica ao abrir o arquivo Excel", e);
        }

        return registros;
    }
}
