package dataLoader.service;

import entity.RegistroVoo;
import logger.AppLogger;

import java.util.ArrayList;
import java.util.List;

public class RegistroVooService {

    public List<RegistroVoo> filtrarBrasil(List<RegistroVoo> listaBruta) {

        AppLogger.info("ETL","Iniciando filtro de registros por UF brasileira.",
                "Total de registros recebidos antes do filtro: " + listaBruta.size());

        List<RegistroVoo> listaFiltrada = listaBruta.stream()
                .filter(r -> ehUfBrasil(r.getAeroportoOrigemUf()))
                .filter(r -> ehUfBrasil(r.getAeroportoDestinoUf()))
                .toList();

        int removidos = listaBruta.size() - listaFiltrada.size();

        if (removidos > 0){

            AppLogger.warning("ETL","Registros removidos por UF inválida ou nula.",
                    removidos + " Registros descartados por UF de origem ou destino fora do padrão brasileiro");
        }

        AppLogger.info("ETL", "Filtro concluído.",
                "Registros após filtro: " + listaFiltrada.size() + "( de " + listaBruta.size() + ")");

        return listaFiltrada;
    }

    private boolean ehUfBrasil(String uf) {
        return uf != null && uf.matches(
                "AC|AL|AP|AM|BA|CE|DF|ES|GO|MA|MT|MS|MG|PA|PB|PR|PE|PI|RJ|RN|RS|RO|RR|SC|SP|SE|TO"
        );
    }
}
