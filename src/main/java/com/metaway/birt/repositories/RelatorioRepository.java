package com.metaway.birt.repositories;

import com.metaway.birt.entities.Relatorio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RelatorioRepository extends JpaRepository<Relatorio, Long> {

    Relatorio findByHash(String hash);
}
