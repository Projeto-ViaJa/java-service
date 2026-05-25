import client.S3Provider;
import database.config.DB;
import database.model.dao.DaoFactory;
import database.model.dao.RegistroVooDao;
import entity.RegistroVoo;
import dataLoader.reader.ExcelRegistroVooReader;
import dataLoader.service.RegistroVooService;
import exceptions.DbException;
import logger.SlackService;
import org.slf4j.Logger;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;

import logger.AppLogger;

public class Main {

    public static void main(String[] args) {
        String webhook = "";
        SlackService slack = new SlackService(webhook);
        String nomeArquivo = null;

        File file = new File("dados_registros_voo_10_anos.xlsx");

        if (file.exists()) {
            if (file.delete()) {
                AppLogger.info("S3", "Arquivo local encontrado e deletado",
                        "Arquivo: " + file.getName() + " removido antes do download");

            } else {
                AppLogger.error("s3", "Falha ao deletar o arquivo local",
                        "Verifique permissões ou locks do SO " + "para:" + file.getName());
            }
        }

        //Instanciando o cliente S3 via S3Provider
        S3Client s3Client = new S3Provider().getS3Client();
        String bucketName = "2026-viaja";

         //  Fazendo download de arquivos
        try {
        ListObjectsRequest requisicao = ListObjectsRequest.builder()
        .bucket(bucketName)
                    .build();
            List<S3Object> objects = s3Client.listObjects(requisicao).contents();
            for (S3Object object : objects) {
                AppLogger.info("S3","Iniciando download do arquivo","Nome do arquivo: " + object.key());
                GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(object.key())
                        .build();

                      InputStream inputStream = s3Client.getObject(getObjectRequest, ResponseTransformer.toInputStream());
              Files.copy(inputStream, new File(object.key()).toPath());
              AppLogger.info("S3", "Arquivo baixado com sucesso", "Objeto S3: " + object.key());
              nomeArquivo = object.key();
          }
        } catch (IOException | S3Exception e) {
                AppLogger.error("S3", "Falha no download dos arquivos", e);
        }

        ExcelRegistroVooReader reader = new ExcelRegistroVooReader();
        RegistroVooService service = new RegistroVooService();

        System.out.println("\n=== TEST: Iniciando tentativa de extrair dados ===");
        List<RegistroVoo> registrosVoo = service.filtrarBrasil(
                reader.extrairRegistros(nomeArquivo)
        );
        System.out.println("\n=== TEST: Finalizado tentativa de extrair dados ===");

        try {
            AppLogger.info("DATABASE", "Carregando propriedades de conexão",
                    "Lendo db.properties via FileInputStream.");
            slack.enviarMensagem("Database", "DATABASE - Carregando propriedades de conexão", "INFO");

            RegistroVooDao registroVooDao = DaoFactory.createRegistroVooDao();
            AppLogger.info("","Conexão com o banco de dados estabelecida","getConnection() bem-sucedido.");
            slack.enviarMensagem("Database", "DATABASE - Conexão com o banco de dados estabelecida ", "INFO");

            registroVooDao.insert(registrosVoo);

        } catch (DbException e) {
            AppLogger.error("DATABASE","Falaha ao conectar ao banco de dados", "Erro: " + e);
            slack.enviarMensagem("Database", "DATABASE - Falaha ao conectar ao banco de dados ", "ERROR");
            throw new RuntimeException(e);
        }
    }
}
