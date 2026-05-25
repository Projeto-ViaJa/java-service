Setup Rápido - Integração Slack

## ⚡ Passos para Ativar a Integração

### 1️⃣ Criar Bot no Slack

1. Vá para https://api.slack.com/apps
2. Clique em "Create New App" → "From scratch"
3. Nome: "Viaja Bot" (ou seu nome)
4. Selecione sua workspace
5. Vá para "OAuth & Permissions"
6. Em "Scopes", adicione: `chat:write` e `chat:write.public`
7. Clique "Install to Workspace"
8. **Copie o "Bot User OAuth Token"** (começa com `xoxb-`)

### 2️⃣ Criar Canal no Slack (Opcional)

Se quiser um canal dedicado:
1. No Slack, crie um novo canal: `#banco-de-dados`
2. Adicione o bot ao canal

### 3️⃣ Executar SQL de Setup

Abra seu MySQL e execute:

```sql
-- 1. Criar a tabela SlackParams
CREATE TABLE IF NOT EXISTS SlackParams (
    id INT PRIMARY KEY AUTO_INCREMENT,
    url_slack VARCHAR(255),
    canal VARCHAR(45),
    warning BOOLEAN DEFAULT true,
    infos BOOLEAN DEFAULT true,
    error BOOLEAN DEFAULT true,
    fkUsuario INT,
    CONSTRAINT fkUser
        FOREIGN KEY (fkUsuario)
        REFERENCES usuario(id_usuario)
);

-- 2. Inserir configuração (substitua o token)
INSERT INTO SlackParams (url_slack, canal, warning, infos, error)
VALUES (
    'xoxb-SEU-BOT-TOKEN-AQUI',
    '#banco-de-dados',
    true,
    true,
    true
);
```

### 4️⃣ Pronto! 🎉

Seus logs de banco de dados agora vão para o Slack automaticamente!

## 🧪 Testar a Integração

Execute o código abaixo em algum lugar do seu projeto:

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TesteSlack {
    private static final Logger log = LoggerFactory.getLogger(TesteSlack.class);
    
    public static void main(String[] args) {
        // Se a classe estiver no pacote "database", irá para o Slack
        // Vamos fazer testes manuais:
        
        // Isso NÃO vai para Slack (não está em "database"):
        // log.info("Teste de qualquer pacote");
        
        // Para testar, você pode verificar as queries que chegam no banco
        // ou colocar alguns logs de teste:
    }
}
```

## 📊 Verificar Configuração

```sql
SELECT * FROM SlackParams;
```

Resultado esperado:
```
| id | url_slack                 | canal           | warning | infos | error | fkUsuario |
|----|---------------------------|-----------------|---------|-------|-------|-----------|
| 1  | xoxb-seu-token-aqui       | #banco-de-dados | 1       | 1     | 1     | NULL      |
```

## 🔄 Como Funciona

```
seu_codigo_log.java:
    log.error("Erro ao conectar no banco")
           ↓
Log4j2 captura (SLF4J)
           ↓
SlackAppender.append()
           ↓
SlackParamsDao.obterPrimeiroParam()
           ↓
SELECT * FROM SlackParams;
           ↓
Verifica: error = true? ✅
           ↓
Envia para Slack → #banco-de-dados ✅
```

## 🎯 Logs que Vão para Slack

Apenas logs de classes no pacote `database`:

✅ database.config.DB
✅ database.model.dao.RegistroVooDao
✅ database.model.dao.Impl.RegistroVooDaoJDBC
✅ database.DatabaseLoggingExample
✅ etc.

❌ dataLoader.reader.ExcelRegistroVooReader (não vai)
❌ client.S3Provider (não vai)
❌ notification.* (não vai)

## 🛠️ Gerenciar Configurações

Mudar canal:
```sql
UPDATE SlackParams SET canal = '#logs-criticos' WHERE id = 1;
```

Desabilitar WARNINGs:
```sql
UPDATE SlackParams SET warning = false WHERE id = 1;
```

Desabilitar tudo:
```sql
UPDATE SlackParams SET warning=false, infos=false, error=false WHERE id = 1;
```

Novo token:
```sql
UPDATE SlackParams SET url_slack = 'xoxb-novo-token' WHERE id = 1;
```

**Alterações entram em vigor IMEDIATAMENTE!**

## 🆘 Troubleshooting

| Problema | Solução |
|---|---|
| Logs não aparecem | Verifique se a tabela SlackParams tem dados |
| "Token inválido" | Copie um novo token do Slack |
| Canal não existe | Crie o canal ou use um que existe |
| Aplicação lenta | Slack não bloqueia, rodará em async |

## 📝 Exemplo de Log Real

Quando seu código fizer:

```java
package database.config;

public class DB {
    private static final Logger log = LoggerFactory.getLogger(DB.class);
    
    public static void conectar() {
        log.info("Conectando ao MySQL");
        log.error("Erro: Connection refused", exception);
    }
}
```

**No Slack aparecerá:**

```
*[INFO] 25/05/2026 14:30:45*
Logger: `database.config.DB`
Mensagem: Conectando ao MySQL

*[ERROR] 25/05/2026 14:30:46*
Logger: `database.config.DB`
Mensagem: Erro: Connection refused
Exception: `SQLException`
Motivo: Connection refused: host unreachable
```

---

**Pronto! Você tem integração total com Slack! 🚀**

Qualquer dúvida, consulte [SLACK_INTEGRATION.md](SLACK_INTEGRATION.md)
