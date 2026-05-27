package client;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

public class S3Provider {

    private AwsCredentialsProvider credentials;

    public S3Provider() {
            this.credentials =  DefaultCredentialsProvider.create();
    }

    public S3Client getS3Client() {
        try {
            return S3Client.builder()
                    .region(Region.US_EAST_1)
                    .credentialsProvider(credentials)
                    .build();
        } catch (Exception e) {
            System.out.println("ERROR: Falha ao criar S3Client com SSL customizado. Tentando sem customização...");
            e.printStackTrace();

            // Fallback: criar sem customização
            return S3Client.builder()
                    .region(Region.US_EAST_1)
                    .credentialsProvider(credentials)
                    .build();
        }
    }
}
