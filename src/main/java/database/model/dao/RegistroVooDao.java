package database.model.dao;

import entity.RegistroVoo;

import java.util.List;

public interface RegistroVooDao {
    void insert(List<RegistroVoo> registroVooList);

    void truncateRegistroVoo();
}
