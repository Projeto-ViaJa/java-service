package entity;

public class RegistroVoo {
    private int ano;
    private int mes;
    private String aeroportoOrigemUf;
    private String aeroportoOrigemRegiao;
    private String aeroportoOrigemLocalidade;
    private String aeroportoDestinoUf;
    private String aeroportoDestinoRegiao;
    private String aeroportoDestinoLocalidade;
    private String natureza;
    private String grupoVoo;
    private int passageirosPagos;
    private int passageirosGratis;
    private long ask;
    private long rpk;
    private long atk;
    private long rtk;
    private int decolagens;
    private int assentos;

    public RegistroVoo() {}

    public RegistroVoo(int ano, int mes, String aeroportoOrigemUf, String aeroportoOrigemRegiao, String aeroportoOrigemLocalidade, String aeroportoDestinoUf, String aeroportoDestinoRegiao, String aeroportoDestinoLocalidade, String natureza, String grupoVoo, int passageirosPagos, int passageirosGratis, long ask, long rpk, long atk, long rtk, int decolagens, int assentos) {
        this.ano = ano;
        this.mes = mes;
        this.aeroportoOrigemUf = aeroportoOrigemUf;
        this.aeroportoOrigemRegiao = aeroportoOrigemRegiao;
        this.aeroportoOrigemLocalidade = aeroportoOrigemLocalidade;
        this.aeroportoDestinoUf = aeroportoDestinoUf;
        this.aeroportoDestinoRegiao = aeroportoDestinoRegiao;
        this.aeroportoDestinoLocalidade = aeroportoDestinoLocalidade;
        this.natureza = natureza;
        this.grupoVoo = grupoVoo;
        this.passageirosPagos = passageirosPagos;
        this.passageirosGratis = passageirosGratis;
        this.ask = ask;
        this.rpk = rpk;
        this.atk = atk;
        this.rtk = rtk;
        this.decolagens = decolagens;
        this.assentos = assentos;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public String getAeroportoOrigemUf() {
        return aeroportoOrigemUf;
    }

    public void setAeroportoOrigemUf(String aeroportoOrigemUf) {
        this.aeroportoOrigemUf = aeroportoOrigemUf;
    }

    public String getAeroportoOrigemRegiao() {
        return aeroportoOrigemRegiao;
    }

    public void setAeroportoOrigemRegiao(String aeroportoOrigemRegiao) {
        this.aeroportoOrigemRegiao = aeroportoOrigemRegiao;
    }

    public String getAeroportoOrigemLocalidade() {
        return aeroportoOrigemLocalidade;
    }

    public void setAeroportoOrigemLocalidade(String aeroportoOrigemLocalidade) {
        this.aeroportoOrigemLocalidade = aeroportoOrigemLocalidade;
    }

    public String getAeroportoDestinoUf() {
        return aeroportoDestinoUf;
    }

    public void setAeroportoDestinoUf(String aeroportoDestinoUf) {
        this.aeroportoDestinoUf = aeroportoDestinoUf;
    }

    public String getAeroportoDestinoRegiao() {
        return aeroportoDestinoRegiao;
    }

    public void setAeroportoDestinoRegiao(String aeroportoDestinoRegiao) {
        this.aeroportoDestinoRegiao = aeroportoDestinoRegiao;
    }

    public String getAeroportoDestinoLocalidade() {
        return aeroportoDestinoLocalidade;
    }

    public void setAeroportoDestinoLocalidade(String aeroportoDestinoLocalidade) {
        this.aeroportoDestinoLocalidade = aeroportoDestinoLocalidade;
    }

    public String getNatureza() {
        return natureza;
    }

    public void setNatureza(String natureza) {
        this.natureza = natureza;
    }

    public String getGrupoVoo() {
        return grupoVoo;
    }

    public void setGrupoVoo(String grupoVoo) {
        this.grupoVoo = grupoVoo;
    }

    public int getPassageirosPagos() {
        return passageirosPagos;
    }

    public void setPassageirosPagos(int passageirosPagos) {
        this.passageirosPagos = passageirosPagos;
    }

    public int getPassageirosGratis() {
        return passageirosGratis;
    }

    public void setPassageirosGratis(int passageirosGratis) {
        this.passageirosGratis = passageirosGratis;
    }

    public long getAsk() {
        return ask;
    }

    public void setAsk(long ask) {
        this.ask = ask;
    }

    public long getRpk() {
        return rpk;
    }

    public void setRpk(long rpk) {
        this.rpk = rpk;
    }

    public long getAtk() {
        return atk;
    }

    public void setAtk(long atk) {
        this.atk = atk;
    }

    public long getRtk() {
        return rtk;
    }

    public void setRtk(long rtk) {
        this.rtk = rtk;
    }

    public int getDecolagens() {
        return decolagens;
    }

    public void setDecolagens(int decolagens) {
        this.decolagens = decolagens;
    }

    public int getAssentos() {
        return assentos;
    }

    public void setAssentos(int assentos) {
        this.assentos = assentos;
    }

    @Override
    public String toString() {
        return "RegistroVoo{" +
                "ano=" + ano +
                ", mes=" + mes +
                ", aeroportoOrigemUf='" + aeroportoOrigemUf + '\'' +
                ", aeroportoOrigemRegiao='" + aeroportoOrigemRegiao + '\'' +
                ", aeroportoOrigemLocalidade='" + aeroportoOrigemLocalidade + '\'' +
                ", aeroportoDestinoUf='" + aeroportoDestinoUf + '\'' +
                ", aeroportoDestinoRegiao='" + aeroportoDestinoRegiao + '\'' +
                ", aeroportoDestinoLocalidade='" + aeroportoDestinoLocalidade + '\'' +
                ", natureza='" + natureza + '\'' +
                ", grupoVoo='" + grupoVoo + '\'' +
                ", passageirosPagos=" + passageirosPagos +
                ", passageirosGratis=" + passageirosGratis +
                ", ask=" + ask +
                ", rpk=" + rpk +
                ", atk=" + atk +
                ", rtk=" + rtk +
                ", decolagens=" + decolagens +
                ", assentos=" + assentos +
                '}';
    }
}
