import client.S3Provider;
import database.model.dao.DaoFactory;
import database.model.dao.RegistroVooDao;
import logger.SlackNotifier;
import entity.RegistroVoo;
import dataLoader.reader.ExcelRegistroVooReader;
import dataLoader.service.RegistroVooService;
import exceptions.DbException;
import logger.AppLogger;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class Main {
        // Notificações agora centralizadas via SlackNotifier

    public static void main(String[] args) {
        // Notificador simples que encapsula leitura de parâmetros e envio
        SlackNotifier notifier = new SlackNotifier();

        String nomeArquivo = null;
        File file = new File("dados_registros_voo_10_anos.xlsx");

        if (file.exists()) {
            if (file.delete()) {
                AppLogger.info("S3", "Arquivo local encontrado e deletado",
                        "Arquivo: " + file.getName() + " removido antes do download");
                notifier.notify("INFO", "DATA READER", "Iniciando leitura de arquivo");
            } else {
                AppLogger.error("s3", "Falha ao deletar o arquivo local",
                        "Verifique permissões ou locks do SO para:" + file.getName());
                notifier.notify("ERROR", "DATA READER", "Falha ao deletar o arquivo local");
            }
        }

        S3Client s3Client = new S3Provider().getS3Client();
        String bucketName = "s3-viaja-arquivos";

        try {
            ListObjectsRequest requisicao = ListObjectsRequest.builder()
                    .bucket(bucketName).build();
            List<S3Object> objects = s3Client.listObjects(requisicao).contents();

            for (S3Object object : objects) {
                AppLogger.info("S3", "Iniciando download do arquivo",
                        "Nome do arquivo: " + object.key());
                notifier.notify("INFO", "DATA READER", "Iniciando download do arquivo - " + object.key());

                GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                        .bucket(bucketName).key(object.key()).build();
                ResponseInputStream<GetObjectResponse> inputStream =
                        s3Client.getObject(getObjectRequest);
                File arquivoDestino = new File(object.key());
                long tamanhoTotal = object.size();
                long bytesBaixados = 0;
                byte[] buffer = new byte[1024 * 8];

                try (FileOutputStream outputStream = new FileOutputStream(arquivoDestino)) {
                    int bytesLidos;
                    while ((bytesLidos = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesLidos);
                        bytesBaixados += bytesLidos;
                        double porcentagem = ((double) bytesBaixados / tamanhoTotal) * 100;
                        System.out.printf("Arquivo: %s | %.2f%% baixado%n",
                                object.key(), porcentagem);
                        AppLogger.info("S3", "Progresso download",
                                String.format("Arquivo: %s | %.2f%%", object.key(), porcentagem));
                    }
                }

                AppLogger.info("S3", "Arquivo baixado com sucesso",
                        "Objeto S3: " + object.key());
                        notifier.notify("INFO", "DATA READER", "Arquivo baixado com sucesso - " + object.key());
                nomeArquivo = object.key();
            }

        } catch (IOException | S3Exception e) {
            AppLogger.error("S3", "Falha no download dos arquivos", e);
            notifier.notify("ERROR", "S3", "Falha no download dos arquivos");
        }

        ExcelRegistroVooReader reader = new ExcelRegistroVooReader();
        RegistroVooService service = new RegistroVooService();

        System.out.println("\n= Iniciando tentativa de extrair dados.");
        notifier.notify("INFO", "DATA READER", "Iniciando tentativa extrair dados");

        List<RegistroVoo> registrosVoo = service.filtrarBrasil(
                reader.extrairRegistros(nomeArquivo));
        System.out.println("\n= Tentativa de extrair dados finalizada.");

        try {
            notifier.notify("INFO", "EXECUCAO", "ETL >>>>>>>>>>> Iniciado >>>>>>>>>>>");
            AppLogger.info("DATABASE", "Carregando propriedades de conexão",
                    "Lendo db.properties via FileInputStream.");
            notifier.notify("INFO", "DATABASE", "Carregando propriedades de conexão");

            RegistroVooDao registroVooDao = DaoFactory.createRegistroVooDao();
            AppLogger.info("", "Conexão com o banco de dados estabelecida",
                    "getConnection() bem-sucedido.");
            notifier.notify("INFO", "DATABASE", "Conexão com o banco de dados estabelecida");

            registroVooDao.insert(registrosVoo);

        } catch (DbException e) {
                        AppLogger.error("DATABASE", "Falha ao conectar ao banco de dados", "Erro: " + e);
                        notifier.notify("ERROR", "DATABASE", "Falha ao conectar ao banco de dados - " + e);
            throw new RuntimeException(e);
        }
    }
}
